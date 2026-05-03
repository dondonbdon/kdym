package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class AppConfig(
    val campModeEnabled: Boolean = false,
    val activeCampId: String? = null,
    val publicRegistrationOpen: Boolean = true,
    val allowGroupChat: Boolean = true,
    val allowTribeChat: Boolean = true,
    val allowCampScheduleVisible: Boolean = true,
    val maintenanceMode: Boolean = false,
    val updatedAt: Timestamp? = null
)
