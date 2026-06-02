package dev.bti.kdym.ui.screens.groups

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.core.content.ContextCompat
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.data.models.GroupAttachment
import dev.bti.kdym.data.models.GroupAttachmentType
import dev.bti.kdym.data.models.GroupMessage
import dev.bti.kdym.data.models.MessageReaction
import dev.bti.kdym.ui.components.MappedIcon
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.PollBubble
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.components.ReportMessageDialog
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.ui.theme.toColor
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.graphics.graphicsLayer
import androidx.core.net.toUri

/**
 * Main Chat Screen for group messaging.
 */
val OtherMessageColor = Color(0xFF1E1E1E)

@Composable
fun EmptyChatState(group: AppGroup?, themeColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(themeColor.copy(0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (group?.iconName != null) {
                MappedIcon(
                    iosName = group.iconName,
                    tint = themeColor,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "NO MESSAGES YET",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily
        )
        Text(
            text = "Be the first to start the conversation in ${group?.name ?: "this chat"}.",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontFamily = QuickSandFontFamily,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onNavigateToInfo: (String) -> Unit,
    onNavigateToCreatePoll: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToPdf: (String) -> Unit,
    viewModel: GroupsViewModel,
    adminViewModel: AdminViewModel,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val groups by viewModel.groups.collectAsState()
    val tribes by adminViewModel.tribes.collectAsState()
    val group = remember(groupId, groups, tribes) {
        val baseGroup = groups.find { it.id == groupId }
        if (baseGroup?.type == AppGroupType.tribe) {
            val matchingTribe = tribes.find { it.id == baseGroup.tribeId }
            if (matchingTribe != null) {
                baseGroup.copy(
                    iconName = matchingTribe.iconName,
                    colorHex = matchingTribe.colorHex
                )
            } else baseGroup
        } else baseGroup
    }
    val themeColor = remember(group?.colorHex) { group?.colorHex?.toColor() ?: Color(0xFFEF4444) }
    val messages by viewModel.getMessages(groupId).collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var messageText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<GroupMessage?>(null) }
    var pendingAttachments by remember { mutableStateOf(emptyList<Uri>()) }

    // Unified Optimistic Reactions from ViewModel
    val optimisticReactions by viewModel.optimisticReactions.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                pendingAttachments = pendingAttachments + uri
            }
        }
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                pendingAttachments = pendingAttachments + uri
            }
        }
    )
    
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    fun checkAndRequestPermissions(onGranted: () -> Unit) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            onGranted()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    var selectedMessage by remember { mutableStateOf<GroupMessage?>(null) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showPlusMenu by remember { mutableStateOf(false) }
    var showMediaGallery by remember { mutableStateOf(false) }
    var initialMediaIndex by remember { mutableIntStateOf(0) }

    // Scroll to Bottom State
    val isAtBottom by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex <= 1
        }
    }

    val isLeader = remember(group, viewModel.currentUserId) {
        group?.leaderIds?.contains(viewModel.currentUserId) == true || adminViewModel.allUsers.value.find { it.uid == viewModel.currentUserId }?.roleEnum?.canAccessCommand == true
    }

    val canPost = remember(group, isLeader) {
        group?.chatEnabled == true && (!group.postingRestrictedToLeaders || isLeader)
    }

    val canAttach = remember(group, isLeader) {
        group?.chatEnabled == true && (!group.attachmentsRestrictedToLeaders || isLeader)
    }

    val canPoll = remember(group, isLeader) {
        group?.chatEnabled == true && (!group.pollsRestrictedToLeaders || isLeader)
    }

    // Typing Status Logic
    val typingNames = remember(group, viewModel.currentUserId) {
        group?.typingUserNames?.filterKeys { it != viewModel.currentUserId }?.values?.toList() ?: emptyList()
    }

    val latestMessageId = messages.firstOrNull()?.id
    LaunchedEffect(latestMessageId) {
        if (isAtBottom && latestMessageId != null) {
            listState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= messages.size - 5 && messages.size >= 50) {
                    viewModel.loadMoreMessages(groupId)
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OutpourBackground {
            Column(modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()) {
                ChatHeader(
                    group = group,
                    typingNames = typingNames,
                    onNavigateBack = onNavigateBack,
                    onInfoClick = { onNavigateToInfo(groupId) })

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    if (messages.isEmpty()) {
                        EmptyChatState(group = group, themeColor = themeColor)
                    }
                    
                    MessageList(
                        messages = messages,
                        optimisticReactions = optimisticReactions,
                        listState = listState,
                        themeColor = themeColor,
                        group = group,
                        currentUserId = viewModel.currentUserId,
                        adminViewModel = adminViewModel,
                        typingNames = typingNames,
                        onMessageOptions = { message ->
                            selectedMessage = message
                            showOptionsMenu = true
                        },
                        onSwipeToReply = { message -> replyingTo = message },
                        onReplyClick = { targetId ->
                            val index = messages.indexOfFirst { it.id == targetId }
                            if (index != -1) {
                                scope.launch { listState.animateScrollToItem(index) }
                            }
                        },
                        onMediaClick = { msg, index ->
                            selectedMessage = msg
                            initialMediaIndex = index
                            showMediaGallery = true
                        },
                        onNavigateToProfile = onNavigateToProfile,
                        onNavigateToPdf = onNavigateToPdf,
                        onReactionClick = { },
                        modifier = Modifier.fillMaxSize()
                    )

                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isAtBottom,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        ScrollToBottomButton(
                            themeColor = themeColor,
                            onClick = {
                                scope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        )
                    }
                }

                if (pendingAttachments.isNotEmpty()) {
                    AttachmentPreview(
                        attachments = pendingAttachments,
                        onRemove = { uri -> pendingAttachments = pendingAttachments - uri })
                }

                replyingTo?.let { reply ->
                    ReplyPreview(message = reply, onCancel = { replyingTo = null })
                }

                ChatInput(
                    value = messageText,
                    themeColor = themeColor,
                    onValueChange = { messageText = it },
                    onSend = {
                        if (messageText.isNotBlank() || pendingAttachments.isNotEmpty()) {
                            viewModel.sendMessage(
                                groupId = groupId,
                                text = messageText.trim(),
                                replyTo = replyingTo,
                                attachments = pendingAttachments
                            )
                            messageText = ""
                            replyingTo = null
                            pendingAttachments = emptyList()
                        }
                    },
                    onPlusClick = { if (canAttach || canPoll) showPlusMenu = !showPlusMenu },
                    enabled = canPost
                )
            }
        }

        if (showPlusMenu) {
            PlusMenuOverlay(
                onDismiss = { showPlusMenu = false },
                onMedia = {
                    if (canAttach) {
                        checkAndRequestPermissions {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        }
                    }
                    showPlusMenu = false
                },
                onFile = {
                    if (canAttach) {
                        checkAndRequestPermissions {
                            filePickerLauncher.launch("*/*")
                        }
                    }
                    showPlusMenu = false
                },
                onPoll = {
                    if (canPoll) {
                        onNavigateToCreatePoll(groupId)
                    }
                    showPlusMenu = false
                }
            )
        }

        if (showMediaGallery && selectedMessage != null) {
            MediaGalleryOverlay(
                attachments = selectedMessage!!.attachments,
                initialIndex = initialMediaIndex,
                onDismiss = { showMediaGallery = false })
        }

        if (showOptionsMenu && selectedMessage != null) {
            MessageOptionsOverlay(
                message = selectedMessage!!,
                themeColor = themeColor,
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
                onReport = {
                    showReportDialog = true
                    showOptionsMenu = false
                },
                onReaction = { emoji ->
                    selectedMessage?.let { msg ->
                        scope.launch {
                            viewModel.reactToMessage(groupId, msg.id, emoji)
                        }
                    }
                    showOptionsMenu = false
                    selectedMessage = null
                },
                onNavigateToPdf = onNavigateToPdf,
                adminViewModel = adminViewModel
            )
        }

        if (showReportDialog && selectedMessage != null && group != null) {
            val currentGroup = group
            val currentUser by mainViewModel.user.collectAsState()
            currentUser?.let { user ->
                ReportMessageDialog(
                    message = selectedMessage!!,
                    group = currentGroup,
                    currentUser = user,
                    onDismiss = { showReportDialog = false },
                    onSubmit = { reason, details ->
                        mainViewModel.submitReport(
                            groupId = currentGroup.id,
                            groupName = currentGroup.name,
                            message = selectedMessage!!,
                            reporter = user,
                            reason = reason,
                            details = details,
                            onSuccess = {
                                showReportDialog = false
                                selectedMessage = null
                                mainViewModel.showFeedback("Report submitted. Thank you for keeping KDYM safe.")
                            }
                        )
                    }
                )
            }
        }

/*
        if (showReactionDetails && selectedMessage != null) {
            val reactions = reactionsState[selectedMessage!!.id] ?: emptyList()
            ReactionDetailsSheet(reactions = reactions, onDismiss = { showReactionDetails = false })
        }
*/
    }
}

