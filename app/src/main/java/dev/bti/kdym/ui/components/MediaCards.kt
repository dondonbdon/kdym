package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.PlayItem
import dev.bti.kdym.ui.theme.QuickSandFontFamily

@Composable
fun FeaturedWideMediaCard(playItem: PlayItem, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.fillMaxWidth().height(200.dp),
        cornerRadius = 32.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background placeholder
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.05f)))
            
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            ) {
                Text(
                    text = "FEATURED MOMENT",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = playItem.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }
            
            // Play Button
            Surface(
                modifier = Modifier.align(Alignment.Center).size(64.dp),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                }
            }
        }
    }
}

@Composable
fun VerticalMediaCard(playItem: PlayItem, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.aspectRatio(9f / 16f),
        cornerRadius = 24.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background placeholder
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.05f)))
            
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
            ) {
                Text(
                    text = playItem.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 2
                )
//                Text(
//                    text = playItem. ?: "",
//                    color = TextSecondary,
//                    fontSize = 10.sp,
//                    fontWeight = FontWeight.Medium
//                )
            }
        }
    }
}
