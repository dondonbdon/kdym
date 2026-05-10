package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class AppGroup(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val type: AppGroupType = AppGroupType.general,
    val campId: String? = null,
    val tribeId: String? = null,
    val memberIds: List<String> = emptyList(),
    val leaderIds: List<String> = emptyList(),
    @get:PropertyName("isPublic")
    @set:PropertyName("isPublic")
    var isPublic: Boolean = true,
    @get:PropertyName("isOfficial")
    @set:PropertyName("isOfficial")
    var isOfficial: Boolean = false,
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true,
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
