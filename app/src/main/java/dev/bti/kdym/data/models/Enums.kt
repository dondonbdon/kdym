package dev.bti.kdym.data.models

enum class UserRole(val title: String) {
    `public`("Public"),
    pending("Pending"),
    camper("Camper"),
    pointManager("Point Manager"),
    leader("Leader"),
    admin("Admin");

    val canAccessCommand: Boolean
        get() = this == admin || this == leader || this == pointManager

    val canManageCampSettings: Boolean
        get() = this == admin

    val canManageApprovals: Boolean
        get() = this == admin || this == leader

    val canManageTribes: Boolean
        get() = this == admin || this == leader

    val canManagePoints: Boolean
        get() = this == admin || this == leader || this == pointManager

    val canManageAnnouncements: Boolean
        get() = this == admin || this == leader

    val canManageGroups: Boolean
        get() = this == admin || this == leader

    val canManageEvents: Boolean
        get() = this == admin || this == leader

    val canManagePlay: Boolean
        get() = this == admin || this == leader

    val canAccessCampContent: Boolean
        get() = this == admin || this == leader || this == pointManager || this == camper

    companion object {
        val publicUser: UserRole = `public`

        fun fromString(value: String?): UserRole = entries.find { it.name.equals(value, ignoreCase = true) } ?: `public`
    }
}

enum class AccessStatus(val title: String) {
    `public`("Public Account"),
    pending("Camp Access Pending"),
    approved("Camp Access Approved"),
    rejected("Camp Access Not Approved"),
    suspended("Suspended");

    companion object {
        fun fromString(value: String?): AccessStatus = entries.find { it.name == value } ?: `public`
    }
}

enum class AnnouncementPriority(val title: String) {
    normal("Normal"),
    important("Important"),
    urgent("Urgent")
}

enum class AnnouncementAudience(val title: String) {
    everyone("Everyone"),
    campers("Campers"),
    leaders("Leaders"),
    admins("Admins"),
    tribe("Specific Tribe"),
    group("Specific Group")
}

enum class AppGroupType(val title: String) {
    tribe("Tribe"),
    cabin("Cabin"),
    leadership("Leadership"),
    volunteer("Volunteer"),
    prayer("Prayer"),
    general("General"),
    custom("Custom")
}

enum class FeedPostAudience(val title: String) {
    everyone("Everyone"),
    campers("Campers"),
    leaders("Leaders"),
    admins("Admins"),
    tribe("Specific Tribe"),
    group("Specific Group")
}

enum class FeedPostPriority(val title: String) {
    normal("Normal"),
    important("Important"),
    urgent("Urgent"),
    schedule("Schedule"),
    link("Link"),
    vote("Vote")
}

enum class FeedPostSource(val title: String) {
    manual("Manual"),
    announcement("Announcement"),
    groupMessage("Group Message"),
    tribeWarScore("Tribe Wars"),
    eventSchedule("Schedule"),
    poll("Poll")
}

enum class GroupAttachmentType {
    image, video, file, link
}

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

enum class PlayKind(val title: String, val singularTitle: String) {
    video("Videos", "Video"),
    audio("Audio", "Audio"),
    gallery("Gallery", "Photo")
}

enum class PlayLayout(val title: String) {
    featuredWide("Featured 16:9"),
    shortform("Shortform 9:16"),
    audio("Audio"),
    photo("Photo")
}

enum class TribeWarEventStatus(val title: String) {
    upcoming("Upcoming"),
    active("Active"),
    completed("Completed"),
    archived("Archived")
}
