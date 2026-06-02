@file:UseSerializers(TimestampSerializer::class)

package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import dev.bti.kdym.data.local.serializers.TimestampSerializer

/**
 * Represents a community group or team within the app.
 * Groups can be official camp structures or user-created communities.
 *
 * @property id Unique identifier for the group.
 * @property name Display name of the group.
 * @property description Purpose or mission of the group.
 * @property type Category of the group (e.g., general, tribe, elective).
 * @property campId Optional camp session ID this group is associated with.
 * @property tribeId Optional tribe ID if this group belongs to a tribe.
 * @property memberIds List of user UIDs who are regular members.
 * @property leaderIds List of user UIDs who have administrative control over the group.
 * @property isPublic Whether the group is discoverable and joinable by anyone.
 * @property isOfficial Whether the group was created by camp organizers.
 * @property isActive Whether the group is currently operational.
 * @property chatEnabled Toggle for the group's messaging functionality.
 * @property postingRestrictedToLeaders If true, only leaders can send messages.
 * @property attachmentsRestrictedToLeaders If true, only leaders can send media.
 * @property pollsRestrictedToLeaders If true, only leaders can create polls.
 * @property schedulesRestrictedToLeaders If true, only leaders can attach schedule events.
 * @property homePromotionRestrictedToLeaders If true, only leaders can promote messages to the home feed.
 * @property iconName Name of the icon representing the group.
 * @property colorHex Primary theme color for the group in HEX format.
 * @property createdBy UID of the user who created the group.
 * @property createdAt Timestamp of group creation.
 * @property updatedAt Last modification timestamp.
 */
@IgnoreExtraProperties
@Serializable
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
    val updatedAt: Timestamp? = null,
    val lastMessageAt: Timestamp? = null,
    val lastMessageText: String? = null,
    val lastMessageSenderName: String? = null,
    val lastMessageSenderId: String? = null,
    val lastMessageId: String? = null,
    val lastMessageSenderPhotoURL: String? = null,
    val lastMessageAttachmentCount: Int = 0,
    val lastMessageIsPoll: Boolean = false,
    val unreadCounts: Map<String, Int> = emptyMap(),
    var mutedUserIds: List<String> = emptyList(),
    var pinnedUserIds: List<String> = emptyList(),
    var archivedUserIds: List<String> = emptyList(),
    var lastReadAtByUser: Map<String, Timestamp> = emptyMap(),
    var lastReadMessageIds: Map<String, String> = emptyMap(),
    val typingUserIds: List<String> = emptyList(),
    val typingUserNames: Map<String, String> = emptyMap(),
    val typingUpdatedAt: Timestamp? = null,
) {
    /**
     * Total number of unique members (including leaders).
     */
    val memberCount: Int
        get() = (memberIds + leaderIds).distinct().size

    /**
     * Business logic to determine if a specific user is allowed to post in this group.
     */
    fun canUserPost(user: AppUser?): Boolean {
        if (user == null) return false
        if (user.roleEnum.canAccessCommand) return true
        if (!chatEnabled) return false
        if (leaderIds.contains(user.uid)) return true

        val isImplicitTribeMember = (type == AppGroupType.tribe && user.tribeId == tribeId && tribeId != null)

        if (postingRestrictedToLeaders) return false
        return memberIds.contains(user.uid) || isPublic || isImplicitTribeMember
    }

    /**
     * Helper to check general membership (useful for UI checks)
     */
    fun isMember(user: AppUser?): Boolean {
        if (user == null) return false
        return memberIds.contains(user.uid) ||
                leaderIds.contains(user.uid) ||
                (type == AppGroupType.tribe && user.tribeId == tribeId && tribeId != null)
    }
    /**
     * Business logic to determine if a specific user is allowed to post in this group.
     */

}
