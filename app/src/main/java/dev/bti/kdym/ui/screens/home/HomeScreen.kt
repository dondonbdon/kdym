package dev.bti.kdym.ui.screens.home

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.bti.kdym.data.models.Camp
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.components.ShimmerItem
import dev.bti.kdym.ui.components.FeedPostShimmer
import dev.bti.kdym.ui.components.home.FeedPostCard
import dev.bti.kdym.ui.components.home.HeroCard
import dev.bti.kdym.ui.components.home.HomeBackground
import dev.bti.kdym.ui.components.home.MemoriesCard
import dev.bti.kdym.ui.components.home.RegistrationButton
import dev.bti.kdym.ui.components.home.VerseCard
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import androidx.core.graphics.toColorInt

@Composable
fun HomeScreen(
    onNavigateToComments: (String) -> Unit,
    onNavigateToRequestAccess: () -> Unit,
    onCreatePostClick: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val camps = dev.bti.kdym.data.models.HARDCODED_CAMPS
    val user by viewModel.user.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled == true
    val posts by viewModel.liveUpdates.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    if (isCampMode) {
        if (posts.isEmpty() && uiState.isLoading) {
            LiveUpdatesShimmer(user)
        } else {
            LiveUpdatesView(
                posts = posts,
                user = user,
                onPostClick = onNavigateToComments,
                onReactionClick = { postId, reaction -> viewModel.toggleReaction(postId, reaction) },
                onChangePhoto = {},
                onCreatePostClick = onCreatePostClick,
                getUserReaction = { viewModel.getUserReaction(it) },
                viewModel = viewModel
            )
        }
    } else {
        StandardHomeView(
            camps = camps,
            user = user,
            onNavigateToRequestAccess = onNavigateToRequestAccess,
            viewModel = viewModel
        )
    }
}

@Composable
fun LiveUpdatesShimmer(user: dev.bti.kdym.data.models.AppUser?) {
    OutpourBackground {
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)) {
                ScreenHeader(
                    isPrimary = true,
                    isCampMode = true,
                    userPhotoUrl = user?.photoURL,
                    userInitials = user?.initials,
                    onChangePhoto = {}
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShimmerItem(modifier = Modifier.fillMaxWidth().height(100.dp))
                ShimmerItem(modifier = Modifier.fillMaxWidth().height(60.dp))
                FeedPostShimmer()
                FeedPostShimmer()
            }
        }
    }
}

