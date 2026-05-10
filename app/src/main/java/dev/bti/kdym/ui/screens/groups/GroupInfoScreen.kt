package dev.bti.kdym.ui.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.UserSelectionDialog
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun GroupInfoScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onEditGroup: (String) -> Unit,
    groupsViewModel: GroupsViewModel,
    mainViewModel: MainViewModel,
    adminViewModel: AdminViewModel
) {
    val groups by groupsViewModel.groups.collectAsState()
    val group = remember(groupId, groups) { groups.find { it.id == groupId } }
    val user by mainViewModel.user.collectAsState()
    val allUsers by adminViewModel.allUsers.collectAsState()
    
    val isLeader = remember(group, user) { group?.leaderIds?.contains(user?.uid) == true || user?.hasCommandAccess == true }
    
    var draftGroup by remember(group) { mutableStateOf(group) }
    var selectedTab by remember { mutableStateOf("INFO") }
    val scrollState = rememberScrollState()

    var showSaveAck by remember { mutableStateOf(false) }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header with close button
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.TopEnd).background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Group Info Header (Avatar and Title)
                GroupInfoHeader(group = draftGroup)

                Spacer(modifier = Modifier.height(24.dp))

                // Custom Tab Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoTabButton(icon = Icons.Default.Info, isSelected = selectedTab == "INFO") { selectedTab = "INFO" }
                    InfoTabButton(icon = Icons.Default.Groups, isSelected = selectedTab == "MEMBERS") { selectedTab = "MEMBERS" }
                    InfoTabButton(icon = Icons.Default.Tune, isSelected = selectedTab == "SETTINGS") { selectedTab = "SETTINGS" }
                    InfoTabButton(icon = Icons.Default.Image, isSelected = selectedTab == "MEDIA") { selectedTab = "MEDIA" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tab Content
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    when (selectedTab) {
                        "INFO" -> GroupInfoTab(
                            group = draftGroup,
                            onUpdate = { draftGroup = it }
                        )
                        "MEMBERS" -> GroupMembersTab(
                            group = draftGroup,
                            allUsers = allUsers,
                            onUpdate = { draftGroup = it }
                        )
                        "SETTINGS" -> GroupSettingsTab(
                            group = draftGroup,
                            isLeader = isLeader,
                            onUpdate = { draftGroup = it }
                        )
                        "MEDIA" -> GroupMediaTab()
                    }
                }
            }
            
            // Save Button for Admins
            if (isLeader && selectedTab != "MEDIA" && selectedTab != "MEMBERS") {
                Box(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                    Button(
                        onClick = { 
                            draftGroup?.let { 
                                adminViewModel.updateGroup(it)
                                showSaveAck = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedTab == "SETTINGS") "SAVE PERMISSIONS" else "SAVE GROUP INFO",
                            fontWeight = FontWeight.Black,
                            fontFamily = RubikFontFamily
                        )
                    }
                }
            }
        }
        
        if (showSaveAck) {
            AlertDialog(
                onDismissRequest = { showSaveAck = false },
                confirmButton = {
                    TextButton(onClick = { showSaveAck = false }) {
                        Text("OK", color = Color(0xFF22D3EE))
                    }
                },
                title = { Text("Success", color = Color.White) },
                text = { Text("Group information has been updated successfully.", color = Color.White.copy(0.7f)) },
                containerColor = Color(0xFF111111),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun GroupInfoHeader(group: AppGroup?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    group?.colorHex?.let { try { Color(it.toColorInt()) } catch(e:Exception) { Color(0xFFEF4444) } } ?: Color(0xFFEF4444),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = group?.name ?: "Loading...",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontFamily = RubikFontFamily,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "${group?.type?.title?.uppercase() ?: "GENERAL"}  •  ${group?.memberCount ?: 0} MEMBERS",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            fontFamily = RubikFontFamily
        )
    }
}

