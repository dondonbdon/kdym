package dev.bti.kdym.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image // Added for Logo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size // Added for Logo sizing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Added for Logo resource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import dev.bti.kdym.R // Make sure your R class is imported

private val Background = Color(0xFF050505)
private val CyanGlow = Color(0xFF00E5FF).copy(alpha = 0.15f)
private val RedGlow = Color(0xFFFF1744).copy(alpha = 0.15f)

@Composable
fun AnimatedSplash(onSplashFinished: () -> Unit) {
    // 1. Orchestration States
    val globalAlpha = remember { Animatable(1f) } // Controls the exit fade

    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }

    val textOffset = remember { Animatable(30f) }
    val textAlpha = remember { Animatable(0f) }

    // 2. Background Orbit Animation
    val infiniteTransition = rememberInfiniteTransition(label = "splash_bg")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_phase"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // 3. The Choreography Sequence
    LaunchedEffect(Unit) {
        delay(200)

        // Spring the main Logo in
        launch {
            logoAlpha.animateTo(1f, tween(800, easing = EaseOutCubic))
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        // Slide up the smaller text cluster
        delay(400)
        launch {
            textAlpha.animateTo(1f, tween(600, easing = EaseOutQuad))
        }
        launch {
            textOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }

        // Hold the screen
        delay(1800)

        // EXITS: Fade the entire splash screen out smoothly before navigating
        globalAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(500, easing = LinearEasing)
        )

        // Trigger the transition
        onSplashFinished()
    }

    // Apply the global alpha to the root Box for the exit transition
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .alpha(globalAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        // --- EVOLVED OUTPOUR BACKGROUND ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val gridSize = 44.dp.toPx()
            val gridColor = Color.White.copy(alpha = 0.02f * pulseScale)

            var x = 0f
            while (x < width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, height), 1f)
                x += gridSize
            }
            var y = 0f
            while (y < height) {
                drawLine(gridColor, Offset(0f, y), Offset(width, y), 1f)
                y += gridSize
            }

            val cyanX = width * 0.3f + cos(phase) * (width * 0.2f)
            val cyanY = height * 0.3f + sin(phase) * (height * 0.1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanGlow, Color.Transparent),
                    center = Offset(cyanX, cyanY),
                    radius = width * 0.8f * pulseScale
                ),
                center = Offset(cyanX, cyanY),
                radius = width * 0.8f * pulseScale
            )

            val redX = width * 0.7f + cos(phase + Math.PI.toFloat()) * (width * 0.2f)
            val redY = height * 0.7f + sin(phase + Math.PI.toFloat()) * (height * 0.1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RedGlow, Color.Transparent),
                    center = Offset(redX, redY),
                    radius = width * 0.9f * pulseScale
                ),
                center = Offset(redX, redY),
                radius = width * 0.9f * pulseScale
            )
        }

        // --- FOREGROUND CONTENT ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-20).dp)
        ) {
            // Prominent Image Logo
            Image(
                painter = painterResource(id = R.drawable.white_kdym_logo),
                contentDescription = "KDYM Logo",
                modifier = Modifier
                    .size(120.dp) // Adjust this size as needed for your specific asset
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Subdued Typography Cluster
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = textOffset.value.dp)
                    .alpha(textAlpha.value)
            ) {
                Text(
                    text = "KDYM",
                    color = Color.White,
                    fontSize = 20.sp, // Shrunk from 64sp
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "MM XX VI",
                    color = Color(0xFFFF4757),
                    fontSize = 10.sp, // Shrunk from 14sp
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 10.sp,
                )
            }
        }
    }
}