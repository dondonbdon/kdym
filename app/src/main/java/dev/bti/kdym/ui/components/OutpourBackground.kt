package dev.bti.kdym.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.bti.kdym.ui.theme.Background
import dev.bti.kdym.ui.theme.CyanGlow
import dev.bti.kdym.ui.theme.RedGlow

/**
 * Animated "Outpour" themed background used as the base layer for most screens.
 * Features a digital grid mesh and pulsating radial gradients (Cyan and Red).
 *
 * @param content Composable UI to be rendered on top of the background.
 */
@Composable
fun OutpourBackground(
    gridAlpha: Float = 0.005f,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundPulse")
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Grid Mesh
            val gridSize = 44.dp.toPx()
            val gridColor = Color.White.copy(alpha = gridAlpha)
            
            // Vertical lines
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
                x += gridSize
            }
            
            // Horizontal lines
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 0.5.dp.toPx()
                )
                y += gridSize
            }

            // Cyan Glow at top left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanGlow.copy(alpha = pulseAlpha), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.1f),
                    radius = size.width * 1.2f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.1f),
                radius = size.width * 1.2f
            )
            
            // Red Glow at bottom right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RedGlow.copy(alpha = pulseAlpha), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.9f),
                    radius = size.width * 1.3f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.9f),
                radius = size.width * 1.3f
            )
        }
        
        content()
    }
}
