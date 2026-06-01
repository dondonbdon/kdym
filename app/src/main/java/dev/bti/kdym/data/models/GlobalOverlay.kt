package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer

@Serializable
data class GlobalOverlay(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val message: String = "",
    val buttonTitle: String = "Got it",
    val symbol: String = "megaphone.fill",
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = false,
    val excludeAdmins: Boolean = false,
    val campAccessOnly: Boolean = false,
    val targetEmails: List<String> = emptyList(),
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    val senderId: String = ""
)
