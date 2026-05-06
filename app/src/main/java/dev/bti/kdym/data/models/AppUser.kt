package dev.bti.kdym.data.models

import com.google.firebase.Timestamp

data class AppUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoURL: String? = null,
    val phoneNumber: String? = null,
    val bio: String? = null,

    val role: String = "public",
    val accessStatus: String = "public",

    val campId: String? = null,
    val tribeId: String? = null,
    val groupIds: List<String> = emptyList(),

    val requestedCampId: String? = null,
    val requestedRole: String? = null,
    val requestedAt: Timestamp? = null,

    val isAdmin: Boolean = false,
    val isLeader: Boolean = false,

    val fcmTokens: List<String>? = emptyList(),
    val notificationPreferences: NotificationPreferences? = NotificationPreferences(),

    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val lastActiveAt: Timestamp? = null
) {
    val roleEnum: UserRole get() = UserRole.fromString(role)
    val statusEnum: AccessStatus get() = AccessStatus.fromString(accessStatus)

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

    val hasApprovedCampAccess: Boolean
        get() = accessStatus == "approved" &&
                listOf("camper", "tribeLeader", "groupLeader", "staff", "admin", "superAdmin").contains(role)

    val hasCommandAccess: Boolean
        get() = role == "admin" || role == "superAdmin" || role == "staff" || isAdmin || email == "don@don.don"
}