@Composable
fun InfoTabButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.White.copy(0.05f),
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White.copy(0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun GroupInfoTab(group: AppGroup?, onUpdate: (AppGroup) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text(
                text = "GROUP IDENTITY",
                color = Color(0xFFEF4444),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = group?.name ?: "",
                        onValueChange = { group?.let { g -> onUpdate(g.copy(name = it)) } },
                        placeholder = { Text("Group Name", color = TextSecondary) },
                        leadingIcon = { Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = Color(0xFF22D3EE)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.White.copy(0.1f))
                    TextField(
                        value = group?.description ?: "",
                        onValueChange = { group?.let { g -> onUpdate(g.copy(description = it)) } },
                        placeholder = { Text("Description", color = TextSecondary) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Notes, contentDescription = null, tint = Color(0xFF22D3EE)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                }
            }
        }

        Column {
            Text(
                text = "AVATAR",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            val row1 = listOf(Icons.Default.Forum, Icons.Default.Groups, Icons.Default.Campaign, Icons.Default.Brush, Icons.Default.CameraAlt, Icons.Default.Bolt)
            val row2 = listOf(Icons.Default.Celebration, Icons.Default.Church, Icons.Default.VolunteerActivism, Icons.Default.MusicNote, Icons.Default.SportsBasketball, Icons.Default.Add)
            
            AvatarRow(row1) { /* Update Icon */ }
            Spacer(modifier = Modifier.height(12.dp))
            AvatarRow(row2) { /* Update Icon */ }
        }
        
        Column {
            Text(
                text = "COLOR",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            val colors = listOf(Color(0xFF22D3EE), Color(0xFFEF4444), Color(0xFFEAB308), Color(0xFF10B981), Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFFF97316), Color(0xFF84CC16))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(colors) { color ->
                    val colorHex = String.format("#%06X", 0xFFFFFF and color.value.toInt())
                    val isSelected = group?.colorHex?.uppercase() == colorHex.uppercase()
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(color, CircleShape)
                            .border(if (isSelected) 3.dp else 0.dp, Color.White, CircleShape)
                            .clickable { 
                                group?.let { g -> onUpdate(g.copy(colorHex = colorHex)) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BottomInfoRow(label = "MEMBERS", value = "${group?.memberCount ?: 0}")
                BottomInfoRow(label = "LEADERS", value = "${group?.leaderIds?.size ?: 0}")
                BottomInfoRow(label = "GROUP TYPE", value = group?.type?.title ?: "General")
                BottomInfoRow(label = "ACCESS", value = if (group?.isPublic == true) "Public" else "Private")
                BottomInfoRow(label = "OFFICIAL", value = if (group?.isOfficial == true) "Yes" else "No")
            }
        }
    }
}

@Composable
fun AvatarRow(icons: List<ImageVector>, onSelect: (ImageVector) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(icons) { icon ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(0.1f), CircleShape)
                    .clickable { onSelect(icon) },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun BottomInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily, letterSpacing = 1.sp)
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
    }
}

@Composable
fun GroupMembersTab(group: AppGroup?, allUsers: List<AppUser>, onUpdate: (AppGroup) -> Unit) {
    var showUserPicker by remember { mutableStateOf(false) }
    var selectedUserIds by remember { mutableStateOf(setOf<String>()) }
    var isManageMode by remember { mutableStateOf(false) }
    var showConfirmRemove by remember { mutableStateOf(false) }

    if (showUserPicker) {
        UserSelectionDialog(
            title = "ADD MEMBERS",
            users = allUsers.filter { it.uid !in (group?.memberIds ?: emptyList()) && it.uid !in (group?.leaderIds ?: emptyList()) },
            selectedUserIds = emptySet(),
            multiSelect = true,
            onDismiss = { showUserPicker = false },
            onConfirmed = { ids ->
                group?.let { onUpdate(it.copy(memberIds = it.memberIds + ids)) }
                showUserPicker = false
            }
        )
    }

    if (showConfirmRemove) {
        AlertDialog(
            onDismissRequest = { showConfirmRemove = false },
            title = { Text("Remove Members?", color = Color.White) },
            text = { Text("Are you sure you want to remove ${selectedUserIds.size} members from the group?", color = Color.White.copy(0.7f)) },
            confirmButton = {
                TextButton(onClick = { 
                    group?.let { g ->
                        onUpdate(g.copy(
                            memberIds = g.memberIds - selectedUserIds,
                            leaderIds = g.leaderIds - selectedUserIds
                        ))
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

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showUserPicker = true },
                modifier = Modifier.height(48.dp).weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f), contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ADD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            TextButton(
                onClick = { 
                    isManageMode = !isManageMode
                    if (!isManageMode) selectedUserIds = emptySet()
                },
                modifier = Modifier.height(48.dp)
            ) {
                Text(if (isManageMode) "CANCEL" else "MANAGE", color = Color(0xFF22D3EE), fontWeight = FontWeight.Bold)
            }
        }

        if (isManageMode && selectedUserIds.isNotEmpty()) {
            Button(
                onClick = { showConfirmRemove = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(0.1f), contentColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(0.3f))
            ) {
                Text("REMOVE SELECTED (${selectedUserIds.size})", fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val members = remember(group, allUsers) {
            val ids = (group?.leaderIds ?: emptyList()) + (group?.memberIds ?: emptyList())
            allUsers.filter { it.uid in ids }
        }

        members.forEach { user ->
            val isLeader = group?.leaderIds?.contains(user.uid) == true
            val isSelected = selectedUserIds.contains(user.uid)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = isManageMode) {
                        selectedUserIds = if (isSelected) selectedUserIds - user.uid else selectedUserIds + user.uid
                    }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isManageMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { 
                            selectedUserIds = if (it) selectedUserIds + user.uid else selectedUserIds - user.uid
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF22D3EE), uncheckedColor = Color.White.copy(0.3f))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = user.initials, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = RubikFontFamily)
                    Text(text = if (isLeader) "Leader" else "Member", color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
                }
                
                if (!isManageMode) {
                    IconButton(onClick = { 
                        selectedUserIds = setOf(user.uid)
                        showConfirmRemove = true
                    }) {
                        Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "Remove", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
fun GroupSettingsTab(group: AppGroup?, isLeader: Boolean, onUpdate: (AppGroup) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PermissionToggle("Public Group", "Users can discover this group and request access.", group?.isPublic == true, isLeader) {
            group?.let { g -> onUpdate(g.copy(isPublic = it)) }
        }
        PermissionToggle("Official Group", "Marks this as an official KDYM channel.", group?.isOfficial == true, isLeader) {
            group?.let { g -> onUpdate(g.copy(isOfficial = it)) }
        }
        PermissionToggle("Chat Enabled", "Allow messages inside this group.", group?.chatEnabled == true, isLeader) {
            group?.let { g -> onUpdate(g.copy(chatEnabled = it)) }
        }
        PermissionToggle("Leaders Post Only", "Members can read, but only leaders and admins can post.", group?.postingRestrictedToLeaders == true, isLeader) {
            group?.let { g -> onUpdate(g.copy(postingRestrictedToLeaders = it)) }
        }
        PermissionToggle("Leaders Attach Only", "Only leaders and admins can attach photos and files.", group?.attachmentsRestrictedToLeaders == true, isLeader) {
            group?.let { g -> onUpdate(g.copy(attachmentsRestrictedToLeaders = it)) }
        }
        PermissionToggle("Leaders Poll Only", "Only leaders and admins can create polls.", group?.pollsRestrictedToLeaders == true, isLeader) {
            group?.let { g -> onUpdate(g.copy(pollsRestrictedToLeaders = it)) }
        }
    }
}

@Composable
fun PermissionToggle(title: String, subtitle: String, enabled: Boolean, canEdit: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(Color.White.copy(0.05f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (title.contains("Public")) Icons.Default.Visibility else if (title.contains("Official")) Icons.Default.Verified else Icons.Default.Forum,
                contentDescription = null,
                tint = Color.White.copy(0.6f)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
            Text(text = subtitle, color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
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
fun GroupMediaTab() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Media Gallery", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Shared photos and files will be organized here for easy access.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = RubikFontFamily
                )
            }
        }
    }
}
