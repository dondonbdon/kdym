package dev.bti.kdym.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.FeedComment
import dev.bti.kdym.data.models.FeedPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FeedRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getLiveUpdates(): Flow<List<FeedPost>> {
        return firestore.collection("feedPosts")
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(FeedPost::class.java) }
    }

    suspend fun addComment(postId: String, comment: FeedComment) {
        val postRef = firestore.collection("feedPosts").document(postId)
        val commentRef = postRef.collection("comments").document()
        
        firestore.runTransaction { transaction ->
            transaction.set(commentRef, comment.copy(id = commentRef.id))
            transaction.update(postRef, "commentCount", FieldValue.increment(1))
        }.await()
    }

    fun getComments(postId: String): Flow<List<FeedComment>> {
        return firestore.collection("feedPosts")
            .document(postId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(FeedComment::class.java) }
    }

    suspend fun toggleReaction(postId: String, userId: String, reaction: String) {
        val postRef = firestore.collection("feedPosts").document(postId)
        val userReactionRef = postRef.collection("userReactions").document(userId)
        
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userReactionRef)
            val currentReaction = snapshot.getString("type")
            
            if (currentReaction == reaction) {
                // Remove reaction
                transaction.delete(userReactionRef)
                transaction.update(postRef, "reactionCounts.$reaction", FieldValue.increment(-1))
            } else {
                // Change or add reaction
                if (currentReaction != null) {
                    transaction.update(postRef, "reactionCounts.$currentReaction", FieldValue.increment(-1))
                }
                transaction.set(userReactionRef, mapOf("type" to reaction))
                transaction.update(postRef, "reactionCounts.$reaction", FieldValue.increment(1))
            }
        }.await()
    }

    fun getUserReaction(postId: String, userId: String): Flow<String?> {
        return firestore.collection("feedPosts")
            .document(postId)
            .collection("userReactions")
            .document(userId)
            .snapshots()
            .map { it.getString("type") }
    }
}
