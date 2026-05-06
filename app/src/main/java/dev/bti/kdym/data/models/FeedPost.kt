package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class FeedPost(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val audience: FeedPostAudience = FeedPostAudience.everyone,
    val priority: FeedPostPriority = FeedPostPriority.normal,
    val source: FeedPostSource = FeedPostSource.manual,
    val campId: String? = null,
    val targetTribeId: String? = null,
    val targetGroupId: String? = null,
    val linkTitle: String? = null,
    val linkURL: String? = null,
    val attachedEventId: String? = null,
    val attachedEventTitle: String? = null,
    val attachedEventStartDate: Timestamp? = null,
    val sourceGroupId: String? = null,
    val sourceGroupName: String? = null,
    val sourceMessageId: String? = null,
    val sourceScoreEntryId: String? = null,
    val sourceTribeId: String? = null,
    val sourceTribeName: String? = null,
    val sourceScorePoints: Int? = null,
    val isPublished: Boolean = true,
    val isPinned: Boolean = false,
    val allowComments: Boolean = true,
    val allowReactions: Boolean = true,
    val createdBy: String? = null,
    val createdByName: String? = null,
    val createdByRole: UserRole? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,
    val expiresAt: Timestamp? = null,
    val reactionCounts: Map<String, Int> = emptyMap(),
    val commentCount: Int = 0
) {
    val isExpired: Boolean
        get() = expiresAt?.let { it.seconds < Timestamp.now().seconds } ?: false

    fun canUserSee(user: AppUser?): Boolean {
        if (isExpired || !isPublished) return false
        if (user?.hasCommandAccess == true) return true
        return when (audience) {
            FeedPostAudience.everyone -> true
            FeedPostAudience.campers -> user?.hasApprovedCampAccess == true
            FeedPostAudience.leaders -> user?.isLeader == true || user?.hasCommandAccess == true
            FeedPostAudience.admins -> user?.hasCommandAccess == true
            FeedPostAudience.tribe -> user?.tribeId == targetTribeId
            FeedPostAudience.group -> user?.groupIds?.contains(targetGroupId) == true
        }
    }
}
