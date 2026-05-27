package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

/**
 * Represents a system-wide or targeted announcement.
 * Announcements can be filtered by audience, role, tribe, or group.
 *
 * @property id Unique identifier for the announcement.
 * @property title Short, descriptive title.
 * @property body Detailed content of the announcement.
 * @property priority Importance level (e.g., normal, high).
 * @property audience Scope of the announcement (e.g., everyone, tribe, group).
 * @property targetRole Optional role required to see this announcement.
 * @property targetTribeId Optional tribe ID for tribe-specific announcements.
 * @property targetGroupId Optional group ID for group-specific announcements.
 * @property campId Optional camp session ID this announcement belongs to.
 * @property isPublished Whether the announcement is visible to users.
 * @property createdBy UID of the admin who created the announcement.
 * @property createdByName Name of the creator for display purposes.
 * @property createdAt Server-side timestamp of creation.
 * @property expiresAt Optional timestamp when the announcement should stop being shown.
 * @property readBy List of user UIDs who have acknowledged the announcement.
 */
data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val priority: AnnouncementPriority = AnnouncementPriority.normal,
    val audience: AnnouncementAudience = AnnouncementAudience.everyone,
    val targetRole: UserRole? = null,
    val targetTribeId: String? = null,
    val targetGroupId: String? = null,
    val campId: String? = null,
    @get:PropertyName("isPublished")
    @set:PropertyName("isPublished")
    var isPublished: Boolean = true,
    val createdBy: String? = null,
    val createdByName: String? = null,
    val createdAt: Timestamp? = null,
    val expiresAt: Timestamp? = null,
    val readBy: List<String> = emptyList()
) {
    /**
     * Checks if the announcement has passed its expiration date.
     */
    val isExpired: Boolean
        get() = expiresAt?.let { it.seconds < Timestamp.now().seconds } ?: false
}
