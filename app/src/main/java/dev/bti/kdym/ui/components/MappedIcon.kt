package dev.bti.kdym.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


/**
 * Reusable Composable that takes the iOS string and renders the correct Android icon.
 */
@Composable
fun MappedIcon(
    iosName: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) {
    val iconVector = mapStringToIcon(iosIconName = iosName)

    Icon(
        imageVector = iconVector,
        contentDescription = contentDescription ?: "Mapped icon for $iosName",
        modifier = modifier.size(24.dp),
        tint = tint
    )
}

/**
 * Maps the Android Material ImageVector back to an iOS SF Symbol string.
 */
fun mapIconToSFString(icon: ImageVector): String {
    return when (icon) {
        // Communication & People
        Icons.Filled.Forum -> "bubble.left.and.bubble.right.fill"
        Icons.Filled.Groups -> "person.3.fill"
        Icons.Filled.ConnectWithoutContact -> "person.2.wave.2.fill"
        Icons.Filled.Campaign -> "megaphone.fill"
        Icons.Filled.FamilyRestroom -> "figure.2.and.child.holdinghands"

        // Places & Buildings
        Icons.Filled.LocationCity -> "building.2.fill"
        Icons.Filled.AccountBalance -> "building.columns.fill"
        Icons.Filled.Home -> "house.fill"
        Icons.Filled.Stadium -> "sportscourt.fill"

        // Media & Art
        Icons.Filled.Brush -> "paintbrush.pointed.fill"
        Icons.Filled.CameraAlt -> "camera.fill"
        Icons.Filled.Collections -> "photo.stack.fill"
        Icons.Filled.Videocam -> "video.fill"
        Icons.Filled.Mic -> "music.mic"
        Icons.AutoMirrored.Filled.QueueMusic -> "music.note.list"
        Icons.Filled.Audiotrack -> "guitars.fill"
        Icons.Filled.Piano -> "pianokeys"

        // Elements, Effects & Objects
        Icons.Filled.CleanHands -> "hands.sparkles.fill"
        Icons.Filled.Whatshot -> "flame.fill"
        Icons.Filled.WaterDrop -> "drop.fill"
        Icons.Filled.Bolt -> "bolt.fill"
        Icons.Filled.AutoAwesome -> "sparkles"
        Icons.Filled.Book -> "book.closed.fill"

        // Badges, Status & Action
        Icons.Filled.Shield -> "shield.fill"
        Icons.Filled.Verified -> "checkmark.seal.fill"
        Icons.Filled.Star -> "star.fill"
        Icons.Filled.WorkspacePremium -> "crown.fill"
        Icons.Filled.LocalHospital -> "cross.fill"
        Icons.Filled.Favorite -> "heart.fill"
        Icons.Filled.Flag -> "flag.fill"
        Icons.Filled.EmojiEvents -> "trophy.fill"
        Icons.AutoMirrored.Filled.DirectionsRun -> "figure.run"

        else -> "bubble.left.and.bubble.right.fill"
    }
}

/**
 * Maps the iOS SF Symbol string from the database to an Android Material ImageVector.
 */
fun mapStringToIcon(iosIconName: String): ImageVector {
    return when (iosIconName) {
        // Communication & People
        "bubble.left.and.bubble.right.fill" -> Icons.Filled.Forum
        "person.3.fill" -> Icons.Filled.Groups
        "person.2.wave.2.fill" -> Icons.Filled.ConnectWithoutContact
        "megaphone.fill" -> Icons.Filled.Campaign
        "figure.2.and.child.holdinghands" -> Icons.Filled.FamilyRestroom

        // Places & Buildings
        "building.2.fill" -> Icons.Filled.LocationCity
        "building.columns.fill" -> Icons.Filled.AccountBalance
        "house.fill" -> Icons.Filled.Home
        "sportscourt.fill" -> Icons.Filled.Stadium

        // Media & Art
        "paintbrush.pointed.fill" -> Icons.Filled.Brush
        "camera.fill" -> Icons.Filled.CameraAlt
        "photo.stack.fill" -> Icons.Filled.Collections
        "video.fill" -> Icons.Filled.Videocam
        "music.mic" -> Icons.Filled.Mic
        "music.note.list" -> Icons.AutoMirrored.Filled.QueueMusic
        "guitars.fill" -> Icons.Filled.Audiotrack
        "pianokeys" -> Icons.Filled.Piano

        // Elements, Effects & Objects
        "hands.sparkles.fill" -> Icons.Filled.CleanHands
        "flame.fill" -> Icons.Filled.Whatshot
        "drop.fill" -> Icons.Filled.WaterDrop
        "bolt.fill" -> Icons.Filled.Bolt
        "sparkles" -> Icons.Filled.AutoAwesome
        "book.closed.fill" -> Icons.Filled.Book

        // Badges, Status & Action
        "shield.fill" -> Icons.Filled.Shield
        "checkmark.seal.fill" -> Icons.Filled.Verified
        "star.fill" -> Icons.Filled.Star
        "crown.fill" -> Icons.Filled.WorkspacePremium
        "cross.fill" -> Icons.Filled.LocalHospital
        "heart.fill" -> Icons.Filled.Favorite
        "flag.fill" -> Icons.Filled.Flag
        "trophy.fill" -> Icons.Filled.EmojiEvents
        "figure.run" -> Icons.AutoMirrored.Filled.DirectionsRun

        // Fallback for unrecognized strings
        else -> Icons.AutoMirrored.Filled.Help
    }
}

