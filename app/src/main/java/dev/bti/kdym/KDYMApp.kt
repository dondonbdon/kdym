package dev.bti.kdym

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.google.firebase.messaging.FirebaseMessaging
import java.io.File

/**
 * Base Application class for the KDYM app.
 * Initializes Hilt dependency injection and other global app-level configurations.
 */
@UnstableApi
@HiltAndroidApp
class KdymApp : Application(), ImageLoaderFactory {

    companion object {
        @OptIn(UnstableApi::class)
        lateinit var videoCache: SimpleCache
            private set
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        // 1. Create the channels right as the app starts up
        createNotificationChannels()

        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "Token fetch failed", task.exception)
                return@addOnCompleteListener
            }

            Log.d("FCM", "TOKEN = ${task.result}")
        }

        // Initialize Media3 SimpleCache for Video/Audio
        val evictor = LeastRecentlyUsedCacheEvictor(250 * 1024 * 1024L) // 250MB
        val databaseProvider = StandaloneDatabaseProvider(this)
        videoCache = SimpleCache(File(cacheDir, "video_cache"), evictor, databaseProvider)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024L) // 100MB
                    .build()
            }
            .respectCacheHeaders(false)
            .build()
    }

    // 2. Define the channels needed by your Firebase backend
    private fun createNotificationChannels() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val defaultChannel = NotificationChannel(
            "default",
            "General Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Camp updates and general alerts"
        }

        val urgentChannel = NotificationChannel(
            "urgent_alerts",
            "Urgent Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Time-sensitive camp alerts"
        }

        val groupChannel = NotificationChannel(
            "group_messages",
            "Group Messages",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Chat messages from your groups"
        }

        notificationManager.createNotificationChannels(
            listOf(defaultChannel, urgentChannel, groupChannel)
        )
    }
}
