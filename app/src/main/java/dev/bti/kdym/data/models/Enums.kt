package dev.bti.kdym.data.models

import kotlinx.serialization.Serializable

/**
 * Defines the permissions and authorization levels for users.
 */
@Serializable
enum class UserRole(val title: String) {
    /** Basic authenticated user with no specific camp access. */
    `public`("Public"),
    /** Registered user awaiting administrator approval. */
    pending("Pending"),
    /** Approved attendee of the camp. */
    camper("Camper"),
    /** Approved staff member. */
    staff("Worker"),
    /** Local church pastor. */
    pastor("Pastor"),
    /** User with oversight responsibilities for group. */
    groupLeader("Group Leader"),
    /** User with oversight responsibilities for tribes. */
    tribeLeader("Tribe Leader"),
    /** Full system administrator. */
    admin("Admin"),
    /** Global super administrator with full system control. */
    superAdmin("Super Admin");

    val isPublic: Boolean
        get() = this == `public`

    val isPending: Boolean
        get() = this == pending

    val isCamper: Boolean
        get() = this == camper

    val isAdmin: Boolean
        get() = this == admin || this == superAdmin

    val isSuperAdmin: Boolean
        get() = this == superAdmin

    val isLeader: Boolean
        get() = isAdmin || this == tribeLeader || this == groupLeader

    val isGroupLeader: Boolean
        get() = isLeader || this == groupLeader

    val isTribeLeader: Boolean
        get() = isLeader || this == tribeLeader


    val isStaff: Boolean
        get() = isLeader || this == staff

    val canAccessCommand: Boolean
        get() =  isTribeLeader || isGroupLeader

    val canManageCampSettings: Boolean
        get() = isAdmin

    val canManageApprovals: Boolean
        get() = isLeader

    val canManageTribes: Boolean
        get() = isLeader



    val canManageAnnouncements: Boolean
        get() = isLeader

    val canManageGroups: Boolean
        get() = isGroupLeader

    val canManageEvents: Boolean
        get() = isLeader

    val canManagePlay: Boolean
        get() = isTribeLeader || isGroupLeader

    val canAccessCampContent: Boolean
        get() = !isPublic && !isPending

    companion object {
        val publicUser: UserRole = `public`

        /**
         * Parses a string into a [UserRole], defaulting to [public] if no match is found.
         */
        fun fromString(value: String?): UserRole =
            entries.find {
                it.name.equals(value, ignoreCase = true) ||
                        it.title.equals(value, ignoreCase = true)
            } ?: `public`
    }
}

/**
 * Represents the lifecycle of a user's registration and access.
 */
@Serializable
enum class AccessStatus(val title: String) {
    `public`("Public Account"),
    pending("Camp Access Pending"),
    approved("Camp Access Approved"),
    rejected("Camp Access Not Approved"),
    suspended("Suspended");

    companion object {
        /**
         * Parses a string into an [AccessStatus], defaulting to [public] if no match is found.
         */
        fun fromString(value: String?): AccessStatus = entries.find { it.name == value } ?: `public`
    }
}

/**
 * Urgency levels for system announcements.
 */
@Serializable
enum class AnnouncementPriority(val title: String) {
    normal("Normal"),
    important("Important"),
    urgent("Urgent")
}

/**
 * Intended recipients for announcements.
 */
@Serializable
enum class AnnouncementAudience(val title: String) {
    everyone("Everyone"),
    campers("Campers"),
    leaders("Leaders"),
    admins("Admins"),
    tribe("Specific Tribe"),
    group("Specific Group")
}

/**
 * Functional categories for groups.
 */
@Serializable
enum class AppGroupType(val title: String) {
    tribe("Tribe"),
    cabin("Cabin"),
    leadership("Leadership"),
    volunteer("Volunteer"),
    prayer("Prayer"),
    general("General"),
    custom("Custom")
}

/**
 * Visibility scope for feed posts.
 */
@Serializable
enum class FeedPostAudience(val title: String) {
    everyone("Everyone"),
    campers("Campers"),
    leaders("Leaders"),
    admins("Admins"),
    tribe("Specific Tribe"),
    group("Specific Group")
}

/**
 * Visual styling and metadata flags for feed posts.
 */
@Serializable
enum class FeedPostPriority(val title: String) {
    normal("Normal"),
    important("Important"),
    urgent("Urgent"),
    schedule("Schedule"),
    link("Link"),
    vote("Vote")
}

/**
 * Origin of a feed post.
 */
@Serializable
enum class FeedPostSource(val title: String) {
    manual("Manual"),
    announcement("Announcement"),
    groupMessage("Group Message"),
    tribeWarScore("Tribe Wars"),
    eventSchedule("Schedule"),
    poll("Poll")
}

/**
 * MIME-like categories for group attachments.
 */
@Serializable
enum class GroupAttachmentType {
    image, video, file, link
}

/**
 * Functional categories for calendar events.
 */
@Serializable
enum class EventCategory(val title: String) {
    rally("Rally"),
    convention("Convention"),
    camp("Camp"),
    service("Service"),
    tribeWars("Tribe Wars"),
    meeting("Meeting"),
    social("Social"),
    other("Other")
}

/**
 * Types of content available in the "Play" (media) section.
 */
@Serializable
enum class PlayKind(val title: String, val singularTitle: String) {
    video("Videos", "Video"),
    audio("Audio", "Audio"),
    gallery("Gallery", "Photo");

    val rawValue: String get() = name
}

/**
 * UI display modes for media items.
 */
@Serializable
enum class PlayLayout(val title: String) {
    featuredWide("Featured 16:9"),
    shortform("Shortform 9:16"),
    audio("Audio"),
    photo("Photo");

    val rawValue: String get() = name
}

/**
 * Lifecycle states for tribe war competitions.
 */
@Serializable
enum class TribeWarEventStatus(val title: String) {
    upcoming("Upcoming"),
    active("Active"),
    completed("Completed"),
    archived("Archived")
}
