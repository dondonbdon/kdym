package dev.bti.kdym.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily
import kotlinx.coroutines.delay

@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 64.sp
) {
    var glitchState by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay((1500..4500).random().toLong())
            glitchState = true
            delay((100..250).random().toLong())
            glitchState = false
        }
    }

    val cyanOffsetX by animateFloatAsState(
        targetValue = if (glitchState) (-4..2).random().toFloat() else 0f,
        animationSpec = tween(50),
        label = "cyanX"
    )
    val cyanOffsetY by animateFloatAsState(
        targetValue = if (glitchState) (-2..2).random().toFloat() else 0f,
        animationSpec = tween(50),
        label = "cyanY"
    )

    val redOffsetX by animateFloatAsState(
        targetValue = if (glitchState) (0..5).random().toFloat() else 0f,
        animationSpec = tween(50),
        label = "redX"
    )
    val redOffsetY by animateFloatAsState(
        targetValue = if (glitchState) (-3..1).random().toFloat() else 0f,
        animationSpec = tween(50),
        label = "redY"
    )

    val glitchAlpha by animateFloatAsState(
        targetValue = if (glitchState) 0.8f else 0f,
        animationSpec = tween(80),
        label = "alpha"
    )

    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        fontFamily = RubikGlitchFontFamily,
        fontSize = fontSize,
        lineHeight = fontSize
    )

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxWidthPx = constraints.maxWidth
        val textLayoutResult = remember(text, textStyle, maxWidthPx) {
            textMeasurer.measure(text = text, style = textStyle)
        }
        
        val scaleFactor = if (textLayoutResult.size.width > maxWidthPx && maxWidthPx > 0) {
            maxWidthPx.toFloat() / textLayoutResult.size.width
        } else {
            1f
        }

        Box(modifier = Modifier.scale(scaleFactor)) {
            // Red Shift
            Text(
                text = text,
                fontFamily = RubikGlitchFontFamily,
                fontSize = fontSize,
                lineHeight = fontSize,
                color = Color(0xFFFF4B4B).copy(alpha = glitchAlpha),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.offset(
                    x = redOffsetX.dp,
                    y = redOffsetY.dp
                )
            )

            // Cyan / Light Blue Shift
            Text(
                text = text,
                fontFamily = RubikGlitchFontFamily,
                fontSize = fontSize,
                lineHeight = fontSize,
                color = Color(0xFF88DDFF).copy(alpha = glitchAlpha * 0.9f),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.offset(
                    x = cyanOffsetX.dp,
                    y = cyanOffsetY.dp
                )
            )

            // Main White Text
            Text(
                text = text,
                fontFamily = RubikGlitchFontFamily,
                fontSize = fontSize,
                lineHeight = fontSize,
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.graphicsLayer {
                    translationX = if (glitchState) (-1..1).random().toFloat() else 0f
                }
            )
        }
    }
}
