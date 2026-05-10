package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import lombok.Getter
import lombok.Setter
import java.util.UUID

@Getter
@Setter
data class GroupMessage(
    val id: String = "",
    val groupId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderPhotoURL: String? = null,
    val senderRole: UserRole? = null,
    val text: String = "",
    val attachments: List<GroupAttachment> = emptyList(),
    val replyToMessageId: String? = null,
    val replyToSenderName: String? = null,
    val replyToText: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val deleted: Boolean = false,
    val deletedAt: Timestamp? = null,
    val deletedBy: String? = null,
    @get:PropertyName("isSystemMessage")
    @set:PropertyName("isSystemMessage")
    var isSystemMessage: Boolean = false,
    val promoteToHome: Boolean = false,
    val promotedFeedPostId: String? = null,
    val promotedAt: Timestamp? = null,
    val reactionCounts: Map<String, Int> = emptyMap(),
    val pollId: String? = null,
    val pollQuestion: String? = null,
    val scheduleEventId: String? = null,
    val scheduleEventTitle: String? = null,
    val scheduleEventStartDate: Timestamp? = null
)

data class GroupAttachment(
    val id: String = UUID.randomUUID().toString(),
    val type: GroupAttachmentType = GroupAttachmentType.image,
    val url: String = "",
    val thumbnailURL: String? = null,
    val fileName: String? = null,
    val contentType: String? = null,
    val sizeBytes: Long? = null
)
