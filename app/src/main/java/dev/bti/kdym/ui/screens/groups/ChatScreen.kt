package dev.bti.kdym.ui.screens.groups

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.GroupAttachment
import dev.bti.kdym.data.models.GroupAttachmentType
import dev.bti.kdym.data.models.GroupMessage
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.PollBubble
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onNavigateToInfo: (String) -> Unit,
    onNavigateToCreatePoll: (String) -> Unit,
    viewModel: GroupsViewModel,
    adminViewModel: AdminViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val group = remember(groupId, groups) { groups.find { it.id == groupId } }
    val messages by viewModel.getMessages(groupId).collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var messageText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<GroupMessage?>(null) }
    var pendingAttachments by remember { mutableStateOf(emptyList<GroupAttachment>()) }

    var selectedMessage by remember { mutableStateOf<GroupMessage?>(null) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    
    var showPlusMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        OutpourBackground {
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                // Chat Header
                ChatHeader(group = group, onNavigateBack = onNavigateBack, onInfoClick = { onNavigateToInfo(groupId) })

                // Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(messages, key = { _, it -> it.id }) { _, message ->
                        MessageBubble(
                            message = message,
                            isOwnMessage = message.senderId == viewModel.currentUserId,
                            adminViewModel = adminViewModel,
                            onLongPress = {
                                selectedMessage = message
                                showOptionsMenu = true
                            },
                            onSwipeToReply = {
                                replyingTo = message
                            },
                            onReplyClick = { targetId ->
                                val index = messages.indexOfFirst { it.id == targetId }
                                if (index != -1) {
                                    scope.launch {
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            }
                        )
                    }
                }

                // Attachment Preview
                if (pendingAttachments.isNotEmpty()) {
                    AttachmentPreview(attachments = pendingAttachments, onRemove = { attachment ->
                        pendingAttachments = pendingAttachments - attachment
                    })
                }

                // Reply Preview
                replyingTo?.let { reply ->
                    ReplyPreview(message = reply, onCancel = { replyingTo = null })
                }

                // Input Area
                ChatInput(
                    value = messageText,
                    onValueChange = { messageText = it },
                    onSend = {
                        if (messageText.isNotBlank() || pendingAttachments.isNotEmpty()) {
                            viewModel.sendMessage(groupId, messageText, replyingTo, pendingAttachments)
                            messageText = ""
                            replyingTo = null
                            pendingAttachments = emptyList()
                        }
                    },
                    onPlusClick = { showPlusMenu = !showPlusMenu }
                )
            }
        }

        // Plus Menu Overlay
        if (showPlusMenu) {
            PlusMenuOverlay(
                onDismiss = { showPlusMenu = false },
                onMedia = { /* TODO */ showPlusMenu = false },
                onFile = { /* TODO */ showPlusMenu = false },
                onPoll = { 
                    showPlusMenu = false
                    onNavigateToCreatePoll(groupId)
                }
            )
        }

        // Animated Overlay for Message Options
        if (showOptionsMenu && selectedMessage != null) {
            MessageOptionsOverlay(
                message = selectedMessage!!,
                visible = showOptionsMenu,
                onDismiss = {
                    showOptionsMenu = false
                    selectedMessage = null
                },
                onReply = {
                    replyingTo = selectedMessage
                    showOptionsMenu = false
                    selectedMessage = null
                },
                onReaction = { emoji ->
                    viewModel.reactToMessage(groupId, selectedMessage!!.id, emoji)
                    showOptionsMenu = false
                    selectedMessage = null
                }
            )
        }
    }
}

