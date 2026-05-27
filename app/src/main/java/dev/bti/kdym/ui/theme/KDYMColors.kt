package dev.bti.kdym.ui.theme

import androidx.compose.ui.graphics.Color

val KDYM_PALETTE = listOf(
    "#67E8F9", // Cyan 300
    "#EF4444", // Red 500
    "#FBBF24", // Amber 400
    "#33D17A", // Green (Custom)
    "#6D4AFF", // Purple (Custom)
    "#F472B6", // Pink 400
    "#FB923C", // Orange 400
    "#A3E635", // Lime 400
    "#38BDF8", // Sky 400
    "#C084FC", // Purple 400
    "#FACC15", // Yellow 400
    "#14B8A6", // Teal 500
    "#F43F5E", // Rose 500
    "#8B5CF6", // Violet 500
    "#22C55E", // Green 500
    "#EAB308"  // Yellow 600
)

fun String.toColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color.White
    }
}
