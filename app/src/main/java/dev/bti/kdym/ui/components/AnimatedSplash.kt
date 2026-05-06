package dev.bti.kdym.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.bti.kdym.R
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplash(onFinished: () -> Unit) {
    var start by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        start = true
        delay(1200)
        onFinished()
    }

    val scale by animateFloatAsState(
        targetValue = if (start) 1f else 0.85f,
        animationSpec = tween(500),
        label = "scale"
    )

    val alphaState by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = tween(500),
        label = "alpha"
    )

    val glitchAlpha by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = tween(150, delayMillis = 400),
        label = "glitch"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {

        // 🔴 glitch layer (slight red offset)
        Image(
            painter = painterResource(R.drawable.white_kdym_logo),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    translationX = 4f * glitchAlpha
                },
            colorFilter = ColorFilter.tint(
                Color(0xFFFF4D4D).copy(alpha = 0.6f * glitchAlpha)
            )
        )

        // ⚪ main logo
        Image(
            painter = painterResource(R.drawable.white_kdym_logo),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = alphaState
                }
        )
    }
}