@Composable
fun ChatHeader(group: AppGroup?, onNavigateBack: () -> Unit, onInfoClick: () -> Unit) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().height(64.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Back", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(4.dp))

            Row(
                modifier = Modifier.weight(1f).clickable { onInfoClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = group?.name?.take(2)?.uppercase() ?: "??",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = group?.name ?: "Chat",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    Text(
                        text = "${group?.type?.title ?: "General"} • ${group?.memberCount ?: 0} Members",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = RubikFontFamily
                    )
                }
            }

            IconButton(onClick = onInfoClick) {
                Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "Info", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: GroupMessage,
    isOwnMessage: Boolean,
    adminViewModel: AdminViewModel,
    onLongPress: () -> Unit,
    onSwipeToReply: () -> Unit,
    onReplyClick: (String) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "swipe_offset"
    )

    val swipeThreshold = 100f
    val iconScale by animateFloatAsState(
        targetValue = if (animatedOffsetX.absoluteValue > swipeThreshold * 0.5f) 1f else 0f,
        label = "icon_scale"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isOwnMessage) {
            AvatarCircle(url = message.senderPhotoURL, initials = message.senderName.take(2).uppercase())
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX.absoluteValue > swipeThreshold) {
                                onSwipeToReply()
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (isOwnMessage && dragAmount < 0) {
                                offsetX = (offsetX + dragAmount).coerceAtLeast(-200f)
                            } else if (!isOwnMessage && dragAmount > 0) {
                                offsetX = (offsetX + dragAmount).coerceAtMost(200f)
                            }
                        }
                    )
                },
            contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            if (animatedOffsetX.absoluteValue > 0) {
                Icon(
                    imageVector = Icons.Default.Reply,
                    contentDescription = "Reply",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .offset { IntOffset((animatedOffsetX * 0.8f).roundToInt(), 0) }
                        .scale(iconScale)
                        .padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onLongPress
                    ),
                horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
            ) {
                if (!isOwnMessage) {
                    Text(
                        text = message.senderName,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
                        fontFamily = RubikFontFamily
                    )
                }

                if (message.replyToMessageId != null) {
                    ReplyBubbleContent(
                        senderName = message.replyToSenderName ?: "Unknown",
                        text = message.replyToText ?: "",
                        isOwnMessage = isOwnMessage,
                        onClick = { onReplyClick(message.replyToMessageId) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Box {
                    if (message.pollId != null) {
                        PollBubble(
                            pollId = message.pollId,
                            viewModel = adminViewModel,
                            modifier = Modifier.padding(bottom = if (message.reactionCounts.isNotEmpty()) 12.dp else 0.dp)
                        )
                    } else {
                        Surface(
                            color = if (isOwnMessage) Color.White else Color.White.copy(0.08f),
                            shape = RoundedCornerShape(
                                topStart = if (message.replyToMessageId != null && !isOwnMessage) 4.dp else 20.dp,
                                topEnd = if (message.replyToMessageId != null && isOwnMessage) 4.dp else 20.dp,
                                bottomStart = if (isOwnMessage) 20.dp else 4.dp,
                                bottomEnd = if (isOwnMessage) 4.dp else 20.dp
                            ),
                            modifier = Modifier.zIndex(1f)
                        ) {
                            Text(
                                text = message.text,
                                color = if (isOwnMessage) Color.Black else Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                fontSize = 15.sp,
                                fontFamily = RubikFontFamily,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (message.reactionCounts.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .align(if (isOwnMessage) Alignment.BottomEnd else Alignment.BottomStart)
                                .offset(y = 12.dp, x = if (isOwnMessage) (-8).dp else 8.dp)
                                .zIndex(2f),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            message.reactionCounts.forEach { (emoji, count) ->
                                Surface(
                                    color = Color(0xFF222222),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = emoji, fontSize = 12.sp)
                                        if (count > 1) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = count.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val timeStr = message.createdAt?.let {
                    SimpleDateFormat("h:mm a", Locale.US).format(it.toDate())
                } ?: ""

                Text(
                    text = timeStr,
                    color = TextSecondary.copy(0.5f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = if (message.reactionCounts.isNotEmpty()) 16.dp else 4.dp, start = 4.dp, end = 4.dp),
                    fontFamily = RubikFontFamily
                )
            }
        }
    }
}

@Composable
fun AvatarCircle(url: String?, initials: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Color.White.copy(0.1f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(text = initials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun ReplyBubbleContent(senderName: String, text: String, isOwnMessage: Boolean, onClick: () -> Unit) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = if (!isOwnMessage) 2.dp else 12.dp,
            bottomEnd = if (isOwnMessage) 2.dp else 12.dp
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start
        ) {
            if (!isOwnMessage) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(Color.White.copy(0.3f), RoundedCornerShape(1.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start) {
                Text(
                    text = senderName.uppercase(),
                    color = Color.White.copy(0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = text,
                    color = Color.White.copy(0.8f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    fontFamily = RubikFontFamily
                )
            }

            if (isOwnMessage) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(Color.White.copy(0.3f), RoundedCornerShape(1.dp))
                )
            }
        }
    }
}

@Composable
fun ReplyPreview(message: GroupMessage, onCancel: () -> Unit) {
    Surface(
        color = Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(36.dp)
                    .background(Color.White.copy(0.3f), RoundedCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.senderName.uppercase(),
                    color = Color.White.copy(0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = message.text,
                    color = Color.White.copy(0.8f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    fontFamily = RubikFontFamily
                )
            }
            IconButton(onClick = onCancel) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun AttachmentPreview(attachments: List<GroupAttachment>, onRemove: (GroupAttachment) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().background(Color.Black.copy(0.5f)).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(attachments) { attachment ->
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.1f))) {
                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color.White.copy(0.5f), modifier = Modifier.align(Alignment.Center))
                IconButton(
                    onClick = { onRemove(attachment) },
                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.Black.copy(0.5f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onPlusClick: () -> Unit
) {
    Surface(
        color = Color.Black.copy(0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onPlusClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(0.1f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Options", tint = Color.White)
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Message...", color = TextSecondary) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(0.08f),
                    unfocusedContainerColor = Color.White.copy(0.08f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 4
            )

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(48.dp)
                    .background(if (value.isNotBlank()) Color.White else Color.White.copy(0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (value.isNotBlank()) Color.Black else Color.White.copy(0.5f)
                )
            }
        }
    }
}

@Composable
fun PlusMenuOverlay(
    onDismiss: () -> Unit,
    onMedia: () -> Unit,
    onFile: () -> Unit,
    onPoll: () -> Unit
) {
    Popup(
        alignment = Alignment.BottomStart,
        properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomStart
        ) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 80.dp)
                    .width(200.dp),
                color = Color(0xFF111111),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    PlusMenuItem(icon = Icons.Default.Image, label = "Media", onClick = onMedia)
                    PlusMenuItem(icon = Icons.Default.Description, label = "File", onClick = onFile)
                    PlusMenuItem(icon = Icons.Default.Poll, label = "Poll", onClick = onPoll)
                }
            }
        }
    }
}

@Composable
fun PlusMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
    }
}

@Composable
fun MessageOptionsOverlay(
    message: GroupMessage,
    visible: Boolean,
    onDismiss: () -> Unit,
    onReply: () -> Unit,
    onReaction: (String) -> Unit
) {
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(initialScale = 0.8f) + fadeIn(),
                exit = scaleOut(targetScale = 0.8f) + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Reaction Bar
                    Surface(
                        color = Color(0xFF1A1A1A),
                        shape = RoundedCornerShape(32.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            listOf("❤️", "👍", "🔥", "🙏", "😂").forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier.clickable { onReaction(emoji) }
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "More",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White.copy(0.1f), CircleShape)
                                    .padding(4.dp)
                                    .clickable { /* TODO: Emoji Picker */ }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Context Menu
                    Surface(
                        color = Color(0xFF1A1A1A),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            OptionMenuItem(icon = Icons.Default.Reply, label = "Reply") { onReply() }
                            OptionMenuItem(icon = Icons.Default.ContentCopy, label = "Copy") { onDismiss() }
                            OptionMenuItem(icon = Icons.Default.Delete, label = "Delete", color = Color(0xFFEF4444)) { onDismiss() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionMenuItem(icon: ImageVector, label: String, color: Color = Color.White, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
    }
}
