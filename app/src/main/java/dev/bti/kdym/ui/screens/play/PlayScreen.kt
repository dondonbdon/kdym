package dev.bti.kdym.ui.screens.play

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.bti.kdym.R
import dev.bti.kdym.data.models.PlayItem
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.components.ShimmerItem
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun PlayScreen(
    onNavigateToCreatePlayItem: () -> Unit,
    onNavigateToClips: (String) -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    var selectedSegment by remember { mutableStateOf("VIDEOS") }
    val appConfig by viewModel.appConfig.collectAsState()
    val user by viewModel.user.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false
    
    val allPlayItems by viewModel.playItems.collectAsState()
    val featuredPlayItems by viewModel.featuredPlayItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val filteredPlayItems = remember(allPlayItems, selectedSegment) {
        val kind = when (selectedSegment) {
            "VIDEOS" -> "video"
            "AUDIO" -> "audio"
            "GALLERY" -> "gallery"
            else -> "video"
        }
        allPlayItems.filter { item ->
            item.kind == kind
        }
    }

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
                    PlayTopBar(onAddClick = onNavigateToCreatePlayItem, showAdd = user?.roleEnum?.canManagePlay == true)
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
                            FeaturedMediaCard(featuredPlayItems.find { it.kind == "video" })
                        }

                        item {
                            SectionHeader(
                                label = "VIDEO",
                                title = "CLIPS & MOMENTS"
                            )
                        }

                        // Grid of clips
                        item {
                            if (filteredPlayItems.isEmpty() && uiState.isLoading) {
                                PlayGridShimmer()
                            } else {
                                ClipsGrid(
                                    items = filteredPlayItems,
                                    onClipClick = { id -> onNavigateToClips("$id/video") }
                                )
                            }
                        }
                    }

                    "AUDIO" -> {
                        item {
                            FeaturedMediaCard(featuredPlayItems.find { it.kind == "audio" })
                        }

                        item {
                            SectionHeader(
                                label = "AUDIO ARCHIVE",
                                title = "LISTEN AGAIN"
                            )
                        }

                        if (filteredPlayItems.isEmpty()) {
                            item {
                                NoContentCard(
                                    icon = Icons.Default.Audiotrack,
                                    message = "No audio yet",
                                    description = "Admins can post audio recordings, messages, and archive tracks."
                                )
                            }
                        } else {
                            items(filteredPlayItems) { item ->
                                AudioClipCard(
                                    item = item,
                                    onClick = { item.id?.let { onNavigateToClips("$it/audio") } }
                                )
                            }
                        }
                    }

                    "GALLERY" -> {
                        item {
                            SectionHeader(
                                label = "GALLERY",
                                title = "PHOTO DROPS"
                            )
                        }

                        if (filteredPlayItems.isEmpty()) {
                            item {
                                NoContentCard(
                                    icon = Icons.Default.PhotoLibrary,
                                    message = "No photos yet",
                                    description = "Admins can upload full albums and high-quality photo drops."
                                )
                            }
                        } else {
                            // Grid for gallery
                            item {
                                if (filteredPlayItems.isEmpty() && uiState.isLoading) {
                                    PlayGridShimmer(columns = 3)
                                } else {
                                    GalleryGrid(
                                        items = filteredPlayItems,
                                        onItemClick = { item -> item.id?.let { onNavigateToClips("${item.id}/gallery") } }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayGridShimmer(columns: Int = 2) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(columns) {
                    ShimmerItem(modifier = Modifier.weight(1f).aspectRatio(if (columns == 2) 0.7f else 1f))
                }
            }
        }
    }
}

@Composable
fun ClipsGrid(items: List<PlayItem>, onClipClick: (String) -> Unit) {
    if (items.isEmpty()) {
        NoContentCard(
            icon = Icons.Default.PlayCircle,
            message = "No videos yet",
            description = "Admins can post vertical videos, recaps, and clips from their gallery."
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { item ->
                    ClipCard(
                        modifier = Modifier.weight(1f),
                        item = item,
                        onClick = { item.id?.let { onClipClick(it) } }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun GalleryGrid(items: List<PlayItem>, onItemClick: (PlayItem) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(0.05f))
                            .clickable { onItemClick(item) }
                    ) {
                        AsyncImage(
                            model = item.displayThumbnailURL,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AudioClipCard(
    item: PlayItem,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        backgroundColor = Color.Black.copy(0.3f),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = Color.White.copy(0.1f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                Text(text = item.publishedAt?.toDate()?.let { 
                    java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(it)
                } ?: "N/A", color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
            }
            item.durationText?.let {
                Text(text = it, color = Color.White.copy(0.6f), fontSize = 12.sp, fontFamily = QuickSandFontFamily)
            }
        }
    }
}

@Composable
fun ClipCard(
    modifier: Modifier = Modifier,
    item: PlayItem,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier
            .height(240.dp)
            .clickable { onClick() },
        backgroundColor = Color.Black.copy(0.3f),
        contentPadding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (item.displayThumbnailURL != null) {
                AsyncImage(
                    model = item.displayThumbnailURL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(0.05f)))
            }
            
            // Duration Badge (Top Right)
            item.durationText?.let { duration ->
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    color = Color.Black.copy(0.6f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = duration,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                
                Column {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.publishedAt?.toDate()?.let { 
                            java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(it)
                        } ?: "N/A",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = QuickSandFontFamily
                    )
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
            fontFamily = QuickSandFontFamily
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily
        )
    }
}

@Composable
fun PlayTopBar(onAddClick: () -> Unit, showAdd: Boolean) {
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
                    fontFamily = QuickSandFontFamily
                )
            }
            Text(
                text = "PRESS",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
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
                fontFamily = QuickSandFontFamily
            )
        }

        if (showAdd) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onAddClick() },
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
}

@Composable
fun FeaturedMediaCard(item: PlayItem? = null) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16/9f),
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        contentPadding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (item?.thumbnailURL != null) {
                AsyncImage(
                    model = item.thumbnailURL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
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
                        fontFamily = QuickSandFontFamily
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
                        text = item?.title ?: "Outpour Media",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = item?.description ?: "Recaps. Messages. Audio.",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontFamily = QuickSandFontFamily
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
        backgroundColor = Color.Black.copy(0.3f)
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
                fontFamily = QuickSandFontFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}
