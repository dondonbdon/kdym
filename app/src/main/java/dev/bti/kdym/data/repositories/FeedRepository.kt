package dev.bti.kdym.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.local.FeedPostDao
import dev.bti.kdym.data.local.toEntity
import dev.bti.kdym.data.local.toModel
import dev.bti.kdym.data.models.FeedComment
import dev.bti.kdym.data.models.FeedPost
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing home feed posts, comments, and reactions.
 */
class FeedRepository(
    private val feedPostDao: FeedPostDao? = null,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a stream of published feed posts.
     * Prioritizes local cache while syncing with network updates.
     */
    fun getLiveUpdates(): Flow<List<FeedPost>> {
        return firestore.collection("feedPosts")
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FeedPost::class.java)
                        ?.copy(id = doc.id)
                }
            }
    }

    /**
     * Creates a new feed post.
     *
     * @param post The post data to save.
     */
    suspend fun createPost(post: FeedPost) {
        val ref = firestore.collection("feedPosts").document()
        ref.set(post.copy(id = ref.id)).await()
    }

    /**
     * Adds a comment to a specific feed post.
     * Uses a transaction to increment the post's comment count atomicity.
     *
     * @param postId The ID of the post.
     * @param comment The comment data to add.
     */
    suspend fun addComment(postId: String, comment: FeedComment) {
        val postRef = firestore.collection("feedPosts").document(postId)
        val commentRef = postRef.collection("comments").document()

        try {
            firestore.runTransaction { transaction ->
                transaction.set(commentRef, comment.copy(id = commentRef.id))
                transaction.update(postRef, "commentCount", FieldValue.increment(1))
            }.await()

        } catch (e: Exception) {
            Log.e("FeedRepo", "addComment FAILED", e)
        }
    }

    /**
     * Returns a real-time stream of comments for a specific post.
     *
     * @param postId The ID of the post.
     */
    fun getComments(postId: String): Flow<List<FeedComment>> {
        require(postId.isNotBlank()) { "postId cannot be blank" }

        return firestore.collection("feedPosts")
            .document(postId)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(FeedComment::class.java) }
    }

    /**
     * Toggles a user's reaction (e.g., "like") on a post.
     * Uses a transaction to update aggregate reaction counts on the post document.
     *
     * @param postId The ID of the post.
     * @param userId The UID of the reacting user.
     * @param reaction The type of reaction (e.g., "like", "pray").
     */
    suspend fun toggleReaction(postId: String, userId: String, reaction: String) {
        val postRef = firestore.collection("feedPosts").document(postId)
        val userReactionRef = postRef.collection("userReactions").document(userId)
        
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userReactionRef)
            val currentReaction = snapshot.getString("type")
            
            if (currentReaction == reaction) {
                // User clicked the same reaction: remove it
                transaction.delete(userReactionRef)
                transaction.update(postRef, "reactionCounts.$reaction", FieldValue.increment(-1))
            } else {
                // Change or add new reaction
                if (currentReaction != null) {
                    transaction.update(postRef, "reactionCounts.$currentReaction", FieldValue.increment(-1))
                }
                transaction.set(userReactionRef, mapOf("type" to reaction))
                transaction.update(postRef, "reactionCounts.$reaction", FieldValue.increment(1))
            }
        }.await()
    }

    /**
     * Returns a real-time stream of the current user's reaction type for a specific post.
     */
    fun getUserReaction(postId: String, userId: String): Flow<String?> {
        if (postId.isBlank() || userId.isBlank()) {
            return flowOf(null)
        }

        return firestore.collection("feedPosts")
            .document(postId)
            .collection("userReactions")
            .document(userId)
            .snapshots()
            .map { it.getString("type") }
    }
}