@Composable
fun MessageList(
    messages: List<GroupMessage>,
    optimisticReactions: Map<String, String?>,
    listState: LazyListState,
    themeColor: Color,
    group: AppGroup?,
    currentUserId: String?,
    adminViewModel: AdminViewModel,
    typingNames: List<String>,
    onMessageOptions: (GroupMessage) -> Unit,
    onSwipeToReply: (GroupMessage) -> Unit,
    onReplyClick: (String) -> Unit,
    onMediaClick: (GroupMessage, Int) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToPdf: (String) -> Unit,
    onReactionClick: (GroupMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val processedItems = remember(messages) { processMessages(messages) }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        reverseLayout = true,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            TypingIndicator(typingNames = typingNames, themeColor = themeColor)
        }

        items(processedItems, key = { it.key }) { item ->
            when (item) {
                is ChatListItem.MessageItem -> {
                    val message = item.message
                    val index = messages.indexOf(message)
                    val prevMessage = messages.getOrNull(index + 1)
                    val nextMessage = messages.getOrNull(index - 1)

                    val isFirstInGroup = prevMessage == null || prevMessage.senderId != message.senderId ||
                            prevMessage.isSystemMessage ||
                            (message.createdAt?.seconds ?: 0) - (prevMessage.createdAt?.seconds ?: 0) > 300
                    val isLastInGroup = nextMessage == null || nextMessage.senderId != message.senderId ||
                            nextMessage.isSystemMessage ||
                            (nextMessage.createdAt?.seconds ?: 0) - (message.createdAt?.seconds ?: 0) > 300

                    val userEmoji = optimisticReactions[message.id]
                    val finalReactions = if (userEmoji != null) {
                        val updated = message.reactionCounts.toMutableMap()
                        updated[userEmoji] = (updated[userEmoji] ?: 0) + 1
                        updated
                    } else {
                        message.reactionCounts
                    }

                    MessageBubble(
                        message = message.copy(reactionCounts = finalReactions),
                        isOwnMessage = message.senderId == currentUserId,
                        themeColor = themeColor,
                        isFirstInGroup = isFirstInGroup,
                        isLastInGroup = isLastInGroup,
                        adminViewModel = adminViewModel,
                        onLongPress = { onMessageOptions(message) },
                        onSwipeToReply = { onSwipeToReply(message) },
                        onReplyClick = onReplyClick,
                        onMediaClick = { mediaIndex -> onMediaClick(message, mediaIndex) },
                        onNavigateToProfile = onNavigateToProfile,
                        onNavigateToPdf = onNavigateToPdf,
                        onReactionClick = { onReactionClick(message) }
                    )
                }
                is ChatListItem.SystemGroupItem -> {
                    SystemMessageBubble(text = item.text)
                }
                is ChatListItem.DateHeaderItem -> {
                    DateHeader(timestamp = item.timestamp)
                }
            }
        }
    }
}

