package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class AppGroup(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val type: AppGroupType = AppGroupType.general,
    val campId: String? = null,
    val tribeId: String? = null,
    val memberIds: List<String> = emptyList(),
    val leaderIds: List<String> = emptyList(),
    val isPublic: Boolean = true,
    val isOfficial: Boolean = false,
    val isActive: Boolean = true,
    val chatEnabled: Boolean = true,
    val postingRestrictedToLeaders: Boolean = false,
    val attachmentsRestrictedToLeaders: Boolean = false,
    val pollsRestrictedToLeaders: Boolean = true,
    val schedulesRestrictedToLeaders: Boolean = true,
    val homePromotionRestrictedToLeaders: Boolean = true,
    val iconName: String? = null,
    val colorHex: String? = null,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    val memberCount: Int
        get() = (memberIds + leaderIds).distinct().size

    fun canUserPost(user: AppUser?): Boolean {
        if (user == null) return false
        if (user.hasCommandAccess) return true
        if (!chatEnabled) return false
        if (leaderIds.contains(user.uid)) return true
        if (postingRestrictedToLeaders) return false
        return memberIds.contains(user.uid) || isPublic
    }
}
