package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val type: String = "general", // tribe, cabin, leadership, volunteer, prayer, general, custom
    val campId: String? = null,
    val tribeId: String? = null,
    val memberIds: List<String> = emptyList(),
    val leaderIds: List<String> = emptyList(),
    val isPublic: Boolean = true,
    val isOfficial: Boolean = true,
    val isActive: Boolean = true,
    val chatEnabled: Boolean = true,
    val postingRestrictedToLeaders: Boolean = false,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class Message(
    val id: String = "",
    val groupId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String? = null,
    val text: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val deleted: Boolean = false,
    val deletedAt: Timestamp? = null,
    val deletedBy: String? = null,
    val isSystemMessage: Boolean = false
)
