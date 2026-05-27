//package dev.bti.kdym.ui.components.home
//
//import androidx.compose.animation.animateColorAsState
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import dev.bti.kdym.ui.theme.Background
//import dev.bti.kdym.ui.theme.RubikFontFamily
//
//@Composable
//fun HomeBackground(
//    accentColor: Color,
//    themeText: String,
//    scrollOffset: Float, // 0 to 1 between pages
//    content: @Composable () -> Unit
//) {
//    val animatedAccent by animateColorAsState(
//        targetValue = accentColor,
//        animationSpec = tween(1000),
//        label = "accentColor"
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Background)
//    ) {
//        // Dynamic Glows
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(animatedAccent.copy(alpha = 0.15f), Color.Transparent),
//                    center = Offset(size.width * 0.8f, size.height * 0.2f),
//                    radius = size.width * 1.2f
//                ),
//                center = Offset(size.width * 0.8f, size.height * 0.2f),
//                radius = size.width * 1.2f
//            )
//
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(animatedAccent.copy(alpha = 0.1f), Color.Transparent),
//                    center = Offset(size.width * 0.2f, size.height * 0.8f),
//                    radius = size.width * 1.0f
//                ),
//                center = Offset(size.width * 0.2f, size.height * 0.8f),
//                radius = size.width * 1.0f
//            )
//        }
//
//        // Faint Large Text Background
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center,
//                modifier = Modifier.graphicsLayer {
//                    translationY = -scrollOffset * 250
//                    translationX = scrollOffset * 100 // Horizontal movement added
//                }
//            ) {
//                repeat(5) { // Added one more repeat for better coverage
//                    Text(
//                        text = themeText.uppercase(),
//                        color = Color.White.copy(alpha = 0.03f),
//                        fontSize = 120.sp,
//                        fontWeight = FontWeight.Black,
//                        fontFamily = RubikFontFamily,
//                        letterSpacing = 8.sp,
//                        lineHeight = 110.sp,
//                        modifier = Modifier.graphicsLayer {
//                            // Alternate horizontal offset for each row
//                            translationX = if (it % 2 == 0) -scrollOffset * 50 else scrollOffset * 50
//                        }
//                    )
//                }
//            }
//        }
//
//        content()
//    }
//}

package dev.bti.kdym.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import kotlin.random.Random
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.QuickSandFontFamily

@Composable
fun HomeBackground(
    accentColor: Color,
    themeText: String,
    dragOffset: Float,
    campIndex: Int,
    content: @Composable () -> Unit
) {
    // Generate a list of randomized configurations for the background text based on the camp index
    // so every camp feels slightly different, but remains consistent when returning to it.
    val backgroundTexts = remember(campIndex) {
        List(3) { _ -> // Reduced from 6 to 3 for sparseness
            TextConfig(
                xOffsetFactor = Random.nextInt(-100, 101).toFloat(),
                yOffsetFactor = Random.nextInt(-250, 251).toFloat(),
                rotation = Random.nextInt(-35, 36).toFloat(),
                scaleFactor = Random.nextDouble(1.2, 2.5).toFloat(), // Increased scale for bigger text
                // Determines how this specific text reacts to the wind/drag
                windMultiplierX = Random.nextInt(-10, 11).toFloat() / 10f,
                windMultiplierY = Random.nextInt(-15, 16).toFloat() / 10f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F0F),
                        accentColor.copy(alpha = 0.08f),
                        Color(0xFF0F0F0F)
                    )
                )
            )
    ) {
        // Render scattered windy text
        backgroundTexts.forEach { config ->
            Text(
                text = themeText.uppercase(),
                color = accentColor.copy(alpha = 0.03f), // Even fainter as it's bigger
                fontSize = 80.sp, // Increased from 40.sp
                fontFamily = QuickSandFontFamily,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        // Apply the base scattered positions
                        val baseX = config.xOffsetFactor
                        val baseY = config.yOffsetFactor

                        // Add the drag offset mapped to a windy movement
                        translationX = baseX + (dragOffset * config.windMultiplierX)
                        translationY = baseY + (dragOffset * config.windMultiplierY)

                        // Text remains angled and randomized
                        rotationZ = config.rotation + (dragOffset * 0.01f)
                        scaleX = config.scaleFactor
                        scaleY = config.scaleFactor
                    }
            )
        }

        // The foreground content (HomeScreen cards)
        content()
    }
}

data class TextConfig(
    val xOffsetFactor: Float,
    val yOffsetFactor: Float,
    val rotation: Float,
    val scaleFactor: Float,
    val windMultiplierX: Float,
    val windMultiplierY: Float
)