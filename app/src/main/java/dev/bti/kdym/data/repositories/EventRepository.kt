package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.KDYMEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getAllPublishedEvents(): Flow<List<KDYMEvent>> {
        return firestore.collection("events")
            .whereEqualTo("isPublished", true)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(KDYMEvent::class.java)
            }
    }

    fun getCampSchedule(campId: String): Flow<List<KDYMEvent>> {
        return firestore.collection("events")
            .whereEqualTo("campId", campId)
            .whereEqualTo("isCampEvent", true)
            .whereEqualTo("isPublished", true)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(KDYMEvent::class.java)
            }
    }
}
