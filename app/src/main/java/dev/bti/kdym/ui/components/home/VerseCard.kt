package dev.bti.kdym.ui.components.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun VerseCard(isCampMode: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "verseAnim")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val accentColor = if (isCampMode) Color(0xFF10B981) else Color(0xFF22D3EE)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = floatAnim },
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        borderColor = accentColor.copy(alpha = 0.1f * glowAnim)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JOEL 2:28",
                    color = accentColor.copy(alpha = glowAnim),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = if (isCampMode) "ACTIVE" else "PROMISE",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"I will pour out my spirit upon all flesh.\"",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 36.sp,
                fontFamily = RubikFontFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "THESE ARE THE LAST DAYS",
                color = RedAccent.copy(alpha = glowAnim),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = RubikFontFamily
            )
        }
    }
}
