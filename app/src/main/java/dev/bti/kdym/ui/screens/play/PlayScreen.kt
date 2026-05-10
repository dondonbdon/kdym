package dev.bti.kdym.ui.screens.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.components.SegmentedControl
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun PlayScreen(
    viewModel: MainViewModel = viewModel()
) {
    var selectedSegment by remember { mutableStateOf("VIDEOS") }
    val appConfig by viewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(isPrimary = true, isCampMode = isCampMode)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 140.dp)
            ) {

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    PlayTopBar()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    SegmentedControl(
                        segments = listOf("VIDEOS", "AUDIO", "GALLERY"),
                        selectedSegment = selectedSegment,
                        onSegmentSelected = { selectedSegment = it }
                    )
                }

                // Dynamic content based on the selected segment
                when (selectedSegment) {
                    "VIDEOS" -> {
                        item {
                            FeaturedMediaCard()
                        }

                        item {
                            SectionHeader(
                                label = "SHORTFORM",
                                title = "REELS & MOMENTS"
                            )
                        }

                        item {
                            NoContentCard(
                                icon = Icons.Default.PlayCircle,
                                message = "No videos yet",
                                description = "Admins can post vertical videos, recaps, and clips from their gallery."
                            )
                        }
                    }

                    "AUDIO" -> {
                        item {
                            FeaturedMediaCard()
                        }

                        item {
                            SectionHeader(
                                label = "AUDIO ARCHIVE",
                                title = "LISTEN AGAIN"
                            )
                        }

                        item {
                            NoContentCard(
                                icon = Icons.Default.Audiotrack,
                                message = "No audio yet",
                                description = "Admins can post audio recordings, messages, and archive tracks."
                            )
                        }
                    }

                    "GALLERY" -> {
                        item {
                            SectionHeader(
                                label = "GALLERY",
                                title = "PHOTO DROPS"
                            )
                        }

                        item {
                            NoContentCard(
                                icon = Icons.Default.PhotoLibrary,
                                message = "No photos yet",
                                description = "Admins can upload full albums and high-quality photo drops."
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(label: String, title: String) {
    Column {
        Text(
            text = label,
            color = RedAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            fontFamily = RubikFontFamily
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontFamily = RubikFontFamily
        )
    }
}

@Composable
fun PlayTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = null,
                    tint = Color(0xFF22D3EE),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "KDYM PLAY",
                    color = Color(0xFF22D3EE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
            Text(
                text = "PRESS",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                lineHeight = 44.sp
            )
            Text(
                text = "PLAY",
                color = Color.White,
                fontSize = 44.sp,
                fontFamily = RubikGlitchFontFamily,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Videos, worship clips, shortform moments, audio archives, and gallery drops.",
                color = TextSecondary,
                fontSize = 16.sp,
                fontFamily = RubikFontFamily
            )
        }

        Surface(
            modifier = Modifier.size(48.dp),
            color = Color.White,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FeaturedMediaCard() {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        contentPadding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image Placeholder/Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "FEATURED",
                        color = Color(0xFF22D3EE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = RubikFontFamily
                    )

                    Surface(
                        modifier = Modifier.size(40.dp),
                        color = Color.White,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "PLAY",
                        fontFamily = RubikGlitchFontFamily,
                        fontSize = 44.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Outpour Media",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    Text(
                        text = "Recaps. Messages. Audio.",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontFamily = RubikFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun NoContentCard(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String, description: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = RubikFontFamily
            )
        }
    }
}
