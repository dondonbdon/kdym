package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer
import java.util.UUID

/**
 * Represents a single message within an [AppGroup] chat.
 *
 * @property id Unique identifier for the message.
 * @property groupId The ID of the group this message belongs to.
 * @property senderId UID of the sender.
 * @property senderName Cached display name of the sender.
 * @property senderPhotoURL Cached photo URL of the sender.
 * @property senderRole Cached role of the sender at the time of posting.
 * @property text Text content of the message.
 * @property attachments List of media or files attached to the message.
 * @property replyToMessageId ID of the message being replied to.
 * @property replyToSenderName Name of the sender of the original message.
 * @property replyToText Snippet of the text being replied to.
 * @property createdAt Timestamp of creation.
 * @property updatedAt Last modification timestamp.
 * @property deleted Whether the message has been soft-deleted.
 * @property deletedAt Timestamp of deletion.
 * @property deletedBy UID of the user/admin who deleted the message.
 * @property isSystemMessage True if this is an automated system notification (e.g., "User joined").
 * @property promoteToHome Whether this message has been marked for promotion to the main feed.
 * @property promotedFeedPostId ID of the resulting [FeedPost] if promoted.
 * @property promotedAt Timestamp of promotion.
 * @property reactionCounts Aggregated counts of emoji reactions.
 * @property pollId Optional ID of an attached poll.
 * @property pollQuestion Cached question text of the attached poll.
 * @property scheduleEventId Optional ID of an attached schedule event.
 * @property scheduleEventTitle Cached title of the attached event.
 * @property scheduleEventStartDate Cached start date of the attached event.
 */
@Serializable
@IgnoreExtraProperties
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
    @Serializable(with = TimestampSerializer::class)
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    val deleted: Boolean = false,
    @Serializable(with = TimestampSerializer::class)
    val deletedAt: Timestamp? = null,
    val deletedBy: String? = null,
    @get:PropertyName("isSystemMessage")
    @set:PropertyName("isSystemMessage")
    var isSystemMessage: Boolean = false,
    val promoteToHome: Boolean = false,
    val promotedFeedPostId: String? = null,
    @Serializable(with = TimestampSerializer::class)
    val promotedAt: Timestamp? = null,
    val reactionCounts: Map<String, Int> = emptyMap(),
    val pollId: String? = null,
    val pollQuestion: String? = null,
    val scheduleEventId: String? = null,
    val scheduleEventTitle: String? = null,
    @Serializable(with = TimestampSerializer::class)
    val scheduleEventStartDate: Timestamp? = null,
    val isPinned: Boolean = false
)

/**
 * Represents a media or file attachment in a [GroupMessage].
 *
 * @property id Unique identifier for the attachment.
 * @property type Category of attachment (image, video, etc.).
 * @property url Public download URL.
 * @property thumbnailURL Optional URL for a low-res preview.
 * @property fileName Original name of the file.
 * @property contentType MIME type of the content.
 * @property sizeBytes Size of the file in bytes.
 */
@Serializable
data class GroupAttachment(
    val id: String = UUID.randomUUID().toString(),
    val type: GroupAttachmentType = GroupAttachmentType.image,
    val url: String = "",
    val thumbnailURL: String? = null,
    val fileName: String? = null,
    val contentType: String? = null,
    val sizeBytes: Long? = null
)
