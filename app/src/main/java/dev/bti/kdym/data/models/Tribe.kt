package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer

/**
 * Represents a major team or "Tribe" within the camp session.
 * Tribes compete for points and have their own leadership and members.
 *
 * @property id Unique identifier for the tribe.
 * @property name Primary name (e.g., "Judah", "Ephraim").
 * @property subtitle Short motto or secondary name.
 * @property colorHex Primary theme color for UI elements.
 * @property iconName Name of the icon representing the tribe.
 * @property campId Associated camp session ID.
 * @property leaderIds List of user UIDs assigned as tribe leaders.
 * @property memberIds List of user UIDs assigned to this tribe.
 * @property totalPoints Current aggregated score.
 * @property rank Current standing relative to other tribes.
 * @property isActive Whether the tribe is currently active in the session.
 * @property createdBy UID of the creator.
 * @property createdAt Timestamp of creation.
 * @property updatedAt Last modification timestamp.
 */
@Serializable
@IgnoreExtraProperties
data class Tribe(
    val id: String = "",
    val name: String = "",
    val subtitle: String? = null,
    val colorHex: String = "#22D3EE",
    val iconName: String? = null,
    val campId: String = "",
    val leaderIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    val totalPoints: Int = 0,
    val rank: Int = 0,
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true,
    val createdBy: String? = null,
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null
) {
    /**
     * Total number of members assigned to the tribe.
     */
    @get:Exclude
    val memberCount: Int
        get() = memberIds.size
}
