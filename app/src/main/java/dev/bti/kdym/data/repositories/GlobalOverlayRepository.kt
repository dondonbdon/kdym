package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.GlobalOverlay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GlobalOverlayRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getGlobalOverlay(): Flow<GlobalOverlay?> {
        return firestore.collection("globalOverlays")
            .document("current")
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(GlobalOverlay::class.java)
            }
    }

    suspend fun updateGlobalOverlay(overlay: GlobalOverlay) {
        firestore.collection("globalOverlays")
            .document("current")
            .set(overlay)
            .await()
    }
}
