package dev.bti.kdym.ui.screens.groups

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.GroupListCard
import dev.bti.kdym.ui.components.MappedIcon
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun CommunityScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToTribeWars: () -> Unit,
    onExploreGroups: () -> Unit,
    onCreateGroup: () -> Unit,
    onAddScore: (String?) -> Unit = {},
    onCreateTribeEvent: (String?) -> Unit = {},
    viewModel: GroupsViewModel = hiltViewModel(
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!,
        key = "groups_vm_community"
    ),
    mainViewModel: MainViewModel = hiltViewModel(
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!,
        key = "main_vm_community"
    ),
    adminViewModel: AdminViewModel = hiltViewModel(
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!,
        key = "admin_vm_community"
    )
) {
    val appConfig by mainViewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false
    val user by mainViewModel.user.collectAsState()

    val selectedTab by viewModel.selectedTab.collectAsState()
    val adminViewMode by viewModel.adminViewMode.collectAsState()

    var showOnlyAdminGroups by remember { mutableStateOf(false) }

    val tribeRevealShown by mainViewModel.tribeRevealShown.collectAsState()
    val guessedTribe by mainViewModel.guessedTribe.collectAsState()
    val tribes by adminViewModel.tribes.collectAsState()
    val allGroups by viewModel.groups.collectAsState()

    val userTribe = remember(user, tribes) { tribes.find { it.id == user?.tribeId } }
    val tribeGroupId = remember(userTribe, allGroups) { allGroups.find { it.tribeId == userTribe?.id }?.id }

    // Target: June 1, 3:00 PM Kansas Time (Central Standard/Daylight Time)
    val kansasZone = remember { ZoneId.of("America/Chicago") }
    val targetZonedDateTime = remember {
        ZonedDateTime.of(2026, 6, 1, 15, 0, 0, 0, kansasZone)
    }

    // Reactive timer state
    var isPastReveal by remember { mutableStateOf(ZonedDateTime.now(kansasZone).isAfter(targetZonedDateTime)) }

    val isSuperAdminOrAllowedEmail = remember(user) {
        user?.roleEnum?.isSuperAdmin == true || user?.email == "nathanleonard1127@gmail.com".trim()
    }

    LaunchedEffect(Unit) {
        while (!isPastReveal) {
            delay(1000)
            isPastReveal = ZonedDateTime.now(kansasZone).isAfter(targetZonedDateTime)
        }
    }

    var showManualReveal by remember { mutableStateOf(false) }

    val isAssigned = user?.tribeId != null
    LaunchedEffect(isPastReveal, isAssigned) {
        if (isPastReveal && isAssigned) {
            delay(500)

            if (!mainViewModel.tribeRevealShown.value) {
                showManualReveal = true
            }
        }
    }

    val canSeeTribeWars = remember(user, isCampMode) {
        (user?.roleEnum?.canAccessCampContent == true || user?.isAdmin == true)
    }

    Scaffold(
        containerColor = Color.Transparent,
    ) { padding ->
        OutpourBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Community Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diversity3,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "COMMUNITY",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if ((user?.isAdmin == true || user?.roleEnum?.isTribeLeader == true) && selectedTab == "GROUPS") {
                            IconButton(
                                onClick = { viewModel.toggleAdminViewMode() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(if (adminViewMode) Color.White.copy(0.2f) else Color.White.copy(0.05f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (adminViewMode) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Admin View",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }



                        val shouldShowMenu = selectedTab == "GROUPS"

                        if (shouldShowMenu) {
                            var showMenu by remember { mutableStateOf(false) }
                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White.copy(0.05f), CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier.background(Color(0xFF1A1A1A))
                                ) {
                                    if (user?.isAdmin == true) {
                                        DropdownMenuItem(
                                            text = { Text("Create Group", color = Color.White) },
                                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                                            onClick = { showMenu = false; onCreateGroup() }
                                        )
                                    }

                                    DropdownMenuItem(
                                        text = { Text("Explore Groups", color = Color.White) },
                                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                                        onClick = { showMenu = false; onExploreGroups() }
                                    )
                                }
                            }
                        }
                    }
                }

                // Tab Switcher
                if (canSeeTribeWars) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CommunityTabCard(
                            label = "TRIBE WARS",
                            subtitle = if (isPastReveal) "REVEALED + SCOREBOARD" else "REVEALS JUNE 1",
                            icon = Icons.Default.Flag,
                            isSelected = selectedTab == "TRIBE_WARS",
                            showLiveBadge = isCampMode,
                            modifier = Modifier.weight(1f),
                            activeColor = Color(0xFF22D3EE)
                        ) { viewModel.selectTab("TRIBE_WARS") }

                        CommunityTabCard(
                            label = "GROUPS",
                            subtitle = "MESSAGES & GROUPS",
                            icon = Icons.Default.Forum,
                            isSelected = selectedTab == "GROUPS",
                            modifier = Modifier.weight(1f)
                        ) { viewModel.selectTab("GROUPS") }
                    }
                }

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        "TRIBE_WARS" -> {
                            if (canSeeTribeWars) {
                                TribeWarsTab(
                                    viewModel = adminViewModel,
                                    mainViewModel = mainViewModel,
                                    isPastReveal = isPastReveal,
                                    isCampMode = isCampMode,
                                    targetZonedDateTime = targetZonedDateTime,
                                    onRevealClick = { showManualReveal = true },
                                    onAddScore = onAddScore,
                                    onCreateEvent = onCreateTribeEvent
                                )
                            } else GroupsTab(
                                onNavigateToChat = onNavigateToChat,
                                onExploreGroups = onExploreGroups,
                                onCreateGroup = onCreateGroup,
                                viewModel = viewModel,
                                mainViewModel = mainViewModel,
                                tribes = tribes,
                                adminOnly = adminViewMode
                            )
                        }
                        "GROUPS" -> GroupsTab(
                            onNavigateToChat = onNavigateToChat,
                            onExploreGroups = onExploreGroups,
                            onCreateGroup = onCreateGroup,
                            viewModel = viewModel,
                            mainViewModel = mainViewModel,
                            tribes = tribes,
                            adminOnly = adminViewMode
                        )
                        else -> GroupsTab(
                            onNavigateToChat = onNavigateToChat,
                            onExploreGroups = onExploreGroups,
                            onCreateGroup = onCreateGroup,
                            viewModel = viewModel,
                            mainViewModel = mainViewModel,
                            tribes = tribes,
                            adminOnly = adminViewMode
                        )
                    }
                }
            }
        }

        // Show manual overlay only when requested
        if (showManualReveal && userTribe != null) {
            TribeRevealOverlay(
                tribe = userTribe,
                tribeGroupId = tribeGroupId,
                guessedTribe = guessedTribe,
                onDismiss = {
                    mainViewModel.setTribeRevealShown(true)
                    showManualReveal = false
                },
                onNavigateToChat = onNavigateToChat
            )
        }
    }
}

