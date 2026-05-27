package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun CreateGroupScreen(
    groupId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val group = remember(groupId, groups) { groups.find { it.id == groupId } }

    var name by remember(group) { mutableStateOf(group?.name ?: "") }
    var description by remember(group) { mutableStateOf(group?.description ?: "") }
    var groupType by remember(group) { mutableStateOf(group?.type ?: AppGroupType.general) }
    
    var isPublic by remember(group) { mutableStateOf(group?.isPublic ?: false) }
    var isOfficial by remember(group) { mutableStateOf(group?.isOfficial ?: true) }
    var chatEnabled by remember(group) { mutableStateOf(group?.chatEnabled ?: true) }
    
    var leaderPostOnly by remember(group) { mutableStateOf(group?.postingRestrictedToLeaders ?: false) }
    var leaderAttachOnly by remember(group) { mutableStateOf(group?.attachmentsRestrictedToLeaders ?: false) }
    var leaderPollsOnly by remember(group) { mutableStateOf(group?.pollsRestrictedToLeaders ?: true) }
    var leaderScheduleOnly by remember(group) { mutableStateOf(group?.schedulesRestrictedToLeaders ?: true) }

    val allUsers by viewModel.allUsers.collectAsState()
    var selectedMemberIds by remember(group) { mutableStateOf(group?.memberIds?.toSet() ?: emptySet()) }
    var selectedLeaderIds by remember(group) { mutableStateOf(group?.leaderIds?.toSet() ?: emptySet()) }
    
    var showUserPicker by remember { mutableStateOf(false) }

    if (showUserPicker) {
        UserSelectionDialog(
            title = "ADD PEOPLE",
            users = allUsers,
            selectedUserIds = selectedMemberIds + selectedLeaderIds,
            onDismiss = { showUserPicker = false },
            onConfirmed = { ids ->
                // Add new ids to members by default, keeping existing leaders
                val newIds = ids - (selectedMemberIds + selectedLeaderIds)
                selectedMemberIds = selectedMemberIds + newIds
                // Remove ids that were unchecked
                selectedMemberIds = selectedMemberIds.intersect(ids)
                selectedLeaderIds = selectedLeaderIds.intersect(ids)
                showUserPicker = false
            }
        )
    }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.ChatBubble,
                title = if (groupId == null) "CREATE" else "EDIT",
                subtitle = "Group Chat"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CommandInputField(value = name, onValueChange = { name = it }, placeholder = "Group Name", icon = Icons.Default.ChatBubble)
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandInputField(value = description, onValueChange = { description = it }, placeholder = "Description", icon = Icons.Default.Description)

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "GROUP TYPE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        color = Color.White.copy(0.05f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = groupType.title, color = Color.White, modifier = Modifier.weight(1f), fontFamily = QuickSandFontFamily)
                            Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ToggleRow("Public Group", "Users can discover this group and request access.", isPublic) { isPublic = it }
                    ToggleRow("Official Group", "Marks this as an official KDYM channel.", isOfficial) { isOfficial = it }
                    ToggleRow("Chat Enabled", "Allow messages inside this group.", chatEnabled) { chatEnabled = it }
                    ToggleRow("Leaders Post Only", "Members can read, but only leaders and admins can post.", leaderPostOnly) { leaderPostOnly = it }
                    ToggleRow("Leaders Attach Only", "Only leaders and admins can attach photos and files.", leaderAttachOnly) { leaderAttachOnly = it }
                    ToggleRow("Leaders Poll Only", "Only leaders and admins can create polls.", leaderPollsOnly) { leaderPollsOnly = it }
                    ToggleRow("Leaders Schedule Only", "Only leaders and admins can create schedule items.", leaderScheduleOnly) { leaderScheduleOnly = it }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "PEOPLE", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                            Text(text = "ASSIGN ROLES", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                        }
                        IconButton(
                            onClick = { showUserPicker = true },
                            modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedMemberIds.isEmpty() && selectedLeaderIds.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            color = Color.White.copy(0.05f),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "No members selected", color = TextSecondary)
                            }
                        }
                    } else {
                        (selectedLeaderIds + selectedMemberIds).forEach { uid ->
                            val user = allUsers.find { it.uid == uid }
                            val isLeader = selectedLeaderIds.contains(uid)
                            user?.let {
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    color = Color.White.copy(0.05f),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(40.dp).background(Color(0xFFEF4444).copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                            Text(text = it.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = it.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = if (isLeader) "Leader" else "Member", color = if (isLeader) Color(0xFF67E8F9) else TextSecondary, fontSize = 12.sp)
                                        }
                                        TextButton(onClick = {
                                            if (isLeader) {
                                                selectedLeaderIds = selectedLeaderIds - uid
                                                selectedMemberIds = selectedMemberIds + uid
                                            } else {
                                                selectedMemberIds = selectedMemberIds - uid
                                                selectedLeaderIds = selectedLeaderIds + uid
                                            }
                                        }) {
                                            Text(
                                                text = if (isLeader) "MAKE MEMBER" else "MAKE LEADER",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            val newGroup = AppGroup(
                                id = groupId ?: "",
                                name = name,
                                description = description,
                                type = groupType,
                                isPublic = isPublic,
                                isOfficial = isOfficial,
                                chatEnabled = chatEnabled,
                                postingRestrictedToLeaders = leaderPostOnly,
                                attachmentsRestrictedToLeaders = leaderAttachOnly,
                                pollsRestrictedToLeaders = leaderPollsOnly,
                                schedulesRestrictedToLeaders = leaderScheduleOnly,
                                memberIds = selectedMemberIds.toList(),
                                leaderIds = selectedLeaderIds.toList(),
                                createdAt = group?.createdAt ?: Timestamp.now(),
                                updatedAt = Timestamp.now()
                            )
                            if (groupId != null) {
                                viewModel.updateGroup(newGroup)
                            } else {
                                viewModel.createGroup(newGroup)
                            }
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(28.dp),
                        enabled = name.isNotBlank()
                    ) {
                        Text(text = if (groupId == null) "CREATE GROUP" else "SAVE PERMISSIONS", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
            Text(text = subtitle, color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFEF4444)
            )
        )
    }
}
