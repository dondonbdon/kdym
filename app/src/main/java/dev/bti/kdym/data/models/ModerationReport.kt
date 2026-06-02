package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ModerationReport(
    val id: String = "",
    val createdAt: Timestamp? = null,
    val details: String = "",
    val groupId: String = "",
    val groupName: String = "",
    val messageId: String = "",
    val messageSenderId: String = "",
    val messageSenderName: String = "",
    val messageText: String = "",
    val reason: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val status: String = "open",
    val type: String = "groupMessage",
    val updatedAt: Timestamp? = null,

    val playMediaURL: String? = null,
    val playThumbnailURL: String? = null,
    val reportedContentOwnerName: String? = null,
    val playKind: String? = null,
    val playDescription: String? = null,
    val playItemId: String? = null,
    val reporterEmail: String? = null,
    val reportedContentOwnerId: String? = null
)