package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

/**
 * Represents a request for elevated access to camp-specific features.
 *
 * @property id Unique identifier for the request.
 * @property campId ID of the camp session being requested.
 * @property requesterId UID of the user making the request.
 * @property requesterName Display name of the requester.
 * @property requesterEmail Email of the requester.
 * @property requestedRole The role being applied for (e.g., Camper, Staff).
 * @property status Current status: pending, approved, or rejected.
 * @property createdAt Timestamp when the request was created.
 * @property updatedAt Timestamp of the last status change.
 */
data class CampAccessRequest(
    val id: String = "",
    val campId: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val requesterEmail: String = "",
    val requestedRole: String = "Camper",
    val status: String = "pending",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
