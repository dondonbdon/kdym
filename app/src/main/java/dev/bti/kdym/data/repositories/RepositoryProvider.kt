package dev.bti.kdym.data.repositories

import android.content.Context
import dev.bti.kdym.data.local.AppPrefs
import dev.bti.kdym.data.local.KdymDatabase

/**
 * Static provider for singleton repository instances.
 * Simplifies dependency access for ViewModels and other components.
 */
object RepositoryProvider {

    private var database: KdymDatabase? = null
    var prefs: AppPrefs? = null
        private set

    fun initialize(context: Context) {
        val appContext = context.applicationContext
        database = KdymDatabase.getDatabase(appContext)
        prefs = AppPrefs(appContext)
    }

    val appConfigRepository by lazy { AppConfigRepository(appPrefs = prefs) }
    val userRepository by lazy { UserRepository(appPrefs = prefs) }
    val tribeRepository by lazy { TribeRepository(tribeDao = database?.tribeDao()) }
    val feedRepository by lazy { FeedRepository(feedPostDao = database?.feedPostDao()) }
    val announcementRepository by lazy { AnnouncementRepository() }
    val groupRepository by lazy { GroupRepository(appGroupDao = database?.appGroupDao(), groupMessageDao = database?.groupMessageDao()) }
    val eventRepository by lazy { EventRepository() }
    val playRepository by lazy { PlayRepository(playItemDao = database?.playItemDao()) }
    val authRepository by lazy { AuthRepository() }
    val storageRepository by lazy { StorageRepository() }
    val churchRepository by lazy { ChurchRepository() }
    val campRepository by lazy { CampRepository() }
    val globalOverlayRepository by lazy { GlobalOverlayRepository() }
}
