package dev.bti.kdym.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent

private val KdymColorScheme = darkColorScheme(
    primary = RedAccent,
    onPrimary = Color.White,
    secondary = CyanAccent,
    onSecondary = Background,
    tertiary = Amber,
    background = Background,
    surface = DeepBackground,
    onBackground = Transparent,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,

)

@Composable
fun KdymTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KdymColorScheme,
        typography = Typography,
        content = content
    )
}