@Composable
fun CommunityTabCard(
    label: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    showLiveBadge: Boolean = false,
    activeColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        color = if (isSelected) activeColor.copy(0.2f) else Color.White.copy(0.05f),
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, activeColor.copy(0.3f)) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) activeColor else Color.White.copy(0.6f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = label,
                    color = if (isSelected) activeColor else Color.White.copy(0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    letterSpacing = 1.sp
                )
                if (showLiveBadge) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(0.2f), CircleShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = if (isSelected) Color.White else Color.White.copy(0.4f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSandFontFamily,
                letterSpacing = 0.5.sp
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
    mainViewModel: MainViewModel,
    tribes: List<Tribe>,
    adminOnly: Boolean = false
) {
    val allGroups by viewModel.groups.collectAsState()
    val user by mainViewModel.user.collectAsState()

    val groups = remember(allGroups, adminOnly, user) {
        val uid = user?.uid ?: return@remember emptyList()

        val hasAdminRights = user!!.isAdmin
        val effectiveAdminOnly = adminOnly && hasAdminRights

        val calculatedGroups = if (effectiveAdminOnly) {
            println("DEBUG: adminOnly is TRUE. Returning all ${allGroups.size} groups.")
            allGroups
        } else {
            allGroups.filter { group ->
                val isLeader = group.leaderIds.contains(uid)
                val isCreator = group.createdBy == uid
                val isMember = group.memberIds.contains(uid)
                val isMyTribe = group.type == AppGroupType.tribe && group.tribeId == user!!.tribeId

                val shouldInclude = isLeader || isCreator || isMember || isMyTribe

                if (shouldInclude) {
                    println("DEBUG: Filter kept -> '${group.name}'")
                }
                shouldInclude
            }
        }

        println("DEBUG: Final list size passed to UI: ${calculatedGroups.size}")
        calculatedGroups
    }

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
                        fontFamily = QuickSandFontFamily
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
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "When admins add you to groups, they will appear here.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontFamily = QuickSandFontFamily
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
                            Text(text = "EXPLORE GROUPS", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                        }
                    }
                }
            }
        } else {
            items(groups, key = { it.id }) { group ->
                val effectiveGroup = if (group.type == AppGroupType.tribe) {
                    val matchingTribe = tribes.find { it.id == group.tribeId }
                    if (matchingTribe != null) {
                        group.copy(
                            iconName = matchingTribe.iconName,
                            colorHex = matchingTribe.colorHex
                        )
                    } else group
                } else group

                GroupListCard(group = effectiveGroup, onClick = { onNavigateToChat(group.id) })
            }
        }
    }
}

