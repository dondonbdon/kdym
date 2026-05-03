package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoURL: String? = null,

    val role: String = "public",
    val accessStatus: String = "public",

    val campId: String? = null,
    val tribeId: String? = null,
    val groupIds: List<String> = emptyList(),

    val requestedCampId: String? = null,
    val requestedRole: String? = null,
    val requestedAt: Timestamp? = null,

    val isAdmin: Boolean = false,
    val isLeader: Boolean = false,

    val fcmTokens: List<String> = emptyList(),
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),

    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val lastActiveAt: Timestamp? = null
)

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
