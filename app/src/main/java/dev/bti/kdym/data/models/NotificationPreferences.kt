package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer

/**
 * User-specific settings for push notifications and app alerts.
 *
 * @property pushEnabled Global toggle for all push notifications.
 * @property announcements Toggle for system-wide announcements.
 * @property urgentAlerts Toggle for high-priority/urgent notifications.
 * @property campUpdates Toggle for general camp session updates.
 * @property tribeUpdates Toggle for notifications from the user's tribe.
 * @property tribeWarUpdates Toggle for tribe war events and scoring.
 * @property groupMessages Toggle for messages from joined groups.
 * @property groupMembership Toggle for notifications about group membership changes.
 * @property groupJoinRequests Toggle for group join request alerts.
 * @property playDrops Toggle for alerts about new media content.
 * @property comments Toggle for comments on user posts or play items.
 * @property reactions Toggle for reactions on user posts or play items.
 * @property eventReminders Toggle for upcoming calendar event alerts.
 * @property accessRequests Toggle for admin notifications about access requests.
 * @property adminModeration Toggle for admin notifications about moderation.
 * @property leaderAlerts Toggle for leader-specific alerts.
 * @property updatedAt Last time these preferences were modified.
 */
@Serializable
data class NotificationPreferences(
    val pushEnabled: Boolean = true,
    val announcements: Boolean = true,
    val urgentAlerts: Boolean = true,

    val campUpdates: Boolean = true,
    val tribeUpdates: Boolean = true,
    val tribeWarUpdates: Boolean = true,
    val eventReminders: Boolean = true,

    val groupMessages: Boolean = true,
    val groupMembership: Boolean = true,
    val groupJoinRequests: Boolean = true,

    val playDrops: Boolean = true,
    val comments: Boolean = true,
    val reactions: Boolean = true,
    val feedComments: Boolean = true,

    val accessRequests: Boolean = true,
    val adminModeration: Boolean = true,
    val leaderAlerts: Boolean = true,
    val adminRequests: Boolean = true,

    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    val eventUpdates: Boolean = true
) {
}
