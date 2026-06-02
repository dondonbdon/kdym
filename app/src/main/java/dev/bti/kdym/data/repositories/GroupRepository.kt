package dev.bti.kdym.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.local.*
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.GroupMessage
import dev.bti.kdym.data.models.GroupAttachment
import dev.bti.kdym.data.models.GroupJoinRequest
import dev.bti.kdym.data.models.MessageReaction
import dev.bti.kdym.data.models.ModerationReport
import dev.bti.kdym.data.models.Poll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing groups, chat messages, polls, and message reactions.
 */
class GroupRepository(
    private val appGroupDao: AppGroupDao? = null,
    private val groupMessageDao: GroupMessageDao? = null,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a stream of active groups for a user, prioritized by local cache then synced via Firestore.
     */
    fun getGroupsForUser(uid: String, tribeId: String? = null): Flow<List<AppGroup>> {
        val networkFlow = flow {
            val memberQuery = firestore.collection("groups")
                .whereArrayContains("memberIds", uid)
                .whereEqualTo("isActive", true)
                .snapshots()

            val leaderQuery = firestore.collection("groups")
                .whereArrayContains("leaderIds", uid)
                .whereEqualTo("isActive", true)
                .snapshots()

            val tribeQuery = if (tribeId != null) {
                firestore.collection("groups")
                    .whereEqualTo("tribeId", tribeId)
                    .whereEqualTo("isActive", true)
                    .snapshots()
            } else {
                flowOf(null)
            }

            combine(memberQuery, leaderQuery, tribeQuery) { memberSnap, leaderSnap, tribeSnap ->
                val allDocs = mutableListOf<AppGroup>()

                memberSnap.documents.forEach { doc ->
                    doc.toObject(AppGroup::class.java)?.copy(id = doc.id)?.let { allDocs.add(it) }
                }

                leaderSnap.documents.forEach { doc ->
                    doc.toObject(AppGroup::class.java)?.copy(id = doc.id)?.let { group ->
                        if (allDocs.none { it.id == group.id }) {
                            allDocs.add(group)
                        }
                    }
                }

                tribeSnap?.documents?.forEach { doc ->
                    doc.toObject(AppGroup::class.java)?.copy(id = doc.id)?.let { group ->
                        if (allDocs.none { it.id == group.id }) {
                            allDocs.add(group)
                        }
                    }
                }

                allDocs.sortedByDescending { it.lastMessageAt ?: it.createdAt }
            }.onEach { groups ->
                val groupIds = groups.joinToString { it.id }
                appGroupDao?.insertGroups(groups.map { it.toEntity() })
            }.collect { emit(it) }
        }.onStart { emit(emptyList()) }

        val cachedFlow = appGroupDao?.getGroups()?.map { entities -> 
            entities.map { it.toModel() }
        } ?: flowOf(emptyList())

        return combine(cachedFlow, networkFlow) { cached, network ->
            val result = network.ifEmpty { cached }
            result
        }
    }



    /**
     * Returns a real-time stream of all active groups in the system.
     */
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
            }.onStart { emit(emptyList()) }
    }

    /**
     * Returns a real-time stream of groups marked as public and active.
     */
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
            }.onStart { emit(emptyList()) }
    }

    /**
     * Returns a stream of the most recent messages for a group.
     * Prioritizes local cache while syncing with network updates.
     */
    fun getMessages(groupId: String, limit: Long = 50, startAfterTimestamp: Timestamp? = null): Flow<List<GroupMessage>> {
        val networkFlow = firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(GroupMessage::class.java)?.copy(
                        id = doc.id
                    )
                }
            }
            .onEach { messages ->
                if (startAfterTimestamp == null) {
                    groupMessageDao?.insertMessages(messages.map { it.toEntity() })
                }
            }.onStart { emit(emptyList()) }

        val cachedFlow = groupMessageDao?.getMessages(groupId)?.map { entities -> 
            entities.map { it.toModel() }
        } ?: flowOf(emptyList())

        return if (startAfterTimestamp == null) {
            combine(cachedFlow, networkFlow) { cached, network -> 
                val output = network.ifEmpty { cached }
                output
            }
        } else {
            networkFlow
        }
    }

    /**
     * Creates a new group.
     */
    suspend fun createGroup(group: AppGroup) {
        val ref = firestore.collection("groups").document()
        ref.set(group.copy(id = ref.id)).await()
    }

    /**
     * Updates an existing group document.
     */
    suspend fun updateGroup(group: AppGroup) {
        firestore.collection("groups")
            .document(group.id)
            .set(group)
            .await()
    }

    /**
     * Appends a message to a group's message subcollection.
     */
    suspend fun sendMessage(groupId: String, message: GroupMessage) {
        firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .add(message)
            .await()
    }

    /**
     * Creates a new poll within a group's subcollection and returns its unique ID.
     */
    suspend fun createPoll(groupId: String, poll: Poll): String {
        val ref = firestore.collection("groups")
            .document(groupId)
            .collection("polls")
            .document()
        
        val newPoll = poll.copy(id = ref.id)
        ref.set(newPoll).await()
        return ref.id
    }

    /**
     * Returns a real-time stream of a specific poll's data.
     */
    fun getPoll(groupId: String, pollId: String): Flow<Poll?> {
        return firestore.collection("groups")
            .document(groupId)
            .collection("polls")
            .document(pollId)
            .snapshots()
            .map { it.toObject(Poll::class.java) }
    }

    /**
     * Casts or toggles a user's vote in a poll.
     * Uses a transaction to ensure vote counts and voter lists remain synchronized.
     */
    suspend fun voteInPoll(groupId: String, pollId: String, optionId: String, userId: String) {
        val pollRef = firestore.collection("groups")
            .document(groupId)
            .collection("polls")
            .document(pollId)
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

    /**
     * Toggles an emoji reaction on a specific group message.
     * Uses a transaction to update the reaction subcollection and aggregate counts on the message.
     */
    suspend fun addReaction(groupId: String, messageId: String, userId: String, emoji: String, userName: String = "KDYM Member") {
        val messageRef = firestore.collection("groups").document(groupId)
            .collection("messages").document(messageId)

        // Using userId as the document ID prevents duplicate reactions from the same user
        val reactionRef = messageRef.collection("reactions").document(userId)

        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(messageRef)
                if (!snapshot.exists()) return@runTransaction

                val existingReactionSnapshot = transaction.get(reactionRef)

                // FIX: Look for the field named "reaction" to match your friend's DB schema
                val existingEmoji = if (existingReactionSnapshot.exists()) {
                    existingReactionSnapshot.getString("reaction")
                } else null

                @Suppress("UNCHECKED_CAST")
                val currentCounts = snapshot.get("reactionCounts") as? MutableMap<String, Long>
                    ?: mutableMapOf()

                if (existingEmoji == emoji) {
                    // USER CLICKED THE SAME EMOJI: Remove the reaction entirely
                    transaction.delete(reactionRef)
                    val count = currentCounts[emoji] ?: 0L
                    if (count > 1L) {
                        currentCounts[emoji] = count - 1L
                    } else {
                        currentCounts.remove(emoji)
                    }
                } else {
                    // USER ADDED A NEW EMOJI OR SWITCHED EMOJIS
                    // FIX: Save the data exactly as your friend structured it
                    val reactionData = mapOf(
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp(),
                        "displayName" to userName,
                        "userName" to userName,
                        "reaction" to emoji,
                        "uid" to userId,
                        "userId" to userId
                    )
                    transaction.set(reactionRef, reactionData)

                    // Decrement old emoji count if they switched
                    if (existingEmoji != null) {
                        val oldCount = currentCounts[existingEmoji] ?: 0L
                        if (oldCount > 1L) {
                            currentCounts[existingEmoji] = oldCount - 1L
                        } else {
                            currentCounts.remove(existingEmoji)
                        }
                    }

                    // Increment new emoji count
                    val newCount = currentCounts[emoji] ?: 0L
                    currentCounts[emoji] = newCount + 1L
                }

                // Update the parent document so the Chat Bubble renders instantly without subcollection queries
                transaction.update(messageRef, "reactionCounts", currentCounts)
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Fetches detailed reaction data for a specific message (Used for the Bottom Sheet).
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun getMessageReactions(groupId: String, messageId: String): Flow<List<MessageReaction>> {
        return firestore.collection("groups").document(groupId)
            .collection("messages").document(messageId)
            .collection("reactions")
            .snapshots()
            .map { snapshot ->
                // FIX: Read directly from the updated schema. No need to do a secondary fetch for user profiles
                // since your friend is saving the userName directly in the reaction document!
                snapshot.documents.mapNotNull { doc ->
                    val userId = doc.getString("userId") ?: return@mapNotNull null
                    val emoji = doc.getString("reaction") ?: "" // Looking for "reaction"
                    val userName = doc.getString("userName") ?: doc.getString("displayName") ?: "Unknown"

                    MessageReaction(
                        userId = userId,
                        userName = userName,
                        userPhotoURL = null, // You can add photoURL to the DB schema if you want avatars in the bottom sheet
                        emoji = emoji
                    )
                }
            }
    }


    /**
     * Submits a request to join a group.
     */
    suspend fun requestJoinGroup(request: GroupJoinRequest) {
        val ref = firestore.collection("groupJoinRequests").document()
        ref.set(request.copy(id = ref.id)).await()
    }

    /**
     * Returns a stream of join requests for a specific user.
     */
    fun getJoinRequestsForUser(uid: String): Flow<List<GroupJoinRequest>> {
        return firestore.collection("groupJoinRequests")
            .whereEqualTo("requesterId", uid)
            .snapshots()
            .map { snap ->
                snap.documents.mapNotNull { it.toObject(GroupJoinRequest::class.java)?.copy(id = it.id) }
            }
    }

    /**
     * Returns a stream of pending join requests for a specific group (for admins/leaders).
     */
    fun getJoinRequestsForGroup(groupId: String): Flow<List<GroupJoinRequest>> {
        return firestore.collection("groupJoinRequests")
            .whereEqualTo("groupId", groupId)
            .whereEqualTo("status", "pending")
            .snapshots()
            .map { snap ->
                snap.documents.mapNotNull { it.toObject(GroupJoinRequest::class.java)?.copy(id = it.id) }
            }
    }

    /**
     * Updates the status of a join request.
     */
    suspend fun updateJoinRequestStatus(requestId: String, status: String, reviewedBy: String) {
        firestore.collection("groupJoinRequests").document(requestId).update(
            "status", status,
            "reviewedBy", reviewedBy,
            "reviewedAt", Timestamp.now(),
            "updatedAt", Timestamp.now()
        ).await()
    }

    /**
     * Fetches all media attachments shared within a group.
     */
    fun getGroupMedia(groupId: String): Flow<List<GroupAttachment>> {
        return firestore.collection("groups").document(groupId)
            .collection("messages")
            .whereNotEqualTo("attachments", emptyList<GroupAttachment>())
            .orderBy("attachments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snap ->
                snap.documents.flatMap { doc ->
                    val message = doc.toObject(GroupMessage::class.java)
                    message?.attachments ?: emptyList()
                }
            }
    }
}
