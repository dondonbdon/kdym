package dev.bti.kdym.ui.screens.play

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.bti.kdym.data.models.PlayComment
import dev.bti.kdym.data.models.PlayItem
import dev.bti.kdym.data.models.PlayReaction
import dev.bti.kdym.ui.components.VideoPlayer
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel
import java.util.Locale

// Ensure these imports are at the top

@Composable
fun ClipsPager(
    initialClipId: String,
    kind: String,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel
) {
    val allPlayItems by viewModel.playItems.collectAsState()
    val playItems = remember(allPlayItems, kind) {
        allPlayItems.filter { it.kind == kind }
    }

    val initialPage = remember(playItems, initialClipId) {
        val index = playItems.indexOfFirst { it.id == initialClipId }
        if (index != -1) index else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { playItems.size }
    )

    // FIX: State to immediately kill the video player upon dismissal
    var isDismissing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (playItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF22D3EE))
            }
        } else if (!isDismissing) {
            // FIX: Instantly removes the VideoPlayer from composition when backing out
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val item = playItems[page]
                when (item.kind) {
                    "audio" -> AudioItem(
                        item = item,
                        isActive = pagerState.currentPage == page,
                        viewModel = viewModel
                    )

                    "gallery" -> GalleryItem(
                        item = item,
                        isActive = pagerState.currentPage == page,
                        viewModel = viewModel
                    )

                    else -> ClipItem(
                        item = item,
                        isActive = pagerState.currentPage == page,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Back Button
        IconButton(
            onClick = {
                isDismissing = true // Triggers the unmount
                onNavigateBack()
            },
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .background(Color.Black.copy(0.3f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
fun PlayItemOverlay(
    item: PlayItem,
    viewModel: MainViewModel,
    progress: Float,
    currentPosition: Long = 0L,
    totalDuration: Long = 0L,
    showProgress: Boolean = true,
    onShowComments: () -> Unit
) {
    var showReactions by remember { mutableStateOf(false) }
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // --- UNIFIED VERTICAL INTERACTION PILL ---
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 16.dp),
            color = Color.Black.copy(0.4f), // Darker translucent background
            shape = RoundedCornerShape(32.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Expanding Vertical Emojis
                AnimatedVisibility(
                    visible = showReactions,
                    enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
                ) {
                    val reactions = listOf(
                        "\uD83D\uDE2E" to PlayReaction.WOW,
                        "👍" to PlayReaction.LIKE,
                        "🔥" to PlayReaction.FIRE,
                        "❤️" to PlayReaction.LOVE,
                        "👏" to PlayReaction.CELEBRATE,
                        "\uD83D\uDE4F" to PlayReaction.AMEN
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        // Changed to 2x3 grid
                        reactions.chunked(2).forEach { rowReactions ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                rowReactions.forEach { (emoji, reaction) ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White.copy(0.1f), CircleShape)
                                            .clickable {
                                                item.id?.let { viewModel.togglePlayReaction(it, reaction.rawValue) }
                                                showReactions = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = emoji,
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily.SansSerif, // Fallback for emojis
                                            color = Color.Unspecified,
                                            style = androidx.compose.ui.text.TextStyle.Default
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        // Visual Divider
                        Box(
                            modifier = Modifier
                                .width(48.dp) // Widened to match the grid width
                                .height(1.dp)
                                .background(Color.White.copy(0.2f))
                        )
                    }
                }

                // Heart / Reaction Toggle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { showReactions = !showReactions },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (showReactions) Color.White.copy(0.2f) else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        // Change icon dynamically if you want it filled when liked
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "${item.reactionCounts.values.sum()}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Comments
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onShowComments,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "${item.commentCount}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Optional: You can add a Flag icon here to perfectly match the screenshot
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { item.id?.let { viewModel.reportPlayItem(it) } },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OutlinedFlag,
                            contentDescription = null,
                            tint = Color.White.copy(0.7f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "0", // Replace with report count if tracked
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out this ${item.kind} on KDYM: https://kdym.bti.dev/play/${item.id}"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "${item.shareCount}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Info Overlay (Title, Description, Progress) remains unchanged...
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 60.dp, start = 16.dp, end = 100.dp)
        ) {
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            item.description?.let {
                Text(
                    text = it,
                    color = Color.White.copy(0.8f),
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            if (showProgress) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = QuickSandFontFamily
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(Color.White.copy(0.2f), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .background(Color(0xFF22D3EE), CircleShape)
                        )
                    }

                    Text(
                        text = formatTime(totalDuration),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun AudioItem(
    item: PlayItem,
    isActive: Boolean,
    viewModel: MainViewModel
) {
    var showComments by remember { mutableStateOf(false) }
    var audioProgress by remember { mutableFloatStateOf(0f) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }

    val user by viewModel.user.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        item.mediaURL?.let { url ->
            VideoPlayer(
                videoUrl = url,
                isPlaying = isActive,
                onProgressUpdate = { progress, pos, dur ->
                    audioProgress = progress
                    currentPosition = pos
                    totalDuration = dur
                }
            )
        }

        // Static Cover / Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Black, Color(0xFF1A1A1A)))
                ), contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(240.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = null,
                            tint = Color.White.copy(0.2f),
                            modifier = Modifier.size(100.dp)
                        )
                        if (item.displayThumbnailURL != null) {
                            AsyncImage(
                                model = item.displayThumbnailURL,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "AUDIO ARCHIVE",
                    color = Color(0xFF22D3EE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
        }

        PlayItemOverlay(
            item = item,
            viewModel = viewModel,
            progress = audioProgress,
            currentPosition = currentPosition,
            totalDuration = totalDuration,
            onShowComments = { showComments = true }
        )

        if (showComments) {
            CommentsBottomSheet(
                item = item,
                viewModel = viewModel,
                onDismiss = { showComments = false })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryItem(
    item: PlayItem,
    isActive: Boolean,
    viewModel: MainViewModel
) {
    var showComments by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { item.assets.size })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = 16.dp
        ) { pageIndex ->
            AsyncImage(
                model = item.assets[pageIndex].url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Page Indicator
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp),
            color = Color.Black.copy(0.4f),
            shape = CircleShape
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / ${item.assets.size}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                fontFamily = QuickSandFontFamily
            )
        }

        PlayItemOverlay(
            item = item,
            viewModel = viewModel,
            progress = (pagerState.currentPage + 1).toFloat() / item.assets.size,
            showProgress = false,
            onShowComments = { showComments = true }
        )

        if (showComments) {
            CommentsBottomSheet(
                item = item,
                viewModel = viewModel,
                onDismiss = { showComments = false })
        }
    }
}

@Composable
fun ClipItem(
    item: PlayItem,
    isActive: Boolean,
    viewModel: MainViewModel
) {
    var showComments by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableFloatStateOf(0f) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    val ratio = item.width / item.height

    var showMenu by remember { mutableStateOf(false) }
    val user by viewModel.user.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        item.primaryMediaURL?.let { url ->
            VideoPlayer(
                videoUrl = url,
                isPlaying = isActive,
                modifier = Modifier

                    .aspectRatio(ratio)
                    .align(Alignment.Center),
                onProgressUpdate = { progress, pos, dur ->
                    videoProgress = progress
                    currentPosition = pos
                    totalDuration = dur
                }
            )
        } ?: run {
            if (item.thumbnailURL != null) {
                AsyncImage(
                    model = item.thumbnailURL,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Admin / Report Menu Button
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 16.dp, end = 16.dp)
                .align(Alignment.TopEnd)
        ) {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.background(Color.Black.copy(0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(Color(0xFF111111))
            ) {
                DropdownMenuItem(
                    text = { Text("Report", color = Color.White) },
                    onClick = {
                        item.id?.let { viewModel.reportPlayItem(it) }
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
                if (user?.roleEnum?.canManagePlay == true) {
                    DropdownMenuItem(
                        text = { Text("Edit", color = Color.White) },
                        onClick = { /* Navigate to Edit */ showMenu = false },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (item.isFeatured) "Unmark Featured" else "Mark as Featured",
                                color = Color.White
                            )
                        },
                        onClick = {
                            item.id?.let { viewModel.toggleFeatured(it, item.isFeatured) }
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            item.id?.let { viewModel.deletePlayItem(it) }
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    )
                }
            }
        }

        PlayItemOverlay(
            item = item,
            viewModel = viewModel,
            progress = videoProgress,
            currentPosition = currentPosition,
            totalDuration = totalDuration,
            onShowComments = { showComments = true }
        )

        if (showComments) {
            CommentsBottomSheet(
                item = item,
                viewModel = viewModel,
                onDismiss = { showComments = false })
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

@Composable
fun EmojiReactionPicker(onReactionSelected: (PlayReaction) -> Unit) {
    Surface(
        color = Color.Black.copy(0.8f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val reactions = listOf(
                PlayReaction.LIKE to "❤️",
                PlayReaction.FIRE to "🔥",
                PlayReaction.LOVE to "😍",
                PlayReaction.PRAY to "🙏",
                PlayReaction.CELEBRATE to "🙌",
                PlayReaction.AMEN to "🫡",
                PlayReaction.WOW to "😮"
            )
            reactions.forEach { (reaction, emoji) ->
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clickable { onReactionSelected(reaction) }
                        .padding(4.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(item: PlayItem, viewModel: MainViewModel, onDismiss: () -> Unit) {
    val comments by viewModel.getPlayComments(item.id ?: "").collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }
    val user by viewModel.user.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111111),
        scrimColor = Color.Black.copy(0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(500.dp)
        ) {
            Text(
                text = "COMMENTS",
                color = Color(0xFF22D3EE),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No comments yet.", color = TextSecondary)
                        }
                    }
                } else {
                    items(comments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            isAdmin = user?.roleEnum?.isAdmin == true,
                            onDelete = {
                                item.id?.let { itemId ->
                                    viewModel.deletePlayComment(
                                        itemId,
                                        comment.id
                                    )
                                }
                            },
                            onReport = { /* Report logic */ }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Add a comment", color = Color.White.copy(0.3f)) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(0.05f),
                        unfocusedContainerColor = Color.White.copy(0.05f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            item.id?.let { viewModel.addPlayComment(it, commentText) }
                            commentText = ""
                        }
                    },
                    modifier = Modifier.background(Color(0xFF22D3EE), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: PlayComment,
    isAdmin: Boolean,
    onDelete: () -> Unit,
    onReport: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { showMenu = true })
            }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = comment.userPhotoURL,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.1f)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.userName,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = comment.text, color = Color.White.copy(0.8f), fontSize = 14.sp)
        }

        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text("Report") },
                onClick = { onReport(); showMenu = false }
            )
            if (isAdmin) {
                DropdownMenuItem(
                    text = { Text("Delete", color = Color.Red) },
                    onClick = { onDelete(); showMenu = false }
                )
            }
        }
    }
}
