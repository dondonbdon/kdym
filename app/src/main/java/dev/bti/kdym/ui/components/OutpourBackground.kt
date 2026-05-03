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
import dev.bti.kdym.ui.theme.Background
import dev.bti.kdym.ui.theme.CyanGlow
import dev.bti.kdym.ui.theme.RedGlow

@Composable
fun OutpourBackground(
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundPulse")
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
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
            // Cyan Glow at top left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanGlow.copy(alpha = pulseAlpha), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.1f),
                    radius = size.width * 0.8f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.1f),
                radius = size.width * 0.8f
            )
            
            // Red Glow at bottom right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RedGlow.copy(alpha = pulseAlpha), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.9f),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.9f),
                radius = size.width * 0.9f
            )
        }
        
        content()
    }
}
