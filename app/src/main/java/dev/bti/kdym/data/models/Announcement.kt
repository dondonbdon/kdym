package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val priority: String = "normal", // normal, important, urgent
    val audience: String = "everyone", // everyone, campers, leaders, admins, tribe, group
    val targetRole: String? = null,
    val targetTribeId: String? = null,
    val targetGroupId: String? = null,
    val campId: String? = null,
    val isPublished: Boolean = true,
    val sendPush: Boolean? = null,
    val pushSentAt: Timestamp? = null,
    val pushError: String? = null,
    val createdBy: String? = null,
    val createdByName: String? = null,
    val createdAt: Timestamp? = null,
    val expiresAt: Timestamp? = null,
    val readBy: List<String> = emptyList()
)
