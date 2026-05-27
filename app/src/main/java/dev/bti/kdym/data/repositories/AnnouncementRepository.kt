package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Announcement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing [Announcement] data from Firestore.
 * Handles fetching published announcements and administrative CRUD operations.
 */
class AnnouncementRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a real-time stream of all announcements marked as published,
     * ordered by creation date (newest first).
     */
    fun getPublishedAnnouncements(): Flow<List<Announcement>> {
        return firestore.collection("announcements")
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Announcement::class.java)
            }
    }

    /**
     * Creates a new announcement document in Firestore.
     *
     * @param announcement The announcement data to save.
     */
    suspend fun createAnnouncement(announcement: Announcement) {
        val ref = firestore.collection("announcements").document()
        ref.set(announcement.copy(id = ref.id)).await()
    }

    /**
     * Updates an existing announcement document.
     *
     * @param announcement The updated announcement data (must include valid ID).
     */
    suspend fun updateAnnouncement(announcement: Announcement) {
        firestore.collection("announcements")
            .document(announcement.id)
            .set(announcement)
            .await()
    }

    /**
     * Hard-deletes an announcement from Firestore.
     *
     * @param id The unique identifier of the announcement to delete.
     */
    suspend fun deleteAnnouncement(id: String) {
        firestore.collection("announcements")
            .document(id)
            .delete()
            .await()
    }
}
