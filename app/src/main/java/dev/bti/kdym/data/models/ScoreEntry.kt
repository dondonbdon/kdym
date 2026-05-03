package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class ScoreEntry(
    val id: String = "",
    val campId: String = "",
    val tribeId: String = "",
    val tribeName: String = "",
    val eventId: String? = null,
    val eventTitle: String? = null,
    val points: Int = 0,
    val reason: String = "",
    val createdBy: String? = null,
    val createdByName: String? = null,
    val createdAt: Timestamp? = null
)

data class TribeWarEvent(
    val id: String = "",
    val campId: String = "",
    val title: String = "",
    val description: String? = null,
    val location: String? = null,
    val startDate: Timestamp? = null,
    val maxPoints: Int = 0,
    val status: String = "upcoming", // upcoming, active, completed, archived
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
