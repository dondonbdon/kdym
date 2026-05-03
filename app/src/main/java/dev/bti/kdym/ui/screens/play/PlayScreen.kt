package dev.bti.kdym.ui.screens.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.PlayItem
import dev.bti.kdym.ui.components.FeaturedWideMediaCard
import dev.bti.kdym.ui.components.GlitchText
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.VerticalMediaCard
import dev.bti.kdym.ui.theme.RubikFontFamily

@Composable
fun PlayScreen() {
    val mockPlayItems = listOf(
        PlayItem(title = "Outpour Recap", kind = "video", layout = "featuredWide"),
        PlayItem(title = "Convention Moments", kind = "video", layout = "shortform"),
        PlayItem(title = "Camp 2025 Highlight", kind = "video", layout = "shortform"),
        PlayItem(title = "Tribe Wars Teaser", kind = "video", layout = "shortform"),
        PlayItem(title = "Worship Night", kind = "video", layout = "shortform")
    )

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "KDYM PLAY",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            
            Row {
                Text(
                    text = "PRESS ",
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                GlitchText(text = "PLAY", fontSize = 48.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Capsule Filter
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Videos") })
                FilterChip(selected = false, onClick = {}, label = { Text("Audio") })
                FilterChip(selected = false, onClick = {}, label = { Text("Gallery") })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Featured Card (full width)
                item(span = { GridItemSpan(2) }) {
                    FeaturedWideMediaCard(playItem = mockPlayItems.first())
                }
                
                // Grid items
                items(mockPlayItems.drop(1)) { item ->
                    VerticalMediaCard(playItem = item)
                }
            }
        }
    }
}
