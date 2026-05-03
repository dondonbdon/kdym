package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppConfigRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getAppConfig(): Flow<AppConfig?> {
        return firestore.collection("appConfig")
            .document("main")
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(AppConfig::class.java)
            }
    }
}
