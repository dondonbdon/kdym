package dev.bti.kdym.data.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * Repository responsible for handling all Firebase Storage operations.
 * This includes uploading profile photos, chat media, and feed post content.
 */
class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    /**
     * Uploads a profile photo for a specific user.
     * Replaces any existing photo at the "profilePhotos/$uid" path.
     *
     * @param uid The unique ID of the user.
     * @param uri The local [Uri] of the image to upload.
     * @return The public download URL of the uploaded image.
     */
    suspend fun uploadProfilePhoto(uid: String, uri: Uri): String {
        return try {
            val ref = storage.reference.child("profilePhotos/$uid")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw StorageException("Failed to upload profile photo: ${e.message}", e)
        }
    }

    /**
     * Uploads media for a specific chat group.
     * Generates a unique filename based on the current timestamp.
     *
     * @param groupId The ID of the group the media belongs to.
     * @param uri The local [Uri] of the media to upload.
     * @return The public download URL of the uploaded media.
     */
    suspend fun uploadChatMedia(groupId: String, uri: Uri): String {
        return try {
            val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val ref = storage.reference.child("chats/$groupId/$fileName")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw StorageException("Failed to upload chat media: ${e.message}", e)
        }
    }

    /**
     * Uploads media for a specific feed post.
     *
     * @param postId The ID of the feed post.
     * @param uri The local [Uri] of the media to upload.
     * @return The public download URL of the uploaded media.
     */
    suspend fun uploadPostMedia(postId: String, uri: Uri): String {
        return try {
            val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val ref = storage.reference.child("posts/$postId/$fileName")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw StorageException("Failed to upload post media: ${e.message}", e)
        }
    }
}

/**
 * Custom exception for storage-related failures.
 */
class StorageException(message: String, cause: Throwable? = null) : Exception(message, cause)
