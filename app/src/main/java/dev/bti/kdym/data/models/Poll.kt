package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

/**
 * Represents an interactive poll that can be shared in groups or on the home feed.
 *
 * @property id Unique identifier for the poll.
 * @property question The main question being asked.
 * @property options List of possible answers.
 * @property allowMultipleVotes Whether a user can select more than one option.
 * @property showOnHome Whether the poll should be promoted to the main feed.
 * @property createdBy UID of the author.
 * @property createdAt Timestamp of creation.
 * @property expiresAt Optional timestamp when voting should be disabled.
 * @property totalVotes Aggregated sum of all votes cast.
 * @property groupIds List of group IDs where this poll is visible.
 */
data class Poll(
    val id: String = "",
    val question: String = "",
    val options: List<PollOption> = emptyList(),
    val allowMultipleVotes: Boolean = false,
    val showOnHome: Boolean = false,
    val createdBy: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val expiresAt: Timestamp? = null,
    val totalVotes: Int = 0,
    val groupIds: List<String> = emptyList()
)

/**
 * Represents a single answer option in a [Poll].
 *
 * @property id Unique identifier for the option.
 * @property text The display text of the option.
 * @property voteCount Number of votes this option has received.
 * @property voterIds List of user UIDs who voted for this option.
 */
data class PollOption(
    val id: String = "",
    val title: String = "",
    val voteCount: Int = 0,
    val voterIds: List<String> = emptyList()
)
