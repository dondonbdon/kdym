package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable
import dev.bti.kdym.data.local.serializers.TimestampSerializer

/**
 * Represents a user of the KDYM application.
 * Contains profile information, role-based permissions, and camp-specific associations.
 */
@Serializable
@IgnoreExtraProperties
data class AppUser(
    val uid: String = "",
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
    val requestedAt: Timestamp? = null,

    val guessedTribe: String? = null,

    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    var isDeleted: Boolean = false,

    val fcmTokens: List<String>? = emptyList(),
    val notificationPreferences: NotificationPreferences? = NotificationPreferences(),

    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val lastActiveAt: Timestamp? = null,
    var phoneVerified: Boolean = false,
    var emailVerified: Boolean = false,
    var reportCount: Int = 0,
) {
    /**
     * Helper to get the strongly-typed role.
     */
    val roleEnum: UserRole get() = UserRole.fromString(role)

    /**
     * Helper to get the strongly-typed access status.
     */
    val statusEnum: AccessStatus get() = AccessStatus.fromString(accessStatus)

    /**
     * Role-based booleans (Phasing out explicit stored flags)
     */
    @get:Exclude
    val isAdmin: Boolean get() = roleEnum.isAdmin
    @get:Exclude
    val isLeader: Boolean get() = roleEnum.isLeader
    @get:Exclude
    val isPublic: Boolean get() = roleEnum.isPublic

    /**
     * Generates initials from the display name for use in placeholders.
     */
    val initials: String
        get() {
            val parts = displayName.split(" ").filter { it.isNotBlank() }
            return if (parts.size >= 2) {
                "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
            } else if (parts.isNotEmpty()) {
                parts[0].take(2).uppercase()
            } else {
                "KD"
            }
        }

    /**
     * Whether the user is fully approved and can view protected camp content.
     */
    val hasApprovedCampAccess: Boolean
        get() = accessStatus == "approved" && roleEnum.canAccessCampContent

    // =========================================================================
    // PERMISSION HELPERS: Bridging Enum Roles and Boolean Flags
    // =========================================================================

    val hasCommandAccess: Boolean
        get() = roleEnum.canAccessCommand

    val canManageCampSettings: Boolean
        get() = roleEnum.canManageCampSettings

    val canManageApprovals: Boolean
        get() = roleEnum.canManageApprovals

    val canManageTribes: Boolean
        get() = roleEnum.canManageTribes

    val canManagePoints: Boolean
        get() = roleEnum.canManagePoints

    val canManageAnnouncements: Boolean
        get() = roleEnum.canManageAnnouncements

    val canManageGroups: Boolean
        get() = roleEnum.canManageGroups
}