@Composable
fun LiveUpdatesView(
    posts: List<FeedPost>,
    user: dev.bti.kdym.data.models.AppUser?,
    onPostClick: (String) -> Unit,
    onReactionClick: (String, String) -> Unit,
    onChangePhoto: () -> Unit,
    onCreatePostClick: () -> Unit,
    getUserReaction: (String) -> kotlinx.coroutines.flow.Flow<String?>,
    viewModel: MainViewModel
) {
    var selectedFilter by remember { mutableStateOf("NEW") }
    val filters = listOf("NEW", "PINNED", "URGENT", "MY GROUPS")
    val isOverlayRecentlyDismissed by viewModel.isOverlayRecentlyDismissed.collectAsState(initial = false)

    OutpourBackground {
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                ScreenHeader(
                    isPrimary = true,
                    isCampMode = true, // Set to false in StandardHomeView
                    userPhotoUrl = user?.photoURL,
                    userInitials = user?.initials,
                    onChangePhoto = onChangePhoto // use {} in StandardHomeView
                )

                if (isOverlayRecentlyDismissed) {
                    IconButton(
                        onClick = { viewModel.showOverlayAgain() },
                        modifier = Modifier
                            .align(Alignment.CenterEnd) // This acts as your right constraint
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                            .size(48.dp) // Optional: locks in the touch target size
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "View Overlay Again",
                            tint = Color(0xFF22D3EE)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        backgroundColor = Color(0xFFEF4444).copy(0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CAMP MODE",
                                    color = Color(0xFFEF4444).copy(0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = QuickSandFontFamily
                                )
                                Text(
                                    text = "Live updates are on",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuickSandFontFamily
                                )
                            }

                            // Pushes the button to the far right
                            Spacer(modifier = Modifier.weight(1f))

                            // Admin-only plus button
                            if (user?.isAdmin == true) {
                                IconButton(
                                    onClick = onCreatePostClick,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Create Home Post",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Column {
                        Text(
                            text = "FOR YOU",
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "LIVE UPDATES",
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { filter ->
                            val isSelected = selectedFilter == filter
                            Surface(
                                onClick = { selectedFilter = filter },
                                color = if (isSelected) Color.White else Color.White.copy(0.05f),
                                shape = CircleShape,
                                modifier = Modifier.height(40.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (filter == "NEW") Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    if (filter == "PINNED") Icon(
                                        imageVector = Icons.Default.PushPin,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    if (filter == "URGENT") Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    if (filter == "MY GROUPS") Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = filter,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = QuickSandFontFamily
                                    )
                                }
                            }
                        }
                    }
                }

                val filteredPosts = posts.filter { post ->
                    when (selectedFilter) {
                        "PINNED" -> post.isPinned
                        "URGENT" -> post.priority == dev.bti.kdym.data.models.FeedPostPriority.important
                        else -> true
                    }
                }

                items(filteredPosts) { post ->
                    val userReaction by getUserReaction(post.id).collectAsState(initial = null)
                    FeedPostCard(
                        post = post,
                        userReaction = userReaction,
                        onReactionClick = onReactionClick,
                        onClick = { onPostClick(post.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(140.dp)) }
            }
        }
    }
}

@Composable
fun StandardHomeView(
    camps: List<Camp>,
    user: dev.bti.kdym.data.models.AppUser?,
    onNavigateToRequestAccess: () -> Unit,
    viewModel: MainViewModel
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }

    // Animation states for the gesture and in-place transition
    val dragOffsetY = remember { Animatable(0f) }
    val cardScale = remember { Animatable(1f) }
    val cardAlpha = remember { Animatable(1f) }

    val currentCamp = camps[currentIndex]
    val accentColor = try {
        Color(android.graphics.Color.parseColor(currentCamp.accentColor))
    } catch (e: Exception) {
        Color(0xFFEF4444)
    }

    // Trigger haptic feedback on index change
    LaunchedEffect(currentIndex) {
        if (currentIndex != 0) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    HomeBackground(
        accentColor = accentColor,
        themeText = currentCamp.theme ?: "",
        dragOffset = dragOffsetY.value,
        campIndex = currentIndex
    ) {
        val isOverlayRecentlyDismissed by viewModel.isOverlayRecentlyDismissed.collectAsState(initial = false)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    val threshold = 200f // Swipe threshold to trigger change
                    val resistance = 0.4f // Makes the card feel heavy/physical

                    detectVerticalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (dragOffsetY.value < -threshold && currentIndex < camps.size - 1) {
                                    // Swiped UP -> Next Camp
                                    currentIndex++
                                    dragOffsetY.snapTo(0f)
                                    cardScale.snapTo(0.8f)
                                    cardAlpha.snapTo(0f)

                                    launch {
                                        cardScale.animateTo(
                                            1f,
                                            tween(400, easing = EaseOutBack)
                                        )
                                    }
                                    launch { cardAlpha.animateTo(1f, tween(300)) }

                                } else if (dragOffsetY.value > threshold && currentIndex > 0) {
                                    // Swiped DOWN -> Previous Camp
                                    currentIndex--
                                    dragOffsetY.snapTo(0f)
                                    cardScale.snapTo(0.8f)
                                    cardAlpha.snapTo(0f)

                                    launch {
                                        cardScale.animateTo(
                                            1f,
                                            tween(400, easing = EaseOutBack)
                                        )
                                    }
                                    launch { cardAlpha.animateTo(1f, tween(300)) }

                                } else {
                                    // Didn't reach threshold, snap back
                                    dragOffsetY.animateTo(
                                        0f,
                                        spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                                }
                            }
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                // Apply resistance to the drag
                                val newOffset = dragOffsetY.value + (dragAmount * resistance)
                                dragOffsetY.snapTo(newOffset)
                            }
                        }
                    )
                }
        ) {

            // Main Card Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Drag translation with a slight rotation for style
                        translationY = dragOffsetY.value
                        rotationZ = dragOffsetY.value * 0.01f

                        // In-place entry animations
                        scaleX = cardScale.value
                        scaleY = cardScale.value
                        alpha = cardAlpha.value

                        // Slightly fade out as you drag further away
                        val dragFade =
                            1f - (dragOffsetY.value.absoluteValue / 1000f).coerceIn(0f, 0.5f)
                        alpha *= dragFade
                    }
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(top = 40.dp, bottom = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth().scale(0.85f),
                    backgroundColor = Color.Black.copy(0.3f),
                    cornerRadius = 48.dp,
                    contentPadding = 0.dp
                ) {
                    if (currentCamp.isActive) {
                        ActiveCampCard(currentCamp, user, onNavigateToRequestAccess)
                    } else {
                        HistoricalCampCard(currentCamp)
                    }
                }
            }

            // Top Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                ScreenHeader(
                    isPrimary = true,
                    isCampMode = false,
                    userPhotoUrl = user?.photoURL,
                    userInitials = user?.initials,
                    onChangePhoto = {}
                )

                if (isOverlayRecentlyDismissed) {
                    IconButton(
                        onClick = { viewModel.showOverlayAgain() },
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "View Overlay Again",
                            tint = Color(0xFF22D3EE)
                        )
                    }
                }
            }

            // Swipe Indicator
            if (camps.size > 1) { // Only show indicator if there is actually history to swipe through
                val isAtTop = currentIndex == 0
                val isAtBottom = currentIndex == camps.size - 1

                val indicatorText = when {
                    isAtBottom -> "BEGIN AGAIN"
                    !isAtTop -> "THERE'S MORE..."
                    else -> "SWIPE UP THROUGH CAMP HISTORY"
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 150.dp)
                        .graphicsLayer {
                            // Hide indicator when dragging
                            alpha = 1f - (dragOffsetY.value.absoluteValue / 100f).coerceIn(0f, 1f)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Show Up Arrow if we are past the first item
                    if (!isAtTop) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Swipe Down",
                            tint = Color.White.copy(0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = indicatorText,
                        color = Color.White.copy(0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )

                    // Show Down Arrow if we haven't reached the bottom
                    if (!isAtBottom) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Swipe Up",
                            tint = Color.White.copy(0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun ActiveCampCard(
    camp: Camp,
    user: dev.bti.kdym.data.models.AppUser?,
    onNavigateToRequestAccess: () -> Unit
) {
    val accentColor = try {
        Color(camp.accentColor.toColorInt())
    } catch (e: Exception) {
        Color(0xFFEF4444)
    }

    val targetDate = remember(camp.startDate) { camp.startDate?.toDate() ?: java.util.Date() }
    var isPastStartDate by remember { mutableStateOf(targetDate.time <= System.currentTimeMillis()) }

    LaunchedEffect(targetDate) {
        while (!isPastStartDate) {
            isPastStartDate = targetDate.time <= System.currentTimeMillis()
            if (isPastStartDate) break
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeroCard(camp = camp)
        if (!isPastStartDate) {
            RegistrationButton()
        }
        VerseCard(
            accentColor = accentColor,
            verse = camp.verse,
            reference = camp.verseReference,
            tagline = camp.verseTagline
        )
    }
}

@Composable
fun HistoricalCampCard(camp: Camp) {
    MemoriesCard(camp = camp)
}