@Composable
fun DateHeader(timestamp: com.google.firebase.Timestamp?) {
    if (timestamp == null) return

    val dateStr = remember(timestamp) {
        when {
            isSameDay(timestamp, com.google.firebase.Timestamp.now()) -> "TODAY"
            isYesterday(timestamp) -> "YESTERDAY"
            else -> SimpleDateFormat("EEEE, d MMMM", Locale.US).format(timestamp.toDate()).uppercase()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color.White.copy(0.05f),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White.copy(0.05f))
        ) {
            Text(
                text = dateStr,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                fontFamily = QuickSandFontFamily
            )
        }
    }
}

@Composable
fun ChatHeader(group: AppGroup?, typingNames: List<String>, onNavigateBack: () -> Unit, onInfoClick: () -> Unit) {
    val typingText = remember(typingNames) {
        when {
            typingNames.isEmpty() -> null
            typingNames.size == 1 -> "${typingNames[0]} is typing..."
            typingNames.size == 2 -> "${typingNames[0]} and ${typingNames[1]} are typing..."
            else -> "${typingNames[0]} and ${typingNames.size - 1} others typing..."
        }
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onNavigateBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(0.1f),
                    contentColor = Color.White
                ),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            // iOS Centered Pill
            Surface(
                color = Color.White.copy(0.08f),
                shape = CircleShape,
                modifier = Modifier.clickable { onInfoClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                group?.colorHex?.toColor() ?: Color(0xFFEF4444),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        MappedIcon(
                            iosName = group?.iconName ?: "",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )

                        // Cool Active Indicator
                        if (typingNames.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 2.dp, y = 2.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                                    .border(2.dp, Color(0xFF111111), CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = group?.name ?: "Chat",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        
                        typingText?.let {
                            Text(
                                text = it.uppercase(),
                                color = Color(0xFF22C55E),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily,
                                letterSpacing = 1.sp
                            )
                        } ?: Text(
                            text = "${group?.type?.title?.uppercase() ?: "TRIBE"} • ${group?.memberCount ?: 0} MEMBERS",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            IconButton(
                onClick = onInfoClick,
                modifier = Modifier
                    .background(Color.White.copy(0.1f), CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Info",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: GroupMessage,
    isOwnMessage: Boolean,
    themeColor: Color,
    isFirstInGroup: Boolean,
    isLastInGroup: Boolean,
    adminViewModel: AdminViewModel,
    onLongPress: () -> Unit,
    onSwipeToReply: () -> Unit,
    onReplyClick: (String) -> Unit,
    onMediaClick: (Int) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToPdf: (String) -> Unit,
    onReactionClick: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isFirstInGroup) 4.dp else 0.dp),
        horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isOwnMessage) {
            if (isLastInGroup) {
                AvatarCircle(
                    url = message.senderPhotoURL,
                    initials = message.senderName.take(2).uppercase(),
                    onClick = { onNavigateToProfile(message.senderId) }
                )
            } else {
                Spacer(modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX.absoluteValue > swipeThreshold) {
                                onSwipeToReply()
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            if (isOwnMessage && dragAmount < 0) offsetX =
                                (offsetX + dragAmount).coerceAtLeast(-200f)
                            else if (!isOwnMessage && dragAmount > 0) offsetX =
                                (offsetX + dragAmount).coerceAtMost(200f)
                        }
                    )
                },
            contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            if (animatedOffsetX.absoluteValue > 0) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Reply",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (animatedOffsetX * 0.8f).roundToInt(),
                                0
                            )
                        }
                        .scale(iconScale)
                        .padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                    .combinedClickable(
                        onClick = { /* Handle click if needed */ },
                        onLongClick = { onLongPress() }),
                horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
            ) {
                if (!isOwnMessage && isFirstInGroup) {
                    Text(
                        text = message.senderName,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .padding(start = 12.dp, bottom = 4.dp)
                            .clickable { onNavigateToProfile(message.senderId) },
                        fontFamily = QuickSandFontFamily
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

                Box(contentAlignment = if (isOwnMessage) Alignment.BottomEnd else Alignment.BottomStart) {
                    Column(
                        horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start,
                        modifier = Modifier.padding(bottom = if (message.reactionCounts.isNotEmpty()) 6.dp else 0.dp)
                    ) {
                        if (message.attachments.isNotEmpty()) {
                            AttachmentGrid(
                                attachments = message.attachments,
                                isOwnMessage = isOwnMessage,
                                themeColor = themeColor,
                                onMediaClick = onMediaClick,
                                onNavigateToPdf = onNavigateToPdf
                            )
                        }

                        if (message.pollId != null) {
                            PollBubble(
                                groupId = message.groupId,
                                pollId = message.pollId,
                                viewModel = adminViewModel
                            )
                        } else if (message.text.isNotBlank()) {
                            MessageTextSurface(
                                text = message.text,
                                isOwnMessage = isOwnMessage,
                                themeColor = themeColor,
                                hasReply = message.replyToMessageId != null,
                                isFirstInGroup = isFirstInGroup,
                                isLastInGroup = isLastInGroup
                            )
                        }
                    }

//                    if (message.reactionCounts.isNotEmpty()) {
//                        ReactionChipRow(
//                            reactions = message.reactionCounts,
//                            isOwnMessage = isOwnMessage,
//                            onReactionClick = onReactionClick,
//                            modifier = Modifier.offset(y = 12.dp, x = if (isOwnMessage) (-12).dp else 12.dp)
//                        )
//                    }
                }

                if (isLastInGroup) {
                    val timeStr = message.createdAt?.let {
                        SimpleDateFormat(
                            "h:mm a",
                            Locale.US
                        ).format(it.toDate())
                    } ?: ""
                    Text(
                        text = timeStr,
                        color = TextSecondary.copy(0.5f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(
                            top = if (message.reactionCounts.isNotEmpty()) 16.dp else 4.dp,
                            start = 4.dp,
                            end = 4.dp
                        ),
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
        }
    }
}

@SuppressLint("UseKtx")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AttachmentGrid(
    attachments: List<GroupAttachment>,
    isOwnMessage: Boolean,
    themeColor: Color,
    onMediaClick: (Int) -> Unit,
    onNavigateToPdf: (String) -> Unit
) {
    val imageAttachments =
        attachments.filter { it.type == GroupAttachmentType.image || it.type == GroupAttachmentType.video }
    val fileAttachments = attachments.filter { it.type == GroupAttachmentType.file }
    val bgColor = if (isOwnMessage) themeColor else OtherMessageColor

    Column(
        modifier = Modifier.padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (imageAttachments.isNotEmpty()) {
            val displayCount = if (imageAttachments.size > 4) 4 else imageAttachments.size
            val rows = (displayCount + 1) / 2

            // Media background wrapper (WhatsApp style)
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(3.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    repeat(rows) { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            repeat(2) { colIndex ->
                                val index = rowIndex * 2 + colIndex
                                if (index < displayCount) {
                                    val attachment = imageAttachments[index]
                                    val isLast = index == 3 && imageAttachments.size > 4

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(if (imageAttachments.size == 1) 16 / 9f else 1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(0.05f))
                                            .clickable { 
                                                val globalIndex = attachments.indexOf(attachment)
                                                if (globalIndex != -1) onMediaClick(globalIndex)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = attachment.url,
                                            contentDescription = "Attachment",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        if (attachment.type == GroupAttachmentType.video) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(
                                                        Color.Black.copy(0.5f),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Play Video",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }

                                        if (isLast) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(0.6f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "+${imageAttachments.size - 3}\nView All",
                                                    color = Color.White,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black,
                                                    fontFamily = QuickSandFontFamily,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                } else if (displayCount > 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        fileAttachments.forEach { attachment ->
            val context = LocalContext.current
            val extension = attachment.fileName?.substringAfterLast('.', "")?.lowercase()

            val isImageFile = extension in listOf("jpg", "jpeg", "png", "gif", "webp", "heic")
            val isPdf = extension == "pdf"

            Surface(
                color = bgColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when {
                            isImageFile -> {
                                val index = attachments.indexOf(attachment)
                                if (index != -1) onMediaClick(index)
                            }
                            isPdf -> {
                                onNavigateToPdf(attachment.url)
                            }
                            else -> {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, attachment.url.toUri())
                                    context.startActivity(intent)
                                } catch (e: Exception) {

                                }
                            }
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White.copy(0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = attachment.fileName ?: "File",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = QuickSandFontFamily
                        )
                        attachment.sizeBytes?.let {
                            Text(
                                text = "${it / 1024} KB",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageTextSurface(
    text: String,
    isOwnMessage: Boolean,
    themeColor: Color,
    hasReply: Boolean,
    isFirstInGroup: Boolean,
    isLastInGroup: Boolean
) {
    val cornerRadius = 24.dp
    val uriHandler = LocalUriHandler.current

    val brush = if (isOwnMessage) {
        Brush.linearGradient(
            colors = listOf(themeColor.copy(alpha = 0.8f), themeColor),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    } else null

    // 1. Update the AnnotatedString builder to use addLink()
    val annotatedString = buildAnnotatedString {
        append(text)
        val matcher = Patterns.WEB_URL.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            val rawUrl = text.substring(start, end)
            val url = if (!rawUrl.startsWith("http")) "https://$rawUrl" else rawUrl

            addStyle(
                style = SpanStyle(
                    color = if (isOwnMessage) Color.White else Color(0xFF22D3EE),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                ),
                start = start,
                end = end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = text.substring(start, end),
                start = start,
                end = end
            )
        }
    }

    val firstLink = remember(text) {
        val matcher = Patterns.WEB_URL.matcher(text)
        if (matcher.find()) text.substring(matcher.start(), matcher.end()) else null
    }

    Column(
        horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isOwnMessage) Color.Transparent else OtherMessageColor,
            shape = RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius,
                bottomStart = if (isOwnMessage) cornerRadius else 4.dp,
                bottomEnd = if (isOwnMessage) 4.dp else cornerRadius
            ),
            border = if (isOwnMessage) null else BorderStroke(1.dp, Color.White.copy(0.05f)),
            modifier = Modifier
                .then(
                    if (isOwnMessage && brush != null) Modifier.background(brush = brush, shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius, bottomStart = cornerRadius, bottomEnd = 4.dp))
                    else Modifier
                )
                .zIndex(1f)
        ) {
            // 2. Replace ClickableText with the standard Text composable
            Text(
                text = annotatedString,
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontFamily = QuickSandFontFamily,
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        if (firstLink != null) {
            LinkPreviewCard(url = firstLink, isOwnMessage = isOwnMessage)
        }
    }
}

@Composable
fun LinkPreviewCard(url: String, isOwnMessage: Boolean) {
    val domain = remember(url) {
        try {
            val uri = Uri.parse(if (!url.startsWith("http")) "https://$url" else url)
            uri.host?.removePrefix("www.") ?: url
        } catch (e: Exception) {
            url
        }
    }

    Surface(
        color = Color.White.copy(0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.1f)),
        modifier = Modifier
            .padding(top = 4.dp)
            .fillMaxWidth(0.8f)
            .clickable { /* Could open link too */ }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = Color.White.copy(0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = domain,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 1
                )
                Text(
                    text = "Tap to visit site",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaGalleryOverlay(
    attachments: List<GroupAttachment>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState =
        rememberPagerState(initialPage = initialIndex, pageCount = { attachments.size })

    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 16.dp
            ) { page ->
                val attachment = attachments[page]
                AsyncImage(
                    model = attachment.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Header
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                Text(
                    text = "${pagerState.currentPage + 1} / ${attachments.size}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )

                Spacer(modifier = Modifier.size(48.dp)) // For symmetry
            }
        }
    }
}

@Composable
fun ReactionChipRow(
    reactions: Map<String, Int>,
    isOwnMessage: Boolean,
    onReactionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (reactions.isEmpty()) return
    val totalCount = reactions.values.sum()
    val topEmojis = reactions.keys.take(3)

    Row(
        modifier = modifier
            .zIndex(2f),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            color = Color(0xFF222222),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(0.15f)),
            modifier = Modifier.clickable { onReactionClick() },
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                topEmojis.forEachIndexed { index, emoji ->
                    Text(
                        text = emoji,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Default,
                        color = Color.Unspecified,
                        modifier = Modifier.offset(x = if (index > 0) (-4 * index).dp else 0.dp),
                        style = LocalTextStyle.current.copy(
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp
                        )
                    )
                }
                if (totalCount > 1) {
                    Spacer(modifier = Modifier.width(if (topEmojis.size > 1) 0.dp else 4.dp))
                    Text(
                        text = totalCount.toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarCircle(url: String?, initials: String, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Color.White.copy(0.1f), CircleShape)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
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
            Text(
                text = initials,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}

@Composable
fun ReplyBubbleContent(
    senderName: String,
    text: String,
    isOwnMessage: Boolean,
    onClick: () -> Unit
) {
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
            verticalAlignment = Alignment.CenterVertically
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

            Column(
                modifier = Modifier.weight(1f, fill = false),
                horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
            ) {
                Text(
                    text = senderName.uppercase(),
                    color = Color.White.copy(0.4f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = text,
                    color = Color.White.copy(0.8f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    fontFamily = QuickSandFontFamily
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
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = message.text,
                    color = Color.White.copy(0.8f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    fontFamily = QuickSandFontFamily
                )
            }
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AttachmentPreview(attachments: List<Uri>, onRemove: (Uri) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.5f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(attachments) { uri ->
            val isVideo = uri.toString().contains("video", ignoreCase = true)
            val isPdf = uri.toString().contains("pdf", ignoreCase = true) || uri.path?.contains(
                ".pdf",
                ignoreCase = true
            ) == true

            Box(modifier = Modifier.size(80.dp)) {
                if (isPdf) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                if (isVideo) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Video",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { onRemove(uri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(20.dp)
                        .background(Color.Black.copy(0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatInput(
    value: String,
    themeColor: Color,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onPlusClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        color = Color.Black.copy(0.7f), // Darker for better glass look
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.White.copy(0.05f))
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
                enabled = enabled,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(if (enabled) 0.1f else 0.03f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Options",
                    tint = if (enabled) Color.White else Color.White.copy(0.3f)
                )
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                placeholder = { Text(if (enabled) "Message..." else "Messaging disabled", color = TextSecondary) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(0.08f),
                    unfocusedContainerColor = Color.White.copy(0.08f),
                    disabledContainerColor = Color.White.copy(0.03f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.White.copy(0.3f)
                ),
                maxLines = 4
            )

            IconButton(
                onClick = onSend,
                enabled = enabled && (value.isNotBlank()),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (enabled && value.isNotBlank()) themeColor else Color.White.copy(0.05f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (enabled && value.isNotBlank()) Color.Black else Color.White.copy(0.3f)
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
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
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
                border = BorderStroke(1.dp, Color.White.copy(0.1f))
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
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = QuickSandFontFamily
        )
    }
}

@Composable
fun MessageOptionsOverlay(
    message: GroupMessage,
    themeColor: Color,
    visible: Boolean,
    onDismiss: () -> Unit,
    onReply: () -> Unit,
    onReport: () -> Unit,
    onReaction: (String) -> Unit,
    onNavigateToPdf: (String) -> Unit,
    adminViewModel: AdminViewModel
) {
    // Use themeColor for some elements if desired, or just ignore if not needed
    // For now we keep it to satisfy the parameter requirement
    Popup(
        alignment = Alignment.Center,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(initialScale = 0.9f) + fadeIn(),
                exit = scaleOut(targetScale = 0.9f) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
/*
                    ReactionSelectionBar(onReaction = { emoji ->
                        onReaction(emoji)
                        onDismiss()
                    })

                    Spacer(modifier = Modifier.height(24.dp))
*/

                    // Focused Message Bubble (Simplified/Non-interactive)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(16.dp)
                    ) {
                        Column {
                            if (message.replyToMessageId != null) {
                                ReplyBubbleContent(
                                    senderName = message.replyToSenderName ?: "Unknown",
                                    text = message.replyToText ?: "",
                                    isOwnMessage = false, // Static look
                                    onClick = {}
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (message.attachments.isNotEmpty()) {
                                AttachmentGrid(
                                    attachments = message.attachments,
                                    isOwnMessage = false, // Simplified look
                                    themeColor = themeColor,
                                    onMediaClick = {},
                                    onNavigateToPdf = onNavigateToPdf
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (message.text.isNotBlank()) {
                                Text(
                                    text = message.text,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = QuickSandFontFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    MessageContextMenu(
                        themeColor = themeColor,
                        onReply = onReply,
                        onReport = onReport,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReactionDetailsSheet(
    reactions: List<MessageReaction>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121212),
        contentColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(0.2f)) },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Reactions",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                modifier = Modifier.padding(16.dp)
            )

            if (reactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reactions yet",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            } else {
                LazyColumn {
                    items(reactions) { reaction ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarCircle(
                                url = reaction.userPhotoURL,
                                initials = reaction.userName.take(2).uppercase()
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = reaction.userName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = QuickSandFontFamily,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = reaction.emoji,
                                fontSize = 24.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Default
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReactionSelectionBar(onReaction: (String) -> Unit) {
    Surface(
        color = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val emojis = listOf("👍", "🙏", "🔥", "✅", "❤️")
            emojis.forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Default, // CRITICAL FIX for blank emojis
                    color = Color.Unspecified,
                    modifier = Modifier
                        .clickable { onReaction(emoji) }
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun MessageContextMenu(
    themeColor: Color,
    onReply: () -> Unit,
    onReport: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(0.8f),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            OptionMenuItem(icon = Icons.AutoMirrored.Filled.Reply, label = "Reply", color = themeColor) { onReply() }
            OptionMenuItem(icon = Icons.Default.ContentCopy, label = "Copy") { onDismiss() }
            OptionMenuItem(
                icon = Icons.Default.Report,
                label = "Report",
                color = Color(0xFFEF4444)
            ) { onReport() }
            OptionMenuItem(
                icon = Icons.Default.Delete,
                label = "Delete",
                color = Color(0xFFEF4444)
            ) { onDismiss() }
        }
    }
}

@Composable
fun ScrollToBottomButton(
    themeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() },
        color = themeColor,
        shape = CircleShape,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun OptionMenuItem(
    icon: ImageVector,
    label: String,
    color: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = QuickSandFontFamily
        )
    }
}

private fun isSameDay(t1: com.google.firebase.Timestamp?, t2: com.google.firebase.Timestamp?): Boolean {
    if (t1 == null || t2 == null) return false
    val cal1 = java.util.Calendar.getInstance().apply { time = t1.toDate() }
    val cal2 = java.util.Calendar.getInstance().apply { time = t2.toDate() }
    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
}

private fun isYesterday(t: com.google.firebase.Timestamp): Boolean {
    val cal1 = java.util.Calendar.getInstance().apply { time = t.toDate() }
    val cal2 = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }
    return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
            cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
}

sealed class ChatListItem(val key: String) {
    data class DateHeaderItem(val id: String, val timestamp: com.google.firebase.Timestamp) : ChatListItem(id)
    data class MessageItem(val message: GroupMessage) : ChatListItem(message.id)
    data class SystemGroupItem(val id: String, val text: String) : ChatListItem(id)
}

fun processMessages(messages: List<GroupMessage>): List<ChatListItem> {
    if (messages.isEmpty()) return emptyList()

    val items = mutableListOf<ChatListItem>()
    var i = 0
    while (i < messages.size) {
        val current = messages[i]

        if (current.isSystemMessage) {
            val systemGroup = mutableListOf<GroupMessage>()
            systemGroup.add(current)
            var j = i + 1
            // Group consecutive system messages from the same day
            while (j < messages.size && messages[j].isSystemMessage && isSameDay(messages[j].createdAt, current.createdAt)) {
                systemGroup.add(messages[j])
                j++
            }

            val combinedText = combineSystemMessages(systemGroup)
            items.add(ChatListItem.SystemGroupItem("system_${current.id}", combinedText))
            i = j
        } else {
            items.add(ChatListItem.MessageItem(current))
            i++
        }

        // Add Date Header if it's the start of a day (since it's reverseLayout, "next" is actually older)
        val next = messages.getOrNull(i)
        if (next == null || !isSameDay(current.createdAt, next.createdAt)) {
            current.createdAt?.let {
                items.add(ChatListItem.DateHeaderItem("date_${it.seconds}", it))
            }
        }
    }
    return items
}

fun combineSystemMessages(messages: List<GroupMessage>): String {
    val addedRegex = "(.+) was added to the group\\.".toRegex()
    val leftRegex = "(.+) left the group\\.".toRegex()

    val addedNames = mutableListOf<String>()
    val leftNames = mutableListOf<String>()
    val others = mutableListOf<String>()

    // Reverse because input messages are [Latest ... Oldest]
    messages.reversed().forEach { msg ->
        val addedMatch = addedRegex.matchEntire(msg.text)
        val leftMatch = leftRegex.matchEntire(msg.text)

        when {
            addedMatch != null -> addedNames.add(addedMatch.groupValues[1])
            leftMatch != null -> leftNames.add(leftMatch.groupValues[1])
            else -> others.add(msg.text)
        }
    }

    val results = mutableListOf<String>()

    if (addedNames.isNotEmpty()) {
        val namesText = formatNamesList(addedNames)
        results.add("$namesText ${if (addedNames.size > 1) "were added to the group" else "was added to the group"}")
    }
    if (leftNames.isNotEmpty()) {
        val namesText = formatNamesList(leftNames)
        results.add("$namesText left the group")
    }
    results.addAll(others)

    return results.joinToString(" • ")
}

fun formatNamesList(names: List<String>): String {
    return when {
        names.size == 1 -> names[0]
        names.size == 2 -> "${names[0]} and ${names[1]}"
        else -> "${names.dropLast(1).joinToString(", ")} and ${names.last()}"
    }
}

@Composable
fun SystemMessageBubble(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = TextSecondary.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            fontFamily = QuickSandFontFamily,
            letterSpacing = 1.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun TypingIndicator(
    typingNames: List<String>,
    themeColor: Color
) {
    if (typingNames.isEmpty()) return

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = OtherMessageColor,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(0.05f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThreeDotsAnimation(themeColor)
                Text(
                    text = "TYPING",
                    color = themeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun ThreeDotsAnimation(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    @Composable
    fun Dot(delay: Int) {
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(delay)
            ),
            label = "dot_scale"
        )
        Box(
            modifier = Modifier
                .size(4.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    alpha = scale
                }
                .background(color, CircleShape)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Dot(0)
        Dot(200)
        Dot(400)
    }
}
