package dev.bti.kdym.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.CyanAccent
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily

@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 120.sp,
    color: Color = Color.White
) {
    Box(modifier = modifier) {
        // Red Glitch Offset
        Text(
            text = text,
            fontFamily = RubikGlitchFontFamily,
            fontSize = fontSize,
            color = RedAccent.copy(alpha = 0.4f),
            modifier = Modifier.offset(x = (-2).dp, y = 1.dp)
        )
        
        // Cyan Glitch Offset
        Text(
            text = text,
            fontFamily = RubikGlitchFontFamily,
            fontSize = fontSize,
            color = CyanAccent.copy(alpha = 0.3f),
            modifier = Modifier.offset(x = 2.dp, y = (-1).dp)
        )
        
        // Main Text
        Text(
            text = text,
            fontFamily = RubikGlitchFontFamily,
            fontSize = fontSize,
            color = color
        )
    }
}
