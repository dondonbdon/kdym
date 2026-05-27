package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

/**
 * Represents a request to join a private group.
 *
 * @property id Unique identifier for the request.
 * @property groupId ID of the target group.
 * @property groupName Name of the target group.
 * @property requesterId UID of the user requesting to join.
 * @property requesterName Display name of the requester.
 * @property requesterEmail Email address of the requester.
 * @property status Current status: pending, approved, or rejected.
 * @property createdAt Creation timestamp.
 * @property updatedAt Last update timestamp.
 */
data class GroupJoinRequest(
    val id: String = "",
    val groupId: String = "",
    val groupName: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val requesterEmail: String = "",
    val status: String = "pending",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
