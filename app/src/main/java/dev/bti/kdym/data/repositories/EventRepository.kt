package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getAllPublishedEvents(): Flow<List<Event>> {
        return firestore.collection("events")
            .whereEqualTo("isPublished", true)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Event::class.java)
            }
    }

    fun getCampSchedule(campId: String): Flow<List<Event>> {
        return firestore.collection("events")
            .whereEqualTo("campId", campId)
            .whereEqualTo("isCampEvent", true)
            .whereEqualTo("isPublished", true)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Event::class.java)
            }
    }
}
