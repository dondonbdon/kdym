package dev.bti.kdym.ui.screens.groups

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun CommunityScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToTribeWars: () -> Unit,
    onExploreGroups: () -> Unit,
    onCreateGroup: () -> Unit,
    onAddScore: () -> Unit = {},
    viewModel: GroupsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    adminViewModel: AdminViewModel
) {
    var selectedTab by remember { mutableStateOf("GROUPS") }
    val appConfig by mainViewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false
    val user by mainViewModel.user.collectAsState()
    
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (user?.hasCommandAccess == true) {
                AdminCommunityFAB(
                    selectedTab = selectedTab,
                    expanded = fabExpanded,
                    onToggle = { fabExpanded = !fabExpanded },
                    onNavigateToAnnouncements = onNavigateToAnnouncements,
                    onExploreGroups = onExploreGroups,
                    onCreateGroup = onCreateGroup,
                    onAddScore = onAddScore
                )
            }
        }
    ) { padding ->
        OutpourBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Community Header
                ScreenHeader(
                    title = "COMMUNITY",
                    subtitle = "Connect with your camp family.",
                    icon = Icons.Default.Diversity3
                )

                // Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CommunityTabButton(
                        label = "CAMP GROUND",
                        icon = Icons.Default.Map,
                        isSelected = selectedTab == "CAMP",
                        modifier = Modifier.weight(1.1f)
                    ) { selectedTab = "CAMP" }

                    if (isCampMode) {
                        CommunityTabButton(
                            label = "TRIBE WARS",
                            icon = Icons.Default.Flag,
                            isSelected = selectedTab == "TRIBE_WARS",
                            modifier = Modifier.weight(1f)
                        ) { selectedTab = "TRIBE_WARS" }
                    }

                    CommunityTabButton(
                        label = "GROUPS",
                        icon = Icons.Default.Forum,
                        isSelected = selectedTab == "GROUPS",
                        modifier = Modifier.weight(0.9f)
                    ) { selectedTab = "GROUPS" }
                }

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        "CAMP" -> CampGroundTab()
                        "TRIBE_WARS" -> TribeWarsTab(adminViewModel)
                        "GROUPS" -> GroupsTab(
                            onNavigateToChat = onNavigateToChat,
                            onExploreGroups = onExploreGroups,
                            onCreateGroup = onCreateGroup,
                            viewModel = viewModel,
                            mainViewModel = mainViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCommunityFAB(
    selectedTab: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onExploreGroups: () -> Unit,
    onCreateGroup: () -> Unit,
    onAddScore: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 120.dp, end = 8.dp)
    ) {
        if (expanded) {
            when (selectedTab) {
                "GROUPS" -> {
                    FABAction(label = "Create Announcement", icon = Icons.Default.Campaign) {
                        onToggle()
                        onNavigateToAnnouncements()
                    }
                    FABAction(label = "Create Group", icon = Icons.Default.Add) {
                        onToggle()
                        onCreateGroup()
                    }
                    FABAction(label = "Explore Groups", icon = Icons.Default.Search) {
                        onToggle()
                        onExploreGroups()
                    }
                }
                "TRIBE_WARS" -> {
                    FABAction(label = "Add Score", icon = Icons.Default.Add) {
                        onToggle()
                        onAddScore()
                    }
                }
            }
        }

        AnimatedFab(
            onClick = onToggle
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Options"
            )
        }
    }
}

@Composable
fun FABAction(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = Color.Black.copy(0.7f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = label.uppercase(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                letterSpacing = 1.sp,
                fontFamily = RubikFontFamily
            )
        }
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = Color.White,
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun CommunityTabButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.White.copy(0.05f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White.copy(0.6f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = if (isSelected) Color.Black else Color.White.copy(0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun GroupsTab(
    onNavigateToChat: (String) -> Unit,
    onExploreGroups: () -> Unit,
    onCreateGroup: () -> Unit,
    viewModel: GroupsViewModel,
    mainViewModel: MainViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val user by mainViewModel.user.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 140.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CHATS",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "YOUR GROUPS",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }
            }
        }

        if (groups.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White.copy(0.05f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No groups yet",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RubikFontFamily
                        )
                        Text(
                            text = "When admins add you to groups, they will appear here.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontFamily = RubikFontFamily
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onExploreGroups,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "EXPLORE GROUPS", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                        }
                    }
                }
            }
        } else {
            items(groups) { group ->
                GroupListCard(group = group, onClick = { onNavigateToChat(group.id) })
            }
        }
    }
}

@Composable
fun TribeWarsTab(viewModel: AdminViewModel) {
    val tribes by viewModel.tribes.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 140.dp)
    ) {
        item {
            Text(
                text = "LIVE SCOREBOARD",
                color = Color(0xFFEF4444),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "RANKINGS",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(tribes) { tribe ->
            TribeSimpleScoreCard(tribe)
        }
    }
}

@Composable
fun TribeSimpleScoreCard(tribe: Tribe) {
    val color = try {
        Color(tribe.colorHex.toColorInt())
    } catch (_: Exception) {
        Color(0xFFEF4444)
    }
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = color.copy(alpha = 0.1f),
        borderColor = color.copy(alpha = 0.2f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Text(
                text = "#${tribe.rank}",
                color = if (tribe.rank == 1) Color(0xFFEAB308) else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                modifier = Modifier.width(40.dp)
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tribe.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "${tribe.memberCount} members",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = RubikFontFamily
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = tribe.totalPoints.toString(),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "PTS",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
        }
    }
}

@Composable
fun CampGroundTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "CAMP GROUND MAP", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
