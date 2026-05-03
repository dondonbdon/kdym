package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class Event(
    val id: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val description: String = "",
    val location: String? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val imageURL: String? = null,
    val registrationURL: String? = null,
    val category: String = "other", // rally, convention, camp, service, tribeWars, meeting, social, other
    val isCampEvent: Boolean = false,
    val campId: String? = null,
    val isPublished: Boolean = true,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
