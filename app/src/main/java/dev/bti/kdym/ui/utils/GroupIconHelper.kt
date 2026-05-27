package dev.bti.kdym.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Helper to map iOS SF Symbol names to Android Material Icons.
 */
object GroupIconHelper {
    fun getGroupIcon(iconName: String?): ImageVector {
        return when (iconName) {
            "bolt.fill" -> Icons.Default.Bolt
            "bubble.left.and.bubble.right.fill" -> Icons.Default.Forum
            "person.3.fill" -> Icons.Default.Groups
            "shield.fill" -> Icons.Default.Shield
            "megaphone.fill" -> Icons.Default.Campaign
            "magnifyingglass" -> Icons.Default.Search
            "flag.fill" -> Icons.Default.Flag
            "star.fill" -> Icons.Default.Star
            "heart.fill" -> Icons.Default.Favorite
            "bell.fill" -> Icons.Default.Notifications
            "gearshape.fill" -> Icons.Default.Settings
            "person.fill" -> Icons.Default.Person
            "lock.fill" -> Icons.Default.Lock
            "calendar" -> Icons.Default.CalendarToday
            else -> Icons.AutoMirrored.Filled.Chat
        }
    }
}
