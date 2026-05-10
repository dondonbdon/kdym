package dev.bti.kdym.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.data.models.ScoreEntry
import dev.bti.kdym.data.models.TribeWarEvent
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.tasks.await

class TribeRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getTribesForCamp(campId: String): Flow<List<Tribe>> {
        return firestore.collection("camps")
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
    }

    suspend fun createTribe(tribe: Tribe) {
        val campId = tribe.campId.ifEmpty { "camp_2026" }
        val ref = firestore.collection("camps")
            .document(campId)
            .collection("tribes")
            .document()
        
        ref.set(tribe.copy(id = ref.id)).await()
    }

    suspend fun updateTribe(tribe: Tribe) {
        val campId = tribe.campId.ifEmpty { "camp_2026" }
        firestore.collection("camps")
            .document(campId)
            .collection("tribes")
            .document(tribe.id)
            .set(tribe)
            .await()
    }

    suspend fun addScoreEntry(entry: ScoreEntry) {
        Log.d("TribeRepo", "tribeId=${entry.tribeId}")
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

    fun getTribeWarEvents(campId: String): Flow<List<TribeWarEvent>> {
        return firestore.collection("camps")
            .document(campId)
            .collection("tribe_events")
            .orderBy("startDate", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(TribeWarEvent::class.java) }
    }

    suspend fun createTribeWarEvent(event: TribeWarEvent) {
        val campId = event.campId.ifEmpty { "camp_2026" }
        val ref = firestore.collection("camps")
            .document(campId)
            .collection("tribe_events")
            .document()
        
        ref.set(event.copy(id = ref.id)).await()
    }
}
