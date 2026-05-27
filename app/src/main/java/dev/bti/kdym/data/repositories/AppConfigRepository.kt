package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.local.AppPrefs
import dev.bti.kdym.data.models.AppConfig
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing global application configuration.
 * Config is stored in a single "main" document within the "appConfig" collection.
 */
class AppConfigRepository(
    private val appPrefs: AppPrefs? = null,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a stream of the global app configuration.
     * Prioritizes local cache while syncing with network updates.
     */
    fun getAppConfig(): Flow<AppConfig?> {
        val networkFlow = firestore.collection("appConfig")
            .document("main")
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(AppConfig::class.java)
            }
            .onEach { config ->
                config?.let { appPrefs?.saveAppConfig(it) }
            }

        return if (appPrefs != null) {
            networkFlow.onStart {
                appPrefs.appConfig.firstOrNull()?.let { emit(it) }
            }
        } else {
            networkFlow
        }
    }

    /**
     * Replaces the current global configuration with the provided [config].
     */
    suspend fun updateAppConfig(config: AppConfig) {
        firestore.collection("appConfig")
            .document("main")
            .set(config)
            .await()
    }
}
