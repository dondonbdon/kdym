package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer
import java.util.UUID

// ==========================================
// MARK: - Enums
// ==========================================

@Serializable
enum class PlayPublishStatus(val rawValue: String) {
    DRAFT("draft"),
    SCHEDULED("scheduled"),
    PUBLISHED("published"),
    ARCHIVED("archived")
}

@Serializable
enum class PlayReaction(val rawValue: String) {
    LIKE("like"),
    FIRE("fire"),
    LOVE("love"),
    PRAY("pray"),
    CELEBRATE("celebrate"),
    AMEN("amen"),
    WOW("wow")
}

// ==========================================
// MARK: - Sub-Models
// ==========================================

@Serializable
data class PlayMediaAsset(
    val id: String = UUID.randomUUID().toString(),
    val url: String = "",
    val thumbnailURL: String? = null,
    val fileName: String? = null,
    val contentType: String? = null,
    val sizeBytes: Long? = null,
    val durationSeconds: Double? = null,
    val width: Double? = null,
    val height: Double? = null
)

@Serializable
data class PlayComment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "KDYM Member",
    val userPhotoURL: String? = null,
    val text: String = "",
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp = Timestamp.now(),
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,

    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,

    val hidden: Boolean = false,
    @Serializable(with = TimestampSerializer::class)
    val hiddenAt: Timestamp? = null,
    val hiddenBy: String? = null,
    val reportCount: Int = 0,
    @Serializable(with = TimestampSerializer::class)
    val lastReportedAt: Timestamp? = null
) {
    val isVisibleToMembers: Boolean
        get() = !isDeleted && !hidden
}

// ==========================================
// MARK: - Main PlayItem Model
// ==========================================

/**
 * Represents a piece of media content (video, audio, or gallery) in the "Play" section.
 * Maps to the `/playItems` collection in Firestore.
 */
@Serializable
@IgnoreExtraProperties
data class PlayItem(
    @DocumentId val id: String? = null,
    val title: String = "",
    val description: String? = null,
    val kind: String = PlayKind.video.rawValue, // video, audio, gallery
    val mediaURL: String? = null,
    val thumbnailURL: String? = null,
    val artCoverURL: String? = null,
    val assets: List<PlayMediaAsset> = emptyList(),
    val status: String = PlayPublishStatus.PUBLISHED.rawValue, // draft, scheduled, published, archived

    val hidden: Boolean = false,
    @Serializable(with = TimestampSerializer::class)
    val hiddenAt: Timestamp? = null,
    val hiddenBy: String? = null,

    val allowComments: Boolean = true,
    val allowReactions: Boolean = true,

    val commentCount: Int = 0,
    val playCount: Int = 0,
    val shareCount: Int = 0,
    val reportCount: Int = 0,
    val sortRank: Int = 0,

    val width: Float = 9f,
    val height: Float = 16f,

    val durationSeconds: Double? = null,

    val reactionCounts: Map<String, Int> = emptyMap(),

    val groupIds: List<String> = emptyList(),
    val groupNames: List<String> = emptyList(),

    @get:PropertyName("isFeatured")
    @set:PropertyName("isFeatured")
    var isFeatured: Boolean = false,

    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,

    val createdById: String? = null,
    val createdByName: String? = null,

    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp = Timestamp.now(),
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val publishedAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val scheduledAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val lastPlayedAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val lastReportedAt: Timestamp? = null
) {

    // ==========================================
    // MARK: - Computed Properties (Helpers)
    // ==========================================

    val stableId: String
        get() = id ?: "local-$title-${createdAt.seconds}"

    val primaryMediaURL: String?
        get() = mediaURL ?: assets.firstOrNull()?.url

    val displayThumbnailURL: String?
        get() = thumbnailURL ?: artCoverURL ?: assets.firstOrNull()?.thumbnailURL ?: if (kind == PlayKind.gallery.rawValue) assets.firstOrNull()?.url else null

    val hasGroups: Boolean
        get() = groupIds.isNotEmpty()

    val groupLabel: String
        get() = if (groupNames.isEmpty()) "General" else groupNames.joinToString(", ")

    val isVisibleToPublic: Boolean
        get() {
            if (isDeleted || hidden) return false
            if (status == PlayPublishStatus.PUBLISHED.rawValue) return true
            if (status == PlayPublishStatus.SCHEDULED.rawValue && scheduledAt != null) {
                return scheduledAt.toDate().before(java.util.Date())
            }
            return false
        }

    val isScheduledForFuture: Boolean
        get() {
            return status == PlayPublishStatus.SCHEDULED.rawValue &&
                    scheduledAt != null &&
                    scheduledAt.toDate().after(java.util.Date())
        }

    val durationText: String?
        get() {
            if (durationSeconds == null || durationSeconds <= 0) return null
            val total = Math.round(durationSeconds).toInt()
            val minutes = total / 60
            val remaining = total % 60
            return String.format("%d:%02d", minutes, remaining)
        }

    fun belongsToGroup(groupId: String?): Boolean {
        if (groupId == null) return true
        return groupIds.contains(groupId)
    }
}