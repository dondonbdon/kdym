package dev.bti.kdym.ui.components.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily

@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 44.sp,
    color: Color = Color.White
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glitch")
    
    val offset1 by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 3f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(40, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = modifier.graphicsLayer { 
        scaleX = scaleAnim
        scaleY = scaleAnim
    }) {
        // Red Glitch
        Box(modifier = modifier.graphicsLayer {
            scaleX = scaleAnim
            scaleY = scaleAnim
        }) {
            // Red Glitch
            Text(
                text = text,
                color = Color.Red.copy(alpha = 0.6f),
                fontSize = fontSize,
                fontFamily = RubikGlitchFontFamily,
                fontWeight = FontWeight.Black,
                modifier = Modifier.graphicsLayer {
                    translationX = offset1 * 1.5f
                    alpha = alphaAnim
                }
            )
            // Cyan Glitch
            Text(
                text = text,
                color = Color.Cyan.copy(alpha = 0.6f),
                fontSize = fontSize,
                fontFamily = RubikGlitchFontFamily,
                fontWeight = FontWeight.Black,
                modifier = Modifier.graphicsLayer {
                    translationX = offset2 * 1.5f
                    alpha = alphaAnim
                }
            )
            // Primary Text
            Text(
                text = text,
                color = color,
                fontSize = fontSize,
                fontFamily = RubikGlitchFontFamily,
                fontWeight = FontWeight.Black
            )
        }
    }
}
