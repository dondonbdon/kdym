package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

/**
 * Represents a local church or tabernacle in the KDYM network.
 *
 * @property id Unique identifier for the church.
 * @property name Official name of the church.
 * @property pastorName Name of the lead pastor.
 * @property pastorId UID of the pastor's app account (if linked).
 * @property city City where the church is located.
 * @property state State where the church is located.
 * @property address Full physical address.
 * @property memberCount Estimated number of youth members.
 * @property createdAt Registration date in the system.
 */
data class Church(
    val id: String = "",
    val name: String = "",
    val pastorName: String = "",
    val pastorId: String? = null,
    val city: String = "",
    val state: String = "",
    val address: String? = null,
    val memberCount: Int = 0,
    val createdAt: Timestamp? = null
)
