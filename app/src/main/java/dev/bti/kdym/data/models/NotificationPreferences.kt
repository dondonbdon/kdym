package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class NotificationPreferences(
    val pushEnabled: Boolean = true,
    val announcements: Boolean = true,
    val urgentAlerts: Boolean = true,
    val campUpdates: Boolean = true,
    val tribeUpdates: Boolean = true,
    val groupMessages: Boolean = true,
    val playDrops: Boolean = true,
    val eventReminders: Boolean = true,
    val updatedAt: Timestamp? = null
)
