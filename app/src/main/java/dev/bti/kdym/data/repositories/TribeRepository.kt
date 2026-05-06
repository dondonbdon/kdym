package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Tribe
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
                snapshot.toObjects(Tribe::class.java)
            }
    }

    suspend fun createTribe(tribe: Tribe) {
        val campId = tribe.campId.ifEmpty { "camp_2026" } // Default camp if empty
        val ref = firestore.collection("camps")
            .document(campId)
            .collection("tribes")
            .document()
        
        ref.set(tribe.copy(id = ref.id)).await()
    }
}
