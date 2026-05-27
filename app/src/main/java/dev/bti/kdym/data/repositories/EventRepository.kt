package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.Timestamp
import dev.bti.kdym.data.models.EventCategory
import dev.bti.kdym.data.models.KDYMEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Repository for managing calendar events and camp schedules.
 */
class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a real-time stream of all public main events.
     * Filters out 'scheduleItem' kinds so they don't clutter the main feed.
     */
    fun getAllPublishedEvents(): Flow<List<KDYMEvent>> {
        return firestore.collection("events")
            .whereEqualTo("isPublished", true)
            .orderBy("startDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(KDYMEvent::class.java)
            }
    }

    /**
     * Returns a real-time stream of schedule items specific to a parent event.
     * (This matches the listenScheduleItems function in your Swift repo)
     */
    fun getScheduleItems(parentEventId: String): Flow<List<KDYMEvent>> {
        if (parentEventId.isBlank()) return flowOf(emptyList())
        return firestore.collection("events")
            .whereEqualTo("parentEventId", parentEventId)
            .whereEqualTo("isPublished", true)
            // Optional: you can also ensure eventKind == "scheduleItem" here
            .orderBy("startDate", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(KDYMEvent::class.java)
            }
    }

    /**
     * Returns a real-time stream of schedule items specific to a camp session.
     */
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

    /**
     * Updates a user's RSVP status for an event.
     */
    suspend fun updateRSVP(eventId: String, userId: String, status: String) {
        if (eventId.isBlank() || userId.isBlank()) return
        val rsvpRef =
            firestore.collection("events").document(eventId).collection("rsvps").document(userId)
        val data = mapOf(
            "userId" to userId,
            "status" to status,
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        rsvpRef.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
    }

    /**
     * Returns a stream of RSVPs for a specific event.
     */
    fun getRSVPs(eventId: String): Flow<List<dev.bti.kdym.viewmodels.EventRSVP>> {
        if (eventId.isBlank()) return flowOf(emptyList())
        return firestore.collection("events").document(eventId).collection("rsvps")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(dev.bti.kdym.viewmodels.EventRSVP::class.java)
            }
    }

    /**
     * Creates a new event in Firestore.
     */
    suspend fun createEvent(event: KDYMEvent) {
        val ref = firestore.collection("events").document()
        ref.set(event.copy(id = ref.id)).await()
    }

    /**
     * Updates an existing event document.
     */
    suspend fun updateEvent(event: KDYMEvent) {
        if (event.id.isBlank()) return
        firestore.collection("events")
            .document(event.id)
            .set(event)
            .await()
    }

    /**
     * Deletes an event from Firestore.
     */
    suspend fun deleteEvent(eventId: String) {
        if (eventId.isBlank()) return
        firestore.collection("events")
            .document(eventId)
            .delete()
            .await()
    }
}

