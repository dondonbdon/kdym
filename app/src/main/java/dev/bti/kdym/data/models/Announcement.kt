package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

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
    val isExpired: Boolean
        get() = expiresAt?.let { it.seconds < Timestamp.now().seconds } ?: false
}
