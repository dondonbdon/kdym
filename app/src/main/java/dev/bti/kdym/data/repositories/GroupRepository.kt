package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.GroupMessage
import dev.bti.kdym.data.models.Poll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GroupRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getGroupsForUser(uid: String): Flow<List<AppGroup>> {
        return firestore.collection("groups")
            .whereArrayContains("memberIds", uid)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AppGroup::class.java)?.copy(
                        id = doc.id
                    )
                }
            }
    }

    fun getAllGroups(): Flow<List<AppGroup>> {
        return firestore.collection("groups")
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AppGroup::class.java)?.copy(
                        id = doc.id
                    )
                }
            }
    }

    fun getPublicGroups(): Flow<List<AppGroup>> {
        return firestore.collection("groups")
            .whereEqualTo("isPublic", true)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AppGroup::class.java)?.copy(
                        id = doc.id
                    )
                }
            }
    }

    fun getMessages(groupId: String): Flow<List<GroupMessage>> {
        return firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .snapshots()
            .map { snapshot ->
                // Ensure IDs are mapped correctly if relying on the document ID
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(GroupMessage::class.java)?.copy(
                        id = doc.id
                    )
                }
            }
    }

    suspend fun createGroup(group: AppGroup) {
        val ref = firestore.collection("groups").document()
        ref.set(group.copy(id = ref.id)).await()
    }

    suspend fun updateGroup(group: AppGroup) {
        firestore.collection("groups")
            .document(group.id)
            .set(group)
            .await()
    }

    suspend fun sendMessage(groupId: String, message: GroupMessage) {
        firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .add(message)
            .await()
    }

    suspend fun createPoll(poll: Poll): String {
        val ref = firestore.collection("polls").document()
        val newPoll = poll.copy(id = ref.id)
        ref.set(newPoll).await()
        return ref.id
    }

    fun getPoll(pollId: String): Flow<Poll?> {
        return firestore.collection("polls")
            .document(pollId)
            .snapshots()
            .map { it.toObject(Poll::class.java) }
    }

    suspend fun voteInPoll(pollId: String, optionId: String, userId: String) {
        val pollRef = firestore.collection("polls").document(pollId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(pollRef)
            val poll = snapshot.toObject(Poll::class.java) ?: return@runTransaction

            val updatedOptions = poll.options.map { option ->
                if (option.id == optionId) {
                    val alreadyVoted = option.voterIds.contains(userId)
                    if (alreadyVoted) {
                        // Toggle off
                        option.copy(
                            voteCount = (option.voteCount - 1).coerceAtLeast(0),
                            voterIds = option.voterIds - userId
                        )
                    } else {
                        // Vote
                        option.copy(
                            voteCount = option.voteCount + 1,
                            voterIds = option.voterIds + userId
                        )
                    }
                } else if (!poll.allowMultipleVotes && option.voterIds.contains(userId)) {
                    // Remove vote from other option if multiple votes not allowed
                    option.copy(
                        voteCount = (option.voteCount - 1).coerceAtLeast(0),
                        voterIds = option.voterIds - userId
                    )
                } else {
                    option
                }
            }

            val totalVotes = updatedOptions.sumOf { it.voteCount }
            transaction.update(pollRef, "options", updatedOptions)
            transaction.update(pollRef, "totalVotes", totalVotes)
        }.await()
    }

    suspend fun addReaction(groupId: String, messageId: String, userId: String, emoji: String) {
        val messageRef = firestore.collection("groups").document(groupId)
            .collection("messages").document(messageId)

        // Path to the subcollection where the user's specific reaction lives
        val reactionRef = messageRef.collection("reactions").document(userId)

        try {
            firestore.runTransaction { transaction ->
                // 1. Read the current message document to get existing counts
                val snapshot = transaction.get(messageRef)

                // If the message was deleted, abort
                if (!snapshot.exists()) return@runTransaction

                // 2. Check if the user has already reacted
                val existingReactionSnapshot = transaction.get(reactionRef)
                val existingEmoji = if (existingReactionSnapshot.exists()) {
                    existingReactionSnapshot.getString("emoji")
                } else null

                // Firestore stores numbers as Long in raw map snapshots
                @Suppress("UNCHECKED_CAST")
                val currentCounts = snapshot.get("reactionCounts") as? MutableMap<String, Long>
                    ?: mutableMapOf()

                // 3. Logic for toggling or switching reactions
                if (existingEmoji == emoji) {
                    // USER CLICKED THE SAME EMOJI: Remove the reaction
                    transaction.delete(reactionRef)
                    val count = currentCounts[emoji] ?: 0L
                    if (count > 1L) {
                        currentCounts[emoji] = count - 1L
                    } else {
                        currentCounts.remove(emoji)
                    }
                } else {
                    // USER ADDED A NEW EMOJI OR SWITCHED EMOJIS:
                    // Write to the subcollection
                    val reactionData = mapOf(
                        "userId" to userId,
                        "emoji" to emoji,
                        "timestamp" to FieldValue.serverTimestamp()
                    )
                    transaction.set(reactionRef, reactionData)

                    // Decrement the old emoji count if they switched
                    if (existingEmoji != null) {
                        val oldCount = currentCounts[existingEmoji] ?: 0L
                        if (oldCount > 1L) {
                            currentCounts[existingEmoji] = oldCount - 1L
                        } else {
                            currentCounts.remove(existingEmoji)
                        }
                    }

                    // Increment the new emoji count
                    val newCount = currentCounts[emoji] ?: 0L
                    currentCounts[emoji] = newCount + 1L
                }

                // 4. Update the aggregate counts on the main message document
                transaction.update(messageRef, "reactionCounts", currentCounts)
            }.await()

        } catch (e: Exception) {
            // Rethrow or handle locally if preferred
            throw e
        }
    }
}