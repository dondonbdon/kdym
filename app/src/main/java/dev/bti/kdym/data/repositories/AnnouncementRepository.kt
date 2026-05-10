package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Announcement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.tasks.await

class AnnouncementRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getPublishedAnnouncements(): Flow<List<Announcement>> {
        return firestore.collection("announcements")
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Announcement::class.java)
            }
    }

    suspend fun createAnnouncement(announcement: Announcement) {
        val ref = firestore.collection("announcements").document()
        ref.set(announcement.copy(id = ref.id)).await()
    }

    suspend fun updateAnnouncement(announcement: Announcement) {
        firestore.collection("announcements")
            .document(announcement.id)
            .set(announcement)
            .await()
    }

    suspend fun deleteAnnouncement(id: String) {
        firestore.collection("announcements")
            .document(id)
            .delete()
            .await()
    }
}
