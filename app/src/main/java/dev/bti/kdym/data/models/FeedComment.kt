package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class FeedComment(
    val id: String = "",
    val postId: String = "",
    val body: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdByRole: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val deleted: Boolean = false,
    val deletedAt: Timestamp? = null,
    val deletedBy: String? = null
)
