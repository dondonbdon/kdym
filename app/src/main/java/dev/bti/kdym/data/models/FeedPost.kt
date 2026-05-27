package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer

/**
 * Represents an entry in the main home feed.
 * Posts can originate from announcements, group messages, scores, or manual entry.
 *
 * @property id Unique identifier for the post.
 * @property title Heading of the post.
 * @property body Detailed content.
 * @property audience Scope of visibility (e.g., everyone, specific tribe).
 * @property priority Visual importance level.
 * @property source The origin system of this post.
 * @property campId Optional camp session ID.
 * @property targetTribeId Optional tribe ID for tribe-targeted posts.
 * @property targetGroupId Optional group ID for group-targeted posts.
 * @property linkTitle Optional text for an attached hyperlink.
 * @property linkURL Optional URL for an attached hyperlink.
 * @property attachedEventId Optional ID of a linked calendar event.
 * @property attachedEventTitle Cached title of the linked event.
 * @property attachedEventStartDate Cached start date of the linked event.
 * @property sourceGroupId ID of the group if the post originated from a chat message.
 * @property sourceGroupName Cached name of the origin group.
 * @property sourceMessageId ID of the origin chat message.
 * @property sourceScoreEntryId ID of the origin score entry for tribe wars posts.
 * @property sourceTribeId ID of the tribe associated with a score post.
 * @property sourceTribeName Cached name of the associated tribe.
 * @property sourceScorePoints Points awarded in a score post.
 * @property isPublished Whether the post is live.
 * @property isPinned Whether the post remains at the top of the feed.
 * @property allowComments Toggle for user commenting.
 * @property allowReactions Toggle for user reactions.
 * @property createdBy UID of the author.
 * @property createdByName Name of the author.
 * @property createdByRole Role of the author at time of creation.
 * @property createdAt Timestamp of creation.
 * @property updatedAt Last modification timestamp.
 * @property expiresAt Optional cleanup timestamp.
 * @property reactionCounts Aggregated counts of user reactions (e.g., {"like": 5}).
 * @property commentCount Total number of comments.
 */
@Serializable
@IgnoreExtraProperties
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
    @Serializable(with = TimestampSerializer::class)
    val attachedEventStartDate: Timestamp? = null,
    val sourceGroupId: String? = null,
    val sourceGroupName: String? = null,
    val sourceMessageId: String? = null,
    val sourceScoreEntryId: String? = null,
    val sourceTribeId: String? = null,
    val sourceTribeName: String? = null,
    val sourceScorePoints: Int? = null,
    @get:PropertyName("isPublished")
    @set:PropertyName("isPublished")
    var isPublished: Boolean = true,

    @get:PropertyName("isPinned")
    @set:PropertyName("isPinned")
    var isPinned: Boolean = false,
    val allowComments: Boolean = true,
    val allowReactions: Boolean = true,
    val createdBy: String? = null,
    val createdByName: String? = null,
    val createdByRole: UserRole? = null,
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp = Timestamp.now(),
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val expiresAt: Timestamp? = null,
    val reactionCounts: Map<String, Int> = emptyMap(),
    val commentCount: Int = 0,
    var imageURLs: List<String> = emptyList(),
    var reportCount: Int = 0,
    @get:PropertyName("isHidden")
    @set:PropertyName("isHidden")
    var hidden: Boolean = false,
) {
    /**
     * Checks if the post has passed its expiration date.
     */
    val isExpired: Boolean
        get() = expiresAt?.let { it.seconds < Timestamp.now().seconds } ?: false

    /**
     * Business logic to determine if a specific user is authorized to view this post.
     */
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
