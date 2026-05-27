package dev.bti.kdym.ui.components.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage

@Composable
fun OverlappingPhotos(
    photos: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        photos.take(3).forEachIndexed { index, photoUrl ->
            val rotation = when (index) {
                0 -> -8f
                1 -> 0f
                2 -> 8f
                else -> 0f
            }
            val translationX = when (index) {
                0 -> (-60).dp
                1 -> 0.dp
                2 -> 60.dp
                else -> 0.dp
            }

            AsyncImage(
                model = photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 140.dp, height = 180.dp)
                    .offset(x = translationX)
                    .rotate(rotation)
                    .zIndex(if (index == 1) 1f else 0f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color.Black.copy(0.2f), RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
