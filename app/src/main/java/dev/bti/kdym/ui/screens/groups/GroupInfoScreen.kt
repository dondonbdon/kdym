package dev.bti.kdym.ui.screens.groups

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.UserRole
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.MappedIcon
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.UserSelectionDialog
import dev.bti.kdym.ui.theme.KDYM_PALETTE
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.ui.theme.toColor
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun GroupInfoScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    groupsViewModel: GroupsViewModel,
    mainViewModel: MainViewModel,
    adminViewModel: AdminViewModel
) {
    val groups by groupsViewModel.groups.collectAsState()
    val group = remember(groupId, groups) { groups.find { it.id == groupId } }
    val user by mainViewModel.user.collectAsState()
    val allUsers by adminViewModel.allUsers.collectAsState()

    val isLeader = remember(
        group,
        user
    ) { group?.leaderIds?.contains(user?.uid) == true || user?.hasCommandAccess == true }

    var draftGroup by remember(group) { mutableStateOf(group) }
    var selectedTab by remember { mutableStateOf("INFO") }
    val scrollState = rememberScrollState()

    OutpourBackground {
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            Column(modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)) {
                GroupInfoHeader(group = draftGroup)
                Spacer(modifier = Modifier.height(24.dp))

                // iOS Animated Pill Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    InfoTabButton(
                        icon = Icons.Default.Info,
                        isSelected = selectedTab == "INFO"
                    ) { selectedTab = "INFO" }
                    Spacer(modifier = Modifier.width(8.dp))
                    InfoTabButton(
                        icon = Icons.Default.Groups,
                        isSelected = selectedTab == "MEMBERS"
                    ) { selectedTab = "MEMBERS" }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isLeader) {
                        InfoTabButton(
                            icon = Icons.Default.Tune,
                            isSelected = selectedTab == "SETTINGS"
                        ) { selectedTab = "SETTINGS" }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    InfoTabButton(
                        icon = Icons.Default.Image,
                        isSelected = selectedTab == "MEDIA"
                    ) { selectedTab = "MEDIA" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)) {
                    when (selectedTab) {
                        "INFO" -> GroupInfoTab(group = draftGroup, onUpdate = { draftGroup = it })
                        "MEMBERS" -> GroupMembersTab(
                            group = draftGroup,
                            allUsers = allUsers,
                            isLeader = isLeader,
                            adminViewModel = adminViewModel,
                            onUpdate = { draftGroup = it },
                            onNavigateToProfile = onNavigateToProfile
                        )

                        "SETTINGS" -> GroupSettingsTab(
                            group = draftGroup,
                            isLeader = isLeader,
                            onUpdate = { draftGroup = it })

                        "MEDIA" -> GroupMediaTab(
                            group = draftGroup,
                            groupsViewModel = groupsViewModel
                        )
                    }
                }
            }

            if (isLeader && selectedTab != "MEDIA" && selectedTab != "MEMBERS") {
                Box(modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()) {
                    Button(
                        onClick = { draftGroup?.let { adminViewModel.updateGroup(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SAVE GROUP",
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupInfoAndPermsScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    adminViewModel: AdminViewModel
) {
    val groups by adminViewModel.groups.collectAsState()
    val group = remember(groupId, groups) { groups.find { it.id == groupId } }
    var draftGroup by remember(group) { mutableStateOf(group) }
    val scrollState = rememberScrollState()

    OutpourBackground {
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "INFO & PERMS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                GroupInfoTab(group = draftGroup, onUpdate = { draftGroup = it })
                Spacer(modifier = Modifier.height(24.dp))
                GroupSettingsTab(
                    group = draftGroup,
                    isLeader = true,
                    onUpdate = { draftGroup = it })
            }

            Box(modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()) {
                Button(
                    onClick = { draftGroup?.let { adminViewModel.updateGroup(it); onNavigateBack() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAVE CHANGES",
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun GroupMembersAndLeadersScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    adminViewModel: AdminViewModel
) {
    val groups by adminViewModel.groups.collectAsState()
    val group = remember(groupId, groups) { groups.find { it.id == groupId } }
    val allUsers by adminViewModel.allUsers.collectAsState()
    var draftGroup by remember(group) { mutableStateOf(group) }
    val scrollState = rememberScrollState()

    OutpourBackground {
        Column(modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "MEMBERS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                GroupMembersTab(
                    group = draftGroup,
                    allUsers = allUsers,
                    isLeader = true, // Admins/Leaders can manage from here
                    adminViewModel = adminViewModel,
                    onUpdate = { draftGroup = it }
                )
            }

            Box(modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()) {
                Button(
                    onClick = { draftGroup?.let { adminViewModel.updateGroup(it); onNavigateBack() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAVE CHANGES",
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun GroupInfoHeader(group: AppGroup?) {
    val accentColor = group?.colorHex?.toColor() ?: Color(0xFFEF4444)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (group?.iconName != null) {
                MappedIcon(
                    iosName = group.iconName,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = group?.name ?: "Loading...",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${group?.type?.title?.uppercase() ?: "GENERAL"}  •  ${group?.memberCount ?: 0} MEMBERS",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            fontFamily = QuickSandFontFamily
        )
    }
}

@Composable
fun InfoTabButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 80.dp else 48.dp,
        label = "tab_width"
    )

    Surface(
        modifier = Modifier
            .width(width)
            .height(48.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.White.copy(0.05f),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White.copy(0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun GroupInfoTab(group: AppGroup?, onUpdate: (AppGroup) -> Unit) {
    val isTribe = group?.type == AppGroupType.tribe
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val accentColor = group?.colorHex?.toColor() ?: Color(0xFFEF4444)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Group Identity",
                    color = Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White.copy(0.02f),
                borderColor = Color.White.copy(0.05f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StyledTextField(
                        value = group?.name ?: "",
                        onValueChange = { if (!isTribe) group?.let { g -> onUpdate(g.copy(name = it)) } },
                        placeholder = "Group Name",
                        leadingIcon = Icons.Default.TextFields,
                        iconColor = Color(0xFF22D3EE),
                        enabled = !isTribe
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    StyledTextField(
                        value = group?.description ?: "",
                        onValueChange = { group?.let { g -> onUpdate(g.copy(description = it)) } },
                        placeholder = "Description",
                        leadingIcon = Icons.AutoMirrored.Filled.Notes,
                        iconColor = Color(0xFF22D3EE),
                        singleLine = false
                    )
                }
            }
        }

        Text(
            text = "AVATAR STYLE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            fontFamily = QuickSandFontFamily
        )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (group?.iconName != null) {
                            MappedIcon(
                                iosName = group.iconName,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Current look",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(text = group?.name ?: "Group", color = TextSecondary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Icon Picker Trigger
                Surface(
                    onClick = { if (!isTribe) showIconPicker = !showIconPicker },
                    color = Color.White.copy(0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Choose Icon",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Open the icon library",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = if (showIconPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }

                if (showIconPicker) {
                    val icons = listOf(
                        "bubble.left.and.bubble.right.fill",
                        "person.3.fill",
                        "megaphone.fill",
                        "building.2.fill",
                        "paintbrush.pointed.fill",
                        "camera.fill",
                        "photo.stack.fill",
                        "video.fill",
                        "music.mic",
                        "flame.fill",
                        "drop.fill",
                        "bolt.fill",
                        "sparkles",
                        "book.closed.fill",
                        "shield.fill",
                        "star.fill",
                        "crown.fill",
                        "heart.fill",
                        "flag.fill",
                        "trophy.fill"
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.heightIn(max = 200.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gridItems(icons) { iconName ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(
                                        if (group?.iconName == iconName) accentColor.copy(
                                            0.2f
                                        ) else Color.White.copy(0.05f), CircleShape
                                    )
                                    .border(
                                        if (group?.iconName == iconName) 2.dp else 0.dp,
                                        accentColor,
                                        CircleShape
                                    )
                                    .clickable { onUpdate(group!!.copy(iconName = iconName)) },
                                contentAlignment = Alignment.Center
                            ) {
                                MappedIcon(
                                    iosName = iconName,
                                    tint = if (group?.iconName == iconName) accentColor else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Color Picker Trigger
                Surface(
                    onClick = { if (!isTribe) showColorPicker = !showColorPicker },
                    color = Color.White.copy(0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Choose Color",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "This controls the glow and accent color",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = if (showColorPicker) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }

                if (showColorPicker) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        modifier = Modifier.heightIn(max = 120.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gridItems(KDYM_PALETTE) { hex ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(hex.toColor(), CircleShape)
                                    .border(
                                        if (group?.colorHex == hex) 3.dp else 0.dp,
                                        Color.White,
                                        CircleShape
                                    )
                                    .clickable { onUpdate(group!!.copy(colorHex = hex)) }
                            )
                        }
                    }
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BottomInfoRow(label = "MEMBERS", value = "${group?.memberCount ?: 0}")
                BottomInfoRow(label = "LEADERS", value = "${group?.leaderIds?.size ?: 0}")
                BottomInfoRow(label = "GROUP TYPE", value = group?.type?.title ?: "General")
                if (!isTribe) {
                    BottomInfoRow(
                        label = "ACCESS",
                        value = if (group?.isPublic == true) "Public" else "Private"
                    )
                    BottomInfoRow(
                        label = "OFFICIAL",
                        value = if (group?.isOfficial == true) "Yes" else "No"
                    )
                }
            }
        }
    }
}

@Composable
fun BottomInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = QuickSandFontFamily
        )
    }
}

@Composable
fun GroupMembersTab(
    group: AppGroup?,
    allUsers: List<AppUser>,
    isLeader: Boolean,
    adminViewModel: AdminViewModel,
    onUpdate: (AppGroup) -> Unit,
    onNavigateToProfile: (String) -> Unit = {}
) {
    val isTribe = group?.type == AppGroupType.tribe
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isSystemAdmin = allUsers.find { it.uid == currentUserId }?.roleEnum?.ordinal!! >= UserRole.groupLeader.ordinal

    var showUserPicker by remember { mutableStateOf(false) }
    var selectedUserIds by remember { mutableStateOf(setOf<String>()) }
    var isManageMode by remember { mutableStateOf(false) }
    var showConfirmRemove by remember { mutableStateOf(false) }

    val joinRequests by adminViewModel.getJoinRequestsForGroup(group?.id ?: "")
        .collectAsState(initial = emptyList())
    var selectedSubTab by remember { mutableStateOf("MEMBERS") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // REQUESTS SUB-TABS (Only visible to leaders if there are requests)
        if (isLeader && joinRequests.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MemberSubTabButton(
                    label = "MEMBERS",
                    isSelected = selectedSubTab == "MEMBERS"
                ) { selectedSubTab = "MEMBERS" }
                MemberSubTabButton(
                    label = "REQUESTS (${joinRequests.size})",
                    isSelected = selectedSubTab == "REQUESTS"
                ) { selectedSubTab = "REQUESTS" }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (selectedSubTab == "REQUESTS" && isLeader) {
            joinRequests.forEach { request ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = request.requesterName.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = request.requesterName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = request.requesterEmail,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        adminViewModel.updateJoinRequestStatus(
                                            request.id,
                                            "approved",
                                            currentUserId
                                        )
                                        group?.let { g -> onUpdate(g.copy(memberIds = (g.memberIds + request.requesterId).distinct())) }
                                    }
                                },
                                modifier = Modifier
                                    .background(
                                        Color(0xFF10B981).copy(0.1f),
                                        CircleShape
                                    )
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Approve",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        adminViewModel.updateJoinRequestStatus(
                                            request.id,
                                            "rejected",
                                            currentUserId
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .background(
                                        Color(0xFFEF4444).copy(0.1f),
                                        CircleShape
                                    )
                                    .size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Reject",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // MEMBERS VIEW
            if (showUserPicker) {
                UserSelectionDialog(
                    title = "ADD MEMBERS",
                    users = allUsers.filter {
                        it.uid !in (group?.memberIds ?: emptyList()) && it.uid !in (group?.leaderIds
                            ?: emptyList())
                    },
                    selectedUserIds = emptySet(),
                    multiSelect = true,
                    onDismiss = { showUserPicker = false },
                    onConfirmed = { ids ->
                        group?.let { onUpdate(it.copy(memberIds = (it.memberIds + ids).distinct())) }
                        showUserPicker = false
                    }
                )
            }

            if (showConfirmRemove) {
                AlertDialog(
                    onDismissRequest = { showConfirmRemove = false },
                    title = {
                        Text(
                            "Remove Members?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Text(
                            "Are you sure you want to remove ${selectedUserIds.size} members from the group?",
                            color = Color.White.copy(0.7f)
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            group?.let { g ->
                                val updatedGroup = g.copy(
                                    memberIds = g.memberIds - selectedUserIds,
                                    leaderIds = g.leaderIds - selectedUserIds
                                )
                                onUpdate(updatedGroup)
                                adminViewModel.updateGroup(updatedGroup)
                            }
                            selectedUserIds = emptySet()
                            isManageMode = false
                            showConfirmRemove = false
                        }) {
                            Text("REMOVE", color = Color(0xFFEF4444), fontWeight = FontWeight.Black)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmRemove = false }) {
                            Text("CANCEL", color = Color.White.copy(0.6f))
                        }
                    },
                    containerColor = Color(0xFF111111),
                    shape = RoundedCornerShape(24.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEMBERS",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )

                if (isLeader && (!isTribe || isSystemAdmin)) {
                    Row {
                        IconButton(onClick = { showUserPicker = true }) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Add",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = {
                            isManageMode = !isManageMode
                            if (!isManageMode) selectedUserIds = emptySet()
                        }) {
                            Icon(
                                imageVector = if (isManageMode) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = "Manage",
                                tint = if (isManageMode) Color.White else Color(0xFF22D3EE)
                            )
                        }
                    }
                }
            }

            if (isManageMode && selectedUserIds.isNotEmpty()) {
                Button(
                    onClick = { showConfirmRemove = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444).copy(
                            0.1f
                        ), contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFEF4444).copy(0.3f)
                    )
                ) {
                    Text("REMOVE SELECTED (${selectedUserIds.size})", fontWeight = FontWeight.Black)
                }
            }

            val members = remember(group, allUsers) {
                val ids = (group?.leaderIds ?: emptyList()) + (group?.memberIds ?: emptyList())
                allUsers.filter { it.uid in ids }
            }

            members.forEach { user ->
                val isCurrentUser = user.uid == currentUserId
                val isUserLeader = group?.leaderIds?.contains(user.uid) == true
                val isSelected = selectedUserIds.contains(user.uid)

                val canRemove = (!isTribe || isSystemAdmin) && !isCurrentUser

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            if (isManageMode && canRemove) {
                                selectedUserIds =
                                    if (isSelected) selectedUserIds - user.uid else selectedUserIds + user.uid
                            } else if (!isManageMode) {
                                onNavigateToProfile(user.uid)
                            }
                        },
                    cornerRadius = 16.dp,
                    backgroundColor = if (isSelected) Color(0xFF22D3EE).copy(0.1f) else Color.White.copy(
                        0.02f
                    ),
                    borderColor = if (isSelected) Color(0xFF22D3EE).copy(0.3f) else Color.White.copy(
                        0.05f
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isManageMode && canRemove) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    selectedUserIds =
                                        if (it) selectedUserIds + user.uid else selectedUserIds - user.uid
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF22D3EE),
                                    uncheckedColor = Color.White.copy(0.3f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(0.08f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.initials,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isCurrentUser) "${user.displayName} (You)" else user.displayName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = QuickSandFontFamily
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                color = if (isUserLeader) Color(0xFFEAB308).copy(0.15f) else Color.White.copy(
                                    0.05f
                                ),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isUserLeader) Color(0xFFEAB308).copy(0.3f) else Color.White.copy(
                                        0.1f
                                    )
                                )
                            ) {
                                Text(
                                    text = if (isUserLeader) "LEADER" else "MEMBER",
                                    color = if (isUserLeader) Color(0xFFEAB308) else TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // ... inside GroupMembersTab, locating the Action Row ...

                        if (!isManageMode && isLeader && (!isTribe || isSystemAdmin)) {
                            Row {
                                if (!isCurrentUser) {
                                    if (!isUserLeader) {
                                        IconButton(onClick = {
                                            group?.let { g ->
                                                // 1. Create the new group state
                                                val updatedGroup = g.copy(
                                                    memberIds = g.memberIds - user.uid,
                                                    leaderIds = (g.leaderIds + user.uid).distinct()
                                                )
                                                // 2. Update local UI state
                                                onUpdate(updatedGroup)
                                                // 3. IMMEDIATELY update Firestore
                                                adminViewModel.updateGroup(updatedGroup)
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = "Make Leader",
                                                tint = Color.White.copy(0.3f)
                                            )
                                        }
                                    } else {
                                        IconButton(onClick = {
                                            group.let { g ->
                                                // 1. Create the new group state
                                                val updatedGroup = g.copy(
                                                    memberIds = (g.memberIds + user.uid).distinct(),
                                                    leaderIds = g.leaderIds - user.uid
                                                )
                                                // 2. Update local UI state
                                                onUpdate(updatedGroup)
                                                // 3. IMMEDIATELY update Firestore
                                                adminViewModel.updateGroup(updatedGroup)
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Demote to Member",
                                                tint = Color(0xFFEAB308)
                                            )
                                        }
                                    }
                                }

                                if (canRemove) { // Hide X for current user & tribes
                                    IconButton(onClick = {
                                        selectedUserIds = setOf(user.uid)
                                        showConfirmRemove = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color(0xFFEF4444).copy(0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // LEAVE GROUP LOGIC
            val isCurrentUserLeader = group?.leaderIds?.contains(currentUserId) == true
            val otherLeadersCount = group?.leaderIds?.count { it != currentUserId } ?: 0
            val isMemberOfGroup = members.any { it.uid == currentUserId }

            // Rules for leaving: Must be > 1 person. If leader, someone else must also be a leader.
            val canLeaveGroup = members.size > 1 && (!isCurrentUserLeader || otherLeadersCount > 0)

            if (isMemberOfGroup && canLeaveGroup && group?.type != AppGroupType.tribe) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        group?.let { g ->
                            onUpdate(
                                g.copy(
                                    memberIds = g.memberIds - currentUserId,
                                    leaderIds = g.leaderIds - currentUserId
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.05f),
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "LEAVE GROUP",
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
            } else if (isMemberOfGroup && members.size > 1) {
                // User wants to leave but is the only leader
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "You must promote another leader before you can leave.",
                    color = Color(0xFFEAB308),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun MemberSubTabButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White.copy(0.15f) else Color.Transparent,
        shape = RoundedCornerShape(18.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(0.2f)
        ) else null
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) Color.White else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}

@Composable
fun GroupSettingsTab(group: AppGroup?, isLeader: Boolean, onUpdate: (AppGroup) -> Unit) {
    val isTribe = group?.type == AppGroupType.tribe
    val canEdit = isLeader

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isTribe) {
            PermissionToggle(
                "Public Group",
                "Users can discover this group and request access.",
                group?.isPublic == true,
                canEdit
            ) {
                group?.let { g -> onUpdate(g.copy(isPublic = it)) }
            }
            PermissionToggle(
                "Official Group",
                "Marks this as an official KDYM channel.",
                group?.isOfficial == true,
                canEdit
            ) {
                group?.let { g -> onUpdate(g.copy(isOfficial = it)) }
            }
        }
        PermissionToggle(
            "Chat Enabled",
            "Allow messages inside this group.",
            group?.chatEnabled == true,
            canEdit
        ) {
            group?.let { g -> onUpdate(g.copy(chatEnabled = it)) }
        }
        PermissionToggle(
            "Leaders Post Only",
            "Members can read, but only leaders and admins can post.",
            group?.postingRestrictedToLeaders == true,
            canEdit
        ) {
            group?.let { g -> onUpdate(g.copy(postingRestrictedToLeaders = it)) }
        }
        PermissionToggle(
            "Leaders Attach Only",
            "Only leaders and admins can attach photos and files.",
            group?.attachmentsRestrictedToLeaders == true,
            canEdit
        ) {
            group?.let { g -> onUpdate(g.copy(attachmentsRestrictedToLeaders = it)) }
        }
        PermissionToggle(
            "Leaders Poll Only",
            "Only leaders and admins can create polls.",
            group?.pollsRestrictedToLeaders == true,
            canEdit
        ) {
            group?.let { g -> onUpdate(g.copy(pollsRestrictedToLeaders = it)) }
        }
    }
}

@Composable
fun PermissionToggle(
    title: String,
    subtitle: String,
    enabled: Boolean,
    canEdit: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(0.05f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (title.contains("Public")) Icons.Default.Visibility else if (title.contains(
                        "Official"
                    )
                ) Icons.Default.Verified else Icons.Default.Forum,
                contentDescription = null,
                tint = Color.White.copy(0.6f)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSandFontFamily
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = QuickSandFontFamily
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onCheckedChange,
            enabled = canEdit,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF22D3EE),
                uncheckedThumbColor = Color.White.copy(0.6f),
                uncheckedTrackColor = Color.White.copy(0.1f)
            )
        )
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    iconColor: Color,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.White.copy(0.3f),
                fontFamily = QuickSandFontFamily,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.03f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontFamily = QuickSandFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
fun GroupMediaTab(group: AppGroup?, groupsViewModel: GroupsViewModel) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Images", "Videos", "Docs", "Audio")

    val media by groupsViewModel.getGroupMedia(group?.id ?: "")
        .collectAsState(initial = emptyList())
    var showFullScreenGallery by remember { mutableStateOf(false) }
    var initialMediaIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GALLERY",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Images, videos, documents, and audio shared inside this group.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily
                )
            }

            IconButton(onClick = {
                if (media.isNotEmpty()) {
                    initialMediaIndex = 0; showFullScreenGallery = true
                }
            }) {
                Icon(
                    imageVector = Icons.Default.OpenInFull,
                    contentDescription = "Expand",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal filter bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    color = if (isSelected) Color.White else Color.White.copy(0.05f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (filter) {
                                "Images" -> Icons.Default.Image; "Videos" -> Icons.Default.PlayCircle; "Docs" -> Icons.Default.Description; else -> Icons.Default.GridView
                            },
                            contentDescription = null,
                            tint = if (isSelected) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = filter,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (media.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No media shared in this group yet.", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            val filteredMedia = remember(media, selectedFilter) {
                when (selectedFilter) {
                    "Images" -> media.filter { it.type.name.contains("image", ignoreCase = true) }
                    "Videos" -> media.filter { it.type.name.contains("video", ignoreCase = true) }
                    "Docs" -> media.filter { it.type.name.contains("file", ignoreCase = true) }
                    else -> media
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 1000.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                gridItems(filteredMedia) { attachment ->
                    val index = media.indexOf(attachment)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(0.05f))
                            .clickable { initialMediaIndex = index; showFullScreenGallery = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (attachment.type.name.contains(
                                "image",
                                ignoreCase = true
                            ) || attachment.type.name.contains("video", ignoreCase = true)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(attachment.thumbnailURL ?: attachment.url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = attachment.fileName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (attachment.type.name.contains("video", ignoreCase = true)) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        } else {
                            // File / Audio Placeholder
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = attachment.fileName?.substringAfterLast(".")?.uppercase()
                                        ?: "FILE",
                                    color = Color(0xFF22D3EE),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = QuickSandFontFamily
                                )
                                Icon(
                                    imageVector = if (attachment.type.name.contains(
                                            "audio",
                                            ignoreCase = true
                                        )
                                    ) Icons.Default.Audiotrack else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White.copy(0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                                attachment.sizeBytes?.let {
                                    Text(
                                        text = "${it / 1024} KB",
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showFullScreenGallery) {
            MediaGalleryOverlay(
                attachments = media,
                initialIndex = initialMediaIndex,
                onDismiss = { showFullScreenGallery = false }
            )
        }
    }
}