@Composable
fun TribeWarsTab(
    viewModel: AdminViewModel,
    mainViewModel: MainViewModel = hiltViewModel(
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!,
        key = "main_vm_tribe_tab"
    ),
    isPastReveal: Boolean,
    isCampMode: Boolean,
    targetZonedDateTime: ZonedDateTime,
    onRevealClick: () -> Unit,
    onAddScore: (String?) -> Unit = {},
    onCreateEvent: (String?) -> Unit = {}
) {
    val tribes by viewModel.tribes.collectAsState()
    val tribeEvents by viewModel.tribeEvents.collectAsState()
    val user by mainViewModel.user.collectAsState()
    val guessedTribe by mainViewModel.guessedTribe.collectAsState()
    val tribeRevealShown by mainViewModel.tribeRevealShown.collectAsState()

    val isAssigned = user?.tribeId != null
    val isAdmin = remember(user) {
        user?.roleEnum?.isSuperAdmin == true || user?.email == "nathanleonard1127@gmail.com".trim()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 140.dp)
    ) {
        if (!isPastReveal) {
            // STATE 1: Counting Down
            item { TribeWarsCountdownCard(targetZonedDateTime) }
            item { TribeRevealCard(isAssigned = isAssigned, canReveal = false, onRevealClick = {}) }
            item {
                GuessYourTribeSection(
                    currentGuess = guessedTribe,
                    onGuess = { mainViewModel.guessTribe(it) }
                )
            }
            item { TribeWarsInfoList() }

        } else if (!tribeRevealShown && isAssigned) {
            // STATE 2: Timer hit 0, User is assigned but hasn't revealed yet
            item { TribeRevealCard(isAssigned = true, canReveal = true, onRevealClick = onRevealClick) }

        } else {
            // STATE 3: Post Reveal or Unassigned
            if (!isCampMode) {
                item { TribeWarsStartingSoonCard() }
            } else {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "SCOREBOARD",
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
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                }

                items(tribes, key = { it.id }) { tribe ->
                    TribeSimpleScoreCard(
                        tribe = tribe,
                        isAdmin = isAdmin,
                        onAddScore = { onAddScore(tribe.id) }
                    )
                }

                if (isAdmin) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "ADMIN",
                            color = Color(0xFFEF4444),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "SCORE CONTROLS",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ScoreActionButton(
                                icon = Icons.Default.Flag,
                                label = "CREATE EVENT",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onCreateEvent(null) }
                            )
                            ScoreActionButton(
                                icon = Icons.Default.AddBusiness,
                                label = "ADD POINTS",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onAddScore(null) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.finalizeScoreboard() },
                            cornerRadius = 32.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = Color(0xFFEAB308)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "FINALIZE SCOREBOARD",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = QuickSandFontFamily
                                    )
                                    Text(
                                        text = "Publish current rankings to the Home feed.",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontFamily = QuickSandFontFamily
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White.copy(0.3f)
                                )
                            }
                        }
                    }
                }

                // Tribe Events Section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "EVENTS",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "TRIBE EVENTS",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (tribeEvents.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No active events",
                                color = TextSecondary,
                                modifier = Modifier.padding(16.dp),
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                } else {
                    items(tribeEvents) { event ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCreateEvent(event.id) },
                            cornerRadius = 24.dp
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(40.dp).background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = event.title.uppercase(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                                    Text(text = "${event.maxPoints} PTS AVAILABLE", color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                                }
                                if (isAdmin) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(0.3f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Recent Scores Section (Placeholder)
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "ACTIVITY",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "RECENT SCORES",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Score history will appear here.",
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TribeWarsCountdownCard(targetZonedDateTime: ZonedDateTime) {
    var remainingTime by remember { mutableStateOf(Duration.between(ZonedDateTime.now(), targetZonedDateTime)) }

    LaunchedEffect(Unit) {
        while (true) {
            remainingTime = Duration.between(ZonedDateTime.now(), targetZonedDateTime)
            delay(1000)
        }
    }

    val days = remember(remainingTime) { remainingTime.toDays().coerceAtLeast(0).toString() }
    val hours = remember(remainingTime) { (remainingTime.toHours() % 24).coerceAtLeast(0).toString().padStart(2, '0') }
    val minutes = remember(remainingTime) { (remainingTime.toMinutes() % 60).coerceAtLeast(0).toString().padStart(2, '0') }
    val seconds = remember(remainingTime) { (remainingTime.seconds % 60).coerceAtLeast(0).toString().padStart(2, '0') }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(0.4f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "COUNTDOWN TO CHECK-IN", color = Color(0xFF22D3EE), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "TRIBE", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily, lineHeight = 44.sp)
            Text(text = "WARS", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Black, fontFamily = RubikGlitchFontFamily, lineHeight = 44.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tribe Wars unlocks when campers check in. Your tribe will be reviewed at 3:00 PM on June 1, then the scoreboard goes live.",
                color = TextSecondary,
                fontSize = 14.sp,
                fontFamily = QuickSandFontFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CountdownUnit(label = "DAYS", value = days)
                CountdownUnit(label = "HRS", value = hours)
                CountdownUnit(label = "MIN", value = minutes)
                CountdownUnit(label = "SEC", value = seconds)
            }
        }
    }
}

@Composable
fun TribeWarsStartingSoonCard() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(0.4f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "STARTING SOON",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The countdown is over! The scoreboard will go live as soon as leadership officially enables Camp Mode.",
                color = TextSecondary,
                fontSize = 15.sp,
                fontFamily = QuickSandFontFamily,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun CountdownUnit(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp).background(Color.White.copy(0.05f), RoundedCornerShape(16.dp)).padding(vertical = 12.dp)) {
        Text(text = value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Text(text = label, color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun TribeRevealCard(isAssigned: Boolean, canReveal: Boolean, onRevealClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.White.copy(0.05f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).background(Color(0xFF22D3EE).copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "YOUR TRIBE", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Text(
                        text = if (canReveal) "READY TO REVEAL" else if (isAssigned) "TRIBE ASSIGNED" else "REVEALED AT CHECK-IN",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = if (canReveal) "Your tribe assignment is ready. Tap below to reveal!"
                        else if (isAssigned) "You have been placed into a tribe. It will be revealed when the countdown ends."
                        else "Leadership will confirm your tribe when camp starts.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            if (canReveal) {
                Spacer(modifier = Modifier.height(16.dp))

                val infiniteTransition = rememberInfiniteTransition(label = "gradientShift")
                val gradientOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1000f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "offset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .drawWithCache {
                            val shiftingBrush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFEF4444), // Red
                                    Color(0xFFF59E0B), // Orange
                                    Color(0xFF10B981), // Green
                                    Color(0xFF22D3EE), // Blue
                                    Color(0xFFEF4444)  // Loop back to red
                                ),
                                start = androidx.compose.ui.geometry.Offset(gradientOffset - 1000f, 0f),
                                end = androidx.compose.ui.geometry.Offset(gradientOffset, 0f)
                            )
                            onDrawBehind {
                                drawRect(shiftingBrush)
                            }
                        }
                        .clickable { onRevealClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Text(
                            text = "REVEAL MY TRIBE",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TribeRevealOverlay(
    tribe: Tribe,
    tribeGroupId: String?,
    guessedTribe: String?,
    onDismiss: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val tribeColor = remember(tribe.colorHex) {
        try {
            Color(tribe.colorHex.toColorInt())
        } catch (_: Exception) {
            Color(0xFF22D3EE)
        }
    }

    var isRevealed by remember { mutableStateOf(false) }

    // 1.5 second suspense delay before exploding into the reveal
    LaunchedEffect(Unit) {
        delay(1500)
        isRevealed = true
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.95f)),
            contentAlignment = Alignment.Center
        ) {
            // SUSPENSE STATE
            AnimatedVisibility(
                visible = !isRevealed,
                exit = fadeOut(tween(300)) + scaleOut(tween(300))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(48.dp))
                    Text(
                        text = "FINDING YOUR TRIBE...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            // REVEAL STATE
            AnimatedVisibility(
                visible = isRevealed,
                enter = fadeIn(tween(700)) + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f))
            ) {
                // Background radial glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(tribeColor.copy(0.4f), Color.Transparent),
                                radius = 1000f
                            )
                        )
                )

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Tribe Icon
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(tribeColor.copy(0.2f), CircleShape)
                            .border(2.dp, tribeColor.copy(0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        MappedIcon(
                            iosName = tribe.iconName ?: "shield.fill",
                            tint = tribeColor,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    // Headers
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "YOU ARE IN",
                            color = Color.White.copy(0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = tribe.name.uppercase(),
                            color = Color.White,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2).sp,
                            fontFamily = QuickSandFontFamily,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    // Guess Results
                    if (guessedTribe != null) {
                        val isCorrect = guessedTribe.equals(tribe.name, ignoreCase = true)
                        GlassCard(
                            backgroundColor = if (isCorrect) Color(0xFF10B981).copy(0.15f) else Color.White.copy(0.05f),
                            borderColor = if (isCorrect) Color(0xFF10B981).copy(0.3f) else Color.White.copy(0.1f),
                            cornerRadius = 20.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isCorrect) Color(0xFF10B981) else Color.White.copy(0.4f)
                                )
                                Column {
                                    Text(
                                        text = "YOUR GUESS: ${guessedTribe.uppercase()}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = if (isCorrect) "Spot on! You knew it." else "Not quite, but this is exactly where you belong.",
                                        color = if (isCorrect) Color(0xFF10B981) else TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = QuickSandFontFamily
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    if (tribeGroupId != null) {
                        Button(
                            onClick = {
                                onDismiss()
                                onNavigateToChat(tribeGroupId)
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = tribeColor, contentColor = Color.Black),
                            shape = CircleShape
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(20.dp))
                                Text(text = "ENTER TRIBE CHAT", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily, letterSpacing = 1.sp)
                            }
                        }
                    }

                    TextButton(onClick = onDismiss) {
                        Text(text = "CLOSE FOR NOW", color = Color.White.copy(0.6f), fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GuessYourTribeSection(currentGuess: String?, onGuess: (String) -> Unit) {
    Column {
        Text(text = "BEFORE REVEAL", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Text(text = "GUESS YOUR TRIBE", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, backgroundColor = Color.White.copy(0.05f)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Casino, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Take a guess before check-in.", color = TextSecondary, fontSize = 13.sp, fontFamily = QuickSandFontFamily)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GuessButton(label = "BLUE", color = Color(0xFF22D3EE), icon = Icons.Default.Shield, isSelected = currentGuess == "BLUE", modifier = Modifier.weight(1f)) { onGuess("BLUE") }
                    GuessButton(label = "GREEN", color = Color(0xFF10B981), icon = Icons.Default.ElectricBolt, isSelected = currentGuess == "GREEN", modifier = Modifier.weight(1f)) { onGuess("GREEN") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GuessButton(label = "ORANGE", color = Color(0xFFF59E0B), icon = Icons.Default.FlashOn, isSelected = currentGuess == "ORANGE", modifier = Modifier.weight(1f)) { onGuess("ORANGE") }
                    GuessButton(label = "RED", color = Color(0xFFEF4444), icon = Icons.Default.LocalFireDepartment, isSelected = currentGuess == "RED", modifier = Modifier.weight(1f)) { onGuess("RED") }
                }
            }
        }
    }
}

@Composable
fun GuessButton(label: String, color: Color, icon: ImageVector, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(48.dp).clickable { onClick() },
        color = color.copy(alpha = if (isSelected) 0.3f else 0.1f),
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun TribeWarsInfoList() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoRow(icon = Icons.Default.Group, title = "GET ASSIGNED", desc = "Campers are placed into tribes by leadership.")
        InfoRow(icon = Icons.Default.Flag, title = "COMPETE ALL WEEK", desc = "Games, services, activities, and surprise challenges can affect points.")
        InfoRow(icon = Icons.Default.EmojiEvents, title = "WATCH THE BOARD", desc = "Rankings unlock when Tribe Wars officially starts.")
    }
}

@Composable
fun InfoRow(icon: ImageVector, title: String, desc: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        backgroundColor = Color.White.copy(0.05f),
        contentPadding = 16.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                Text(text = desc, color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
            }
        }
    }
}

@Composable
fun TribeSimpleScoreCard(tribe: Tribe, isAdmin: Boolean = false, onAddScore: () -> Unit = {}) {
    val color = remember(tribe.colorHex) {
        try {
            Color(tribe.colorHex.toColorInt())
        } catch (_: Exception) {
            Color(0xFFEF4444)
        }
    }
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = color.copy(alpha = 0.1f),
        borderColor = color.copy(alpha = 0.2f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${tribe.rank}",
                    color = if (tribe.rank == 1) Color(0xFFEAB308) else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MappedIcon(
                    iosName = tribe.iconName ?: "shield.fill",
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
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = if (tribe.rank == 1) "The Official Tribe Leaderboard Leader" else "${tribe.memberCount} members",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = QuickSandFontFamily
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = tribe.totalPoints.toString(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "PTS",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onAddScore() },
                    color = Color.White,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Score",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier, cornerRadius = 24.dp, backgroundColor = Color.White.copy(0.05f)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}
