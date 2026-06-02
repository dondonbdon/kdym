package dev.bti.kdym.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.bti.kdym.data.models.ModerationReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ModerationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun submitModerationReport(report: ModerationReport) {
        val ref = firestore.collection("moderationReports").document()
        val finalReport = report.copy(
            id = ref.id,
            createdAt = Timestamp.now(),
            updatedAt = Timestamp.now()
        )
        ref.set(finalReport).await()
    }

    fun getModerationReports(): Flow<List<ModerationReport>> = callbackFlow {
        val subscription = firestore.collection("moderationReports")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reports = snapshot.toObjects(ModerationReport::class.java)
                    trySend(reports)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun updateReportStatus(reportId: String, status: String) {
        firestore.collection("moderationReports").document(reportId)
            .update("status", status, "updatedAt", Timestamp.now())
            .await()
    }
}