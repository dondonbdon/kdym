package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Announcement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
}
