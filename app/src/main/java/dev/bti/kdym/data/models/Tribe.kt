package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class Tribe(
    val id: String = "",
    val name: String = "",
    val subtitle: String? = null,
    val colorHex: String = "#FFFFFF",
    val iconName: String? = null,
    val campId: String = "",
    val leaderIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    val totalPoints: Int = 0,
    val rank: Int = 0,
    val isActive: Boolean = true,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
