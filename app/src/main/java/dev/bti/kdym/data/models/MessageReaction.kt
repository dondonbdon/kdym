package dev.bti.kdym.data.models

/**
 * Represents a user's reaction to a message.
 */
data class MessageReaction(
    val userId: String = "",
    val userName: String = "",
    val userPhotoURL: String? = null,
    val emoji: String = ""
)
