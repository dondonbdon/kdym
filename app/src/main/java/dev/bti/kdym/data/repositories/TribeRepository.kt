package dev.bti.kdym.data.repositories

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.local.TribeDao
import dev.bti.kdym.data.local.toEntity
import dev.bti.kdym.data.local.toModel
import dev.bti.kdym.data.models.ScoreEntry
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.data.models.TribeWarEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing Tribes and Tribe Wars related data.
 * Data is structured under camp documents: camps/{campId}/tribes/{tribeId}.
 */
class TribeRepository(
    private val tribeDao: TribeDao? = null,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a stream of tribes for a specific camp.
     * Prioritizes local cache while syncing with network updates.
     */
    fun getTribesForCamp(campId: String): Flow<List<Tribe>> {
        val networkFlow = firestore.collection("camps")
            .document(campId)
            .collection("tribes")
            .whereEqualTo("isActive", true)
            .orderBy("totalPoints", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapIndexedNotNull { index, doc ->
                    doc.toObject(Tribe::class.java)?.copy(
                        id = doc.id,
                        rank = index + 1
                    )
                }
            }
            .onEach { tribes ->
                tribeDao?.insertTribes(tribes.map { it.toEntity() })
            }

        return if (tribeDao != null) {
            combine(
                tribeDao.getTribes().map { entities -> entities.map { it.toModel() } },
                networkFlow
            ) { cached, network -> network.ifEmpty { cached } }
        } else {
            networkFlow
        }
    }

    /**
     * Registers a new tribe in the specified camp.
     */
    suspend fun createTribe(tribe: Tribe) {
        val campId = tribe.campId.ifEmpty { "camp_2026" }
        val ref = firestore.collection("camps")
            .document(campId)
            .collection("tribes")
            .document()
        
        ref.set(tribe.copy(id = ref.id)).await()
    }

    /**
     * Updates an existing tribe's metadata.
     */
    suspend fun updateTribe(tribe: Tribe) {
        val campId = tribe.campId.ifEmpty { "camp_2026" }
        firestore.collection("camps")
            .document(campId)
            .collection("tribes")
            .document(tribe.id)
            .set(tribe)
            .await()
    }

    /**
     * Awards points to a tribe and records the transaction.
     * Uses a Firestore transaction to ensure the tribe's total points and the score entry are written together.
     */
    suspend fun addScoreEntry(entry: ScoreEntry) {
        val campId = entry.campId.ifEmpty { "camp_2026" }
        val tribeRef = firestore.collection("camps")
            .document(campId)
            .collection("tribes")
            .document(entry.tribeId)
        
        val scoreRef = firestore.collection("camps")
            .document(campId)
            .collection("scoreEntries")
            .document()
        
        firestore.runTransaction { transaction ->
            transaction.set(scoreRef, entry.copy(id = scoreRef.id))
            transaction.update(tribeRef, "totalPoints", FieldValue.increment(entry.points.toLong()))
        }.await()
    }

    /**
     * Returns a real-time stream of tribe war competition events.
     */
    fun getTribeWarEvents(campId: String): Flow<List<TribeWarEvent>> {
        return firestore.collection("camps")
            .document(campId)
            .collection("tribe_events")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(TribeWarEvent::class.java) }
    }

    /**
     * Creates a new tribe war event (competition).
     */
    suspend fun createTribeWarEvent(event: TribeWarEvent) {
        val campId = event.campId.ifEmpty { "camp_2026" }
        val ref = firestore.collection("camps")
            .document(campId)
            .collection("tribe_events")
            .document()
        
        ref.set(event.copy(id = ref.id)).await()
    }

    /**
     * Updates an existing tribe war event.
     */
    suspend fun updateTribeWarEvent(event: TribeWarEvent) {
        val campId = event.campId.ifEmpty { "camp_2026" }
        firestore.collection("camps")
            .document(campId)
            .collection("tribe_events")
            .document(event.id)
            .set(event)
            .await()
    }
}
