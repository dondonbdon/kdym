package dev.bti.kdym.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.local.PlayItemDao
import dev.bti.kdym.data.local.toEntity
import dev.bti.kdym.data.local.toModel
import dev.bti.kdym.data.models.PlayComment
import dev.bti.kdym.data.models.PlayItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class PlayRepository(
    private val playItemDao: PlayItemDao? = null,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("playItems")

    // ==========================================
    // MARK: - Core Play Service
    // ==========================================

    /**
     * Listens to PlayItems. Prioritizes local cache while syncing with network updates.
     */
    fun getPlayItems(kind: String? = null): Flow<List<PlayItem>> {
        var query: Query = collection.whereEqualTo("isDeleted", false)
        if (kind != null) {
            query = query.whereEqualTo("kind", kind)
        }

        // 1. Clean Flow Pipeline (No redundant flow {} builders)
        val networkFlow = query.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(PlayItem::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    Log.e("PlayRepository", "Failed to parse PlayItem ${doc.id}", e)
                    null
                }
            }.sortedWith(playItemComparator())
        }.onEach { items ->
            // Silently cache the latest network fetch into Room
            playItemDao?.insertItems(items.map { it.toEntity() })
        }

        return if (playItemDao != null && kind != null) {
            val cachedFlow = playItemDao.getPlayItems(kind).map { entities -> entities.map { it.toModel() } }

            // 2. Fixed Combine Logic: Use flatMapLatest or a merged flow so it doesn't freeze
            // waiting for the network to respond if offline.
            flow {
                // Instantly emit the cache first
                emitAll(
                    combine(cachedFlow, networkFlow) { cached, network ->
                        if (network.isNotEmpty()) network else cached
                    }
                )
            }
        } else {
            networkFlow
        }
    }

    suspend fun save(item: PlayItem, makeOnlyFeaturedForKind: Boolean = true): String {
        val now = Timestamp.now()
        val documentId = item.id ?: collection.document().id
        val documentRef = collection.document(documentId)

        if (item.isFeatured && makeOnlyFeaturedForKind) {
            val existingFeatured = collection
                .whereEqualTo("kind", item.kind)
                .whereEqualTo("isFeatured", true)
                .whereEqualTo("isDeleted", false)
                .get().await()

            if (!existingFeatured.isEmpty) {
                firestore.runBatch { batch ->
                    for (doc in existingFeatured.documents) {
                        if (doc.id != documentId) {
                            batch.update(doc.reference, mapOf(
                                "isFeatured" to false,
                                "updatedAt" to now
                            ))
                        }
                    }
                }.await()
            }
        }

        val dataToSave = item.copy(
            id = documentId,
            updatedAt = now,
            createdAt = item.id?.let { item.createdAt } ?: now
        )

        documentRef.set(dataToSave, SetOptions.merge()).await()
        return documentId
    }

    suspend fun delete(itemId: String, deletedByUid: String?) {
        collection.document(itemId).update(
            mapOf(
                "isDeleted" to true,
                "deletedBy" to deletedByUid,
                "deletedAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )
        ).await()
    }

    suspend fun incrementPlayCount(itemId: String) {
        try {
            collection.document(itemId).update(
                mapOf(
                    "playCount" to FieldValue.increment(1),
                    "lastPlayedAt" to Timestamp.now()
                )
            ).await()
        } catch (e: Exception) {
            Log.e("PlayRepository", "Analytics update failed", e)
        }
    }

    suspend fun incrementShareCount(itemId: String) {
        try {
            collection.document(itemId).update(
                mapOf(
                    "shareCount" to FieldValue.increment(1),
                    "lastSharedAt" to Timestamp.now()
                )
            ).await()
        } catch (e: Exception) {
            Log.e("PlayRepository", "Analytics update failed", e)
        }
    }

    // ==========================================
    // MARK: - Social Service (Comments & Reactions)
    // ==========================================

    fun getComments(itemId: String): Flow<List<PlayComment>> {
        // Path correctly evaluates to /playItems/{itemId}/comments
        return collection.document(itemId)
            .collection("comments")
            .whereEqualTo("isDeleted", false)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(PlayComment::class.java)?.copy(id = it.id) }
            }
    }

    suspend fun toggleReaction(itemId: String, reaction: String, userId: String, userName: String, photoURL: String?) {
        val itemRef = collection.document(itemId)

        // Path correctly evaluates to /playItems/{itemId}/reactions/{userId}
        val reactionRef = itemRef.collection("reactions").document(userId)

        firestore.runTransaction { transaction ->
            val itemSnapshot = transaction.get(itemRef)
            val reactionSnapshot = transaction.get(reactionRef)

            // 3. CRITICAL CRASH FIX: Safe Map casting for Firestore Numbers
            val rawCounts = itemSnapshot.get("reactionCounts") as? Map<*, *> ?: emptyMap<Any, Any>()
            val counts = rawCounts.entries.associate {
                it.key.toString() to ((it.value as? Number)?.toLong() ?: 0L)
            }.toMutableMap()

            val existingRaw = reactionSnapshot.getString("reaction")

            if (existingRaw == reaction) {
                // Remove existing reaction entirely
                val currentCount = counts[reaction] ?: 1L
                counts[reaction] = maxOf(currentCount - 1, 0L)
                if (counts[reaction] == 0L) counts.remove(reaction)

                transaction.delete(reactionRef)
            } else {
                // Decrement old reaction if changing to a new one
                if (existingRaw != null) {
                    val oldCount = counts[existingRaw] ?: 1L
                    counts[existingRaw] = maxOf(oldCount - 1, 0L)
                    if (counts[existingRaw] == 0L) counts.remove(existingRaw)
                }

                // Increment new reaction
                counts[reaction] = (counts[reaction] ?: 0L) + 1L

                transaction.set(reactionRef, mapOf(
                    "reaction" to reaction,
                    "userId" to userId,
                    "userName" to userName,
                    "userPhotoURL" to photoURL,
                    "updatedAt" to Timestamp.now()
                ), SetOptions.merge())
            }

            transaction.update(itemRef, mapOf(
                "reactionCounts" to counts,
                "updatedAt" to Timestamp.now()
            ))

            null
        }.await()
    }

    suspend fun addComment(itemId: String, text: String, userId: String, userName: String, photoURL: String?) {
        val itemRef = collection.document(itemId)
        val commentRef = itemRef.collection("comments").document()

        firestore.runBatch { batch ->
            batch.set(commentRef, mapOf(
                "userId" to userId,
                "userName" to userName,
                "userPhotoURL" to photoURL,
                "text" to text,
                "createdAt" to Timestamp.now(),
                "isDeleted" to false,
                "hidden" to false,
                "reportCount" to 0
            ))

            batch.update(itemRef, mapOf(
                "commentCount" to FieldValue.increment(1),
                "updatedAt" to Timestamp.now()
            ))
        }.await()
    }

    suspend fun deleteComment(itemId: String, commentId: String, deletedByUid: String) {
        val itemRef = collection.document(itemId)
        val commentRef = itemRef.collection("comments").document(commentId)

        firestore.runTransaction { transaction ->
            val commentSnapshot = transaction.get(commentRef)
            if (!commentSnapshot.exists()) return@runTransaction null // Fail safe

            val alreadyDeleted = commentSnapshot.getBoolean("isDeleted") ?: false

            if (!alreadyDeleted) {
                transaction.update(commentRef, mapOf(
                    "isDeleted" to true,
                    "deletedBy" to deletedByUid,
                    "deletedAt" to Timestamp.now(),
                    "updatedAt" to Timestamp.now()
                ))

                transaction.update(itemRef, mapOf(
                    "commentCount" to FieldValue.increment(-1),
                    "updatedAt" to Timestamp.now()
                ))
            }
            null
        }.await()
    }

    // ==========================================
    // MARK: - Helpers
    // ==========================================

    private fun playItemComparator(): Comparator<PlayItem> = Comparator { lhs, rhs ->
        if (lhs.isFeatured != rhs.isFeatured) {
            return@Comparator if (lhs.isFeatured) -1 else 1
        }
        if (lhs.sortRank != rhs.sortRank) {
            return@Comparator rhs.sortRank.compareTo(lhs.sortRank)
        }

        val leftDate = lhs.publishedAt ?: lhs.scheduledAt ?: lhs.createdAt
        val rightDate = rhs.publishedAt ?: rhs.scheduledAt ?: rhs.createdAt

        // Sort descending (newest first)
        rightDate.compareTo(leftDate)
    }
}