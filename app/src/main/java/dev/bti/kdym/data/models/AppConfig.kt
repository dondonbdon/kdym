package dev.bti.kdym.data.models

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer

/**
 * Global configuration settings for the KDYM app.
 * Controlled by administrators to toggle features and app-wide states.
 *
 * @property campModeEnabled If true, the app switches to "Camp Mode" UI/UX.
 * @property activeCampId The ID of the currently active camp session.
 * @property publicRegistrationOpen Whether new users can register for the camp.
 * @property allowGroupChat Global toggle for group messaging features.
 * @property allowTribeChat Global toggle for tribe messaging features.
 * @property allowCampScheduleVisible Whether the camp schedule is visible to public users.
 * @property maintenanceMode If true, non-admin users are blocked from app features.
 * @property updatedAt Last time the configuration was modified.
 */
@Serializable
@Keep
data class AppConfig(
    val campModeEnabled: Boolean = false,
    val activeCampId: String? = null,
    val publicRegistrationOpen: Boolean = true,
    val newAccountAccessDefault: NewAccountAccessDefault = NewAccountAccessDefault.PUBLIC,
    val allowGroupChat: Boolean = true,
    val allowTribeChat: Boolean = true,
    val allowCampScheduleVisible: Boolean = true,
    val maintenanceMode: Boolean = false,
    val tribeWarsScoreVisible: Boolean = false,
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    val facebookLiveEnabled: Boolean = false,
    val facebookLiveTitle: String? = null,
    val facebookLiveURL: String? = null,
)

@Serializable
@Keep
enum class NewAccountAccessDefault(val value: String) {
    @PropertyName("publicUser")
    PUBLIC("publicUser"),

    @PropertyName("pendingCamper")
    PENDING_CAMPER("pendingCamper"),

    @PropertyName("approvedCamper")
    APPROVED_CAMPER("approvedCamper");
}
