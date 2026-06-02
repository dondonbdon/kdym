package dev.bti.kdym.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.data.models.FeedPostPriority
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import dev.bti.kdym.R
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

@Composable
fun FeedPostCard(
    post: FeedPost,
    userReaction: String?,
    onReactionClick: (postId: String, reaction: String) -> Unit,
    onClick: () -> Unit = {}
) {
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }

    if (selectedImageIndex != null) {
        ImagePagerDialog(
            images = post.imageURLs,
            initialPage = selectedImageIndex!!,
            onDismiss = { selectedImageIndex = null }
        )
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (post.isPinned) {
                    Surface(
                        color = RedAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = RedAccent,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PINNED",
                                color = RedAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                }

                Surface(
                    color = (if (post.priority == FeedPostPriority.important) Color(0xFFEAB308) else Color(
                        0xFF22D3EE
                    )).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (post.priority == FeedPostPriority.important) Icons.Default.Info else Icons.Default.Campaign,
                            contentDescription = null,
                            tint = if (post.priority == FeedPostPriority.important) Color(0xFFEAB308) else Color(
                                0xFF22D3EE
                            ),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.priority.name.uppercase(),
                            color = if (post.priority == FeedPostPriority.important) Color(
                                0xFFEAB308
                            ) else Color(0xFF22D3EE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = post.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.body,
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontFamily = QuickSandFontFamily
            )

            if (post.imageURLs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                PostImageGrid(
                    imageURLs = post.imageURLs,
                    onImageClick = { index -> selectedImageIndex = index }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReactionButton(
                        icon = Icons.Default.ThumbUp,
                        count = post.reactionCounts["like"] ?: 0,
                        isActive = userReaction == "like",
                        onClick = { onReactionClick(post.id, "like") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReactionButton(
                        painter = painterResource(id = R.drawable.ic_pray),
                        count = post.reactionCounts["pray"] ?: 0,
                        isActive = userReaction == "pray",
                        onClick = { onReactionClick(post.id, "pray") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReactionButton(
                        icon = Icons.Default.LocalFireDepartment,
                        count = post.reactionCounts["fire"] ?: 0,
                        isActive = userReaction == "fire",
                        onClick = { onReactionClick(post.id, "fire") }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.commentCount.toString(),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.createdByName ?: "Unknown",
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )

                val formattedTime = SimpleDateFormat("HH:mm", LocalLocale.current.platformLocale)
                    .format(post.createdAt.toDate())
                Text(
                    text = formattedTime,
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}

@Composable
fun PostImageGrid(imageURLs: List<String>, onImageClick: (Int) -> Unit) {
    val count = imageURLs.size
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
    ) {
        when (count) {
            1 -> {
                ImageItem(url = imageURLs[0], modifier = Modifier.aspectRatio(16/9f)) { onImageClick(0) }
            }
            2 -> {
                Row(modifier = Modifier.height(200.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ImageItem(url = imageURLs[0], modifier = Modifier.weight(1f).fillMaxHeight()) { onImageClick(0) }
                    ImageItem(url = imageURLs[1], modifier = Modifier.weight(1f).fillMaxHeight()) { onImageClick(1) }
                }
            }
            3 -> {
                Row(modifier = Modifier.height(200.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ImageItem(url = imageURLs[0], modifier = Modifier.weight(1f).fillMaxHeight()) { onImageClick(0) }
                    Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ImageItem(url = imageURLs[1], modifier = Modifier.weight(1f).fillMaxWidth()) { onImageClick(1) }
                        ImageItem(url = imageURLs[2], modifier = Modifier.weight(1f).fillMaxWidth()) { onImageClick(2) }
                    }
                }
            }
            else -> {
                // 4 or more
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.height(140.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ImageItem(url = imageURLs[0], modifier = Modifier.weight(1f).fillMaxHeight()) { onImageClick(0) }
                        ImageItem(url = imageURLs[1], modifier = Modifier.weight(1f).fillMaxHeight()) { onImageClick(1) }
                    }
                    Row(modifier = Modifier.height(140.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ImageItem(url = imageURLs[2], modifier = Modifier.weight(1f).fillMaxHeight()) { onImageClick(2) }
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            ImageItem(url = imageURLs[3], modifier = Modifier.fillMaxSize()) { onImageClick(3) }
                            if (count > 4) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .clickable { onImageClick(3) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+${count - 3}",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = QuickSandFontFamily
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
fun ImageItem(url: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ImagePagerDialog(
    images: List<String>,
    initialPage: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { images.size }, initialPage = initialPage)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = images[page],
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            // Indicator
            if (images.size > 1) {
                Row(
                    Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(images.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReactionButton(
    icon: ImageVector? = null,
    painter: Painter? = null,
    count: Int,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() },
        color = if (isActive) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
        shape = CircleShape,
        border = BorderStroke(
            1.dp,
            if (isActive) Color.White else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isActive) Color(0xFFEAB308) else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                painter != null -> {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        tint = if (isActive) Color(0xFFEAB308) else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}
