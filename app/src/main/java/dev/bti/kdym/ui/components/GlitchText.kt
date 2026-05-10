package dev.bti.kdym.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.CyanAccent
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily
import kotlinx.coroutines.delay

@Composable
fun GlitchText(text: String, fontSize: TextUnit = 64.sp) {
    var glitchOn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay((2500..6000).random().toLong())
            glitchOn = true
            delay(120) // glitch duration
            glitchOn = false
        }
    }

    val offsetX by animateFloatAsState(
        targetValue = if (glitchOn) (-2..4).random().toFloat() else 0f,
        animationSpec = tween(100),
        label = "offsetX"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (glitchOn) (-1..2).random().toFloat() else 0f,
        animationSpec = tween(100),
        label = "offsetY"
    )

    val redAlpha by animateFloatAsState(
        targetValue = if (glitchOn) 1f else 0f,
        animationSpec = tween(120),
        label = "redAlpha"
    )

    Box {
        // afterimage
        Text(
            text = text,
            fontFamily = RubikGlitchFontFamily,
            fontSize = fontSize,
            lineHeight = fontSize,
            color = Color(0xFFFF6B6B).copy(alpha = 0.5f * redAlpha),
            modifier = Modifier.offset(
                x = (2 * redAlpha).dp,
                y = (-1 * redAlpha).dp
            )
        )

        // main
        Text(
            text = text,
            fontFamily = RubikGlitchFontFamily,
            fontSize = fontSize,
            lineHeight = fontSize,
            color = Color.White,
            modifier = Modifier.offset(
                x = offsetX.dp,
                y = offsetY.dp
            )
        )
    }
}
