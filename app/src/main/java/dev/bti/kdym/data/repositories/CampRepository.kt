package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Camp
import dev.bti.kdym.data.models.CampAccessRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing camp sessions and historical themes.
 */
class CampRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Submits a request for camp access.
     */
    suspend fun requestCampAccess(request: CampAccessRequest) {
        val ref = firestore.collection("campAccessRequests").document()
        ref.set(request.copy(id = ref.id)).await()
    }

    /**
     * Returns all camp sessions, ordered by year descending.
     */
    fun getAllCamps(): Flow<List<Camp>> {
        return firestore.collection("camps")
            .orderBy("year", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(Camp::class.java) }
    }

    /**
     * Returns only historical camp sessions (excluding the active one if desired, 
     * but usually all are shown in the pager).
     */
    fun getCampHistory(): Flow<List<Camp>> {
        return firestore.collection("camps")
            .orderBy("year", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(Camp::class.java) }
    }
}
