package dev.bti.kdym.ui.screens.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.data.models.FeedPostSource
import dev.bti.kdym.ui.components.AnimatedFab
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.components.home.FeedPostCard
import dev.bti.kdym.ui.components.home.HeroCard
import dev.bti.kdym.ui.components.home.RegistrationCard
import dev.bti.kdym.ui.components.home.VerseCard
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.viewmodels.MainViewModel
import java.util.Calendar

enum class LiveUpdateFilter(val title: String) {
    ALL("ALL"),
    ANNOUNCEMENTS("ANNOUNCEMENTS"),
    TRIBE_WARS("TRIBE WARS"),
    PINNED("PINNED")
}


@Composable
fun HomeScreen(
    onNavigateToComments: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val campDate = remember {
        Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 1, 0, 0)
        }.time
    }

    val posts by viewModel.liveUpdates.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val user by viewModel.user.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false

    var selectedFilter by remember { mutableStateOf(LiveUpdateFilter.ALL) }

    val filteredPosts = remember(posts, announcements, selectedFilter) {
        val mergedUpdates = posts + announcements.map {
            FeedPost(
                id = it.id,
                title = it.title,
                body = it.body,
                source = FeedPostSource.announcement,
                createdAt = it.createdAt ?: com.google.firebase.Timestamp.now(),
                createdByName = it.createdByName
            )
        }

        val sorted = mergedUpdates.sortedByDescending { it.createdAt }

        when (selectedFilter) {
            LiveUpdateFilter.ALL -> sorted
            LiveUpdateFilter.ANNOUNCEMENTS -> sorted.filter { it.source == FeedPostSource.announcement }
            LiveUpdateFilter.TRIBE_WARS -> sorted.filter { it.source == FeedPostSource.tribeWarScore }
            LiveUpdateFilter.PINNED -> sorted.filter { it.isPinned }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (user?.hasCommandAccess == true) {
                AnimatedFab(
                    onClick = {
                        onNavigateToCreatePost()
                        Log.d("FAB", "clicked")
                    },
                    modifier = Modifier.padding(bottom = 110.dp, end = 16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Post")
                }
            }
        }
    ) { padding ->
        OutpourBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                ScreenHeader(isPrimary = true, isCampMode = isCampMode)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    if (isCampMode) {
                        CampModeCard()
                    } else {
                        HeroCard(campDate)
                    }

                    if (!isCampMode) {
                        Spacer(modifier = Modifier.height(24.dp))
                        VerseCard(isCampMode = isCampMode)

                        Spacer(modifier = Modifier.height(24.dp))
                        RegistrationCard()
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    LiveUpdatesHeader(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    filteredPosts.forEach { post ->
                        val userReaction by viewModel.getUserReaction(post.id)
                            .collectAsState(initial = null)
                        FeedPostCard(
                            post = post,
                            userReaction = userReaction,
                            onReactionClick = { postId, reaction ->
                                viewModel.toggleReaction(postId, reaction)
                            },
                            onClick = { onNavigateToComments(post.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }
    }
}

@Composable
fun CampModeCard() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.4f),
        contentPadding = 24.dp,
        cornerRadius = 32.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFEF4444)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "CAMP MODE",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "Live updates for your camp day",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
        }
    }
}

@Composable
fun LiveUpdatesHeader(
    selectedFilter: LiveUpdateFilter,
    onFilterSelected: (LiveUpdateFilter) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "FOR YOU",
            color = RedAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            fontFamily = RubikFontFamily
        )
        Text(
            text = "LIVE UPDATES",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontFamily = RubikFontFamily
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(LiveUpdateFilter.entries.toList()) { filter ->
                FilterChip(
                    label = filter.title,
                    isSelected = selectedFilter == filter,
                    onClick = { onFilterSelected(filter) }
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.White.copy(0.05f),
        shape = RoundedCornerShape(18.dp),
        border = if (isSelected) null else BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.Black else Color.White.copy(0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontFamily = RubikFontFamily
            )
        }
    }
}
