package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import dev.bti.kdym.data.local.serializers.TimestampSerializer

/**
 * Represents a user of the KDYM application.
 * Contains profile information, role-based permissions, and camp-specific associations.
 */
@Serializable
@IgnoreExtraProperties
data class AppUser(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val photoURL: String? = null,
    val phoneNumber: String? = null,
    val bio: String? = null,

    val churchId: String? = null,
    val churchName: String? = null,
    val churchCity: String? = null,
    val pastorName: String? = null,

    val role: String = "public",
    val accessStatus: String = "public",

    val campId: String? = null,
    val tribeId: String? = null,
    val groupIds: List<String> = emptyList(),

    val requestedCampId: String? = null,
    val requestedRole: String? = null,
    @Serializable(with = TimestampSerializer::class)
    val requestedAt: Timestamp? = Timestamp.now(),

    val guessedTribe: String? = null,

    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,

    val fcmTokens: List<String>? = emptyList(),
    val notificationPreferences: NotificationPreferences? = NotificationPreferences(),

    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp? = Timestamp.now(),
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = Timestamp.now(),
    @Serializable(with = TimestampSerializer::class)
    val lastActiveAt: Timestamp? = Timestamp.now(),

    var phoneVerified: Boolean = false,
    var emailVerified: Boolean = false,
    var reportCount: Int = 0,
) {
    // =========================================================================
    // IN-MEMORY HELPERS (Strictly excluded from Firestore to prevent bloat)
    // =========================================================================

    @get:Exclude
    val roleEnum: UserRole get() = UserRole.fromString(role)

    @get:Exclude
    val statusEnum: AccessStatus get() = AccessStatus.fromString(accessStatus)

    @get:Exclude
    val isPublic: Boolean get() = roleEnum.isPublic

    @get:Exclude
    val hasApprovedCampAccess: Boolean get() = accessStatus == "approved" && roleEnum.canAccessCampContent

    // =========================================================================
    // PERSISTED FLAGS (Derived from role for integrity, but stored for iOS)
    // =========================================================================

    @get:PropertyName("isAdmin")
    val isAdmin: Boolean get() = roleEnum.isAdmin

    @get:PropertyName("isLeader")
    val isLeader: Boolean get() = roleEnum.isLeader

    @get:Exclude
    val initials: String get() {
        val parts = displayName.split(" ").filter { it.isNotBlank() }
        return if (parts.size >= 2) {
            "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        } else if (parts.isNotEmpty()) {
            parts[0].take(2).uppercase()
        } else {
            "KD"
        }
    }
}