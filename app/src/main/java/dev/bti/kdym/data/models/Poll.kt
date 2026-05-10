package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

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

data class PollOption(
    val id: String = "",
    val text: String = "",
    val voteCount: Int = 0,
    val voterIds: List<String> = emptyList()
)
