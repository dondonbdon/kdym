package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

/**
 * Represents a user comment on a [FeedPost].
 *
 * @property id Unique identifier for the comment.
 * @property postId The ID of the post this comment belongs to.
 * @property body Text content of the comment.
 * @property createdBy UID of the user who authored the comment.
 * @property createdByName Display name of the author.
 * @property createdByRole Optional role of the author at the time of posting.
 * @property createdAt Timestamp of creation.
 * @property updatedAt Last modification timestamp.
 * @property deleted Whether the comment has been soft-deleted.
 * @property deletedAt Timestamp of deletion.
 * @property deletedBy UID of the user/admin who deleted the comment.
 */
data class FeedComment(
    val id: String = "",
    val postId: String = "",
    val body: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdByRole: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val deleted: Boolean = false,
    val deletedAt: Timestamp? = null,
    val deletedBy: String? = null
)
