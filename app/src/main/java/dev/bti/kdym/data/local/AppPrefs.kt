package dev.bti.kdym.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.bti.kdym.data.models.AppConfig
import dev.bti.kdym.data.models.AppUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class AppPrefs(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val KEY_APP_CONFIG = stringPreferencesKey("app_config")
        private val KEY_APP_USER = stringPreferencesKey("app_user")
        private val KEY_ADMIN_VIEW_MODE = stringPreferencesKey("admin_view_mode")
        private val KEY_GUESSED_TRIBE = stringPreferencesKey("guessed_tribe")
        private val KEY_TRIBE_REVEAL_SHOWN = stringPreferencesKey("tribe_reveal_shown")
        private val KEY_DISMISSED_OVERLAY_ID = stringPreferencesKey("dismissed_overlay_id")
        private val KEY_OVERLAY_DISMISSAL_TIME = stringPreferencesKey("overlay_dismissal_time")
    }

    val appConfig: Flow<AppConfig?> = context.dataStore.data.map { prefs ->
        prefs[KEY_APP_CONFIG]?.let { json.decodeFromString<AppConfig>(it) }
    }

    val appUser: Flow<AppUser?> = context.dataStore.data.map { prefs ->
        prefs[KEY_APP_USER]?.let { json.decodeFromString<AppUser>(it) }
    }

    val adminViewMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ADMIN_VIEW_MODE]?.toBoolean() ?: false
    }

    val guessedTribe: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_GUESSED_TRIBE]
    }

    val tribeRevealShown: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_TRIBE_REVEAL_SHOWN]?.toBoolean() ?: false
    }

    val dismissedOverlayId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_DISMISSED_OVERLAY_ID]
    }

    val overlayDismissalTime: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[KEY_OVERLAY_DISMISSAL_TIME]?.toLong() ?: 0L
    }

    suspend fun saveAppConfig(config: AppConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_APP_CONFIG] = json.encodeToString(config)
        }
    }

    suspend fun saveAppUser(user: AppUser) {
        context.dataStore.edit { prefs ->
            prefs[KEY_APP_USER] = json.encodeToString(user)
        }
    }

    suspend fun saveAdminViewMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ADMIN_VIEW_MODE] = enabled.toString()
        }
    }

    suspend fun saveGuessedTribe(tribeId: String?) {
        context.dataStore.edit { prefs ->
            if (tribeId == null) prefs.remove(KEY_GUESSED_TRIBE)
            else prefs[KEY_GUESSED_TRIBE] = tribeId
        }
    }

    suspend fun saveTribeRevealShown(shown: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TRIBE_REVEAL_SHOWN] = shown.toString()
        }
    }

    suspend fun saveOverlayDismissal(overlayId: String, time: Long) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DISMISSED_OVERLAY_ID] = overlayId
            prefs[KEY_OVERLAY_DISMISSAL_TIME] = time.toString()
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
