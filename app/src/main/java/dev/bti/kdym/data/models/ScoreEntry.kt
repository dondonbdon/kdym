package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

/**
 * Represents a historical record of points awarded to a tribe.
 *
 * @property id Unique identifier for the entry.
 * @property campId Associated camp session ID.
 * @property tribeId ID of the tribe receiving points.
 * @property tribeName Cached name of the tribe.
 * @property eventId Optional ID of the competition or event.
 * @property eventTitle Cached title of the event.
 * @property points Number of points awarded (can be negative).
 * @property reason Human-readable justification for the points.
 * @property createdBy UID of the admin/manager who awarded the points.
 * @property createdByName Cached name of the awarder.
 * @property createdAt Timestamp of creation.
 */
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

/**
 * Represents a specific competition within the "Tribe Wars" series.
 *
 * @property id Unique identifier for the event.
 * @property campId Associated camp session ID.
 * @property title Name of the game or competition.
 * @property description Rules or context for the event.
 * @property location Where the event takes place.
 * @property startDate Scheduled time.
 * @property maxPoints Maximum possible points that can be awarded.
 * @property status Lifecycle state (e.g., upcoming, active).
 * @property createdBy UID of the creator.
 * @property createdAt Timestamp of creation.
 * @property updatedAt Last modification timestamp.
 */
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
