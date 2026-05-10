package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun ManageTribePeopleScreen(
    tribeId: String,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    val tribes by viewModel.tribes.collectAsState()
    val tribe = remember(tribeId, tribes) { tribes.find { it.id == tribeId } }
    val allUsers by viewModel.allUsers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("ALL") }

    val filteredUsers = allUsers.filter { user ->
        val matchesSearch = user.displayName.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            "IN TRIBE" -> user.uid in (tribe?.memberIds ?: emptyList()) || user.uid in (tribe?.leaderIds ?: emptyList())
            "NOT IN TRIBE" -> user.uid !in (tribe?.memberIds ?: emptyList()) && user.uid !in (tribe?.leaderIds ?: emptyList())
            "LEADERS" -> user.uid in (tribe?.leaderIds ?: emptyList())
            else -> true
        }
        matchesSearch && matchesTab
    }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Custom Header for Modal Style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "MANAGE",
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = tribe?.name?.uppercase() ?: "TRIBE",
                        color = Color(0xFFEF4444),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Admins can assign leaders and members.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = RubikFontFamily
                    )
                }
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            // Search and Filters
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                CommandInputField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search users",
                    icon = Icons.Default.Search
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterTab("ALL", selectedTab == "ALL") { selectedTab = "ALL" }
                    FilterTab("IN TRIBE", selectedTab == "IN TRIBE") { selectedTab = "IN TRIBE" }
                    FilterTab("NOT IN TRIBE", selectedTab == "NOT IN TRIBE") { selectedTab = "NOT IN TRIBE" }
                    FilterTab("LEADERS", selectedTab == "LEADERS") { selectedTab = "LEADERS" }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Users List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filteredUsers) { user ->
                    ManageUserCard(
                        user = user,
                        isMember = user.uid in (tribe?.memberIds ?: emptyList()),
                        isLeader = user.uid in (tribe?.leaderIds ?: emptyList()),
                        onAddMember = {
                            tribe?.let { t ->
                                val newMembers = t.memberIds.toMutableList()
                                if (user.uid !in newMembers) newMembers.add(user.uid)
                                val newLeaders = t.leaderIds.toMutableList()
                                newLeaders.remove(user.uid) // Remove from leaders if adding as member
                                viewModel.updateTribe(t.copy(memberIds = newMembers, leaderIds = newLeaders))
                            }
                        },
                        onMakeLeader = {
                            tribe?.let { t ->
                                val newLeaders = t.leaderIds.toMutableList()
                                if (user.uid !in newLeaders) newLeaders.add(user.uid)
                                val newMembers = t.memberIds.toMutableList()
                                newMembers.remove(user.uid) // Remove from members if adding as leader
                                viewModel.updateTribe(t.copy(leaderIds = newLeaders, memberIds = newMembers))
                            }
                        },
                        onRemove = {
                            tribe?.let { t ->
                                val newMembers = t.memberIds.toMutableList()
                                newMembers.remove(user.uid)
                                val newLeaders = t.leaderIds.toMutableList()
                                newLeaders.remove(user.uid)
                                viewModel.updateTribe(t.copy(memberIds = newMembers, leaderIds = newLeaders))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.White.copy(0.1f),
        shape = CircleShape
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            fontFamily = RubikFontFamily
        )
    }
}

@Composable
fun ManageUserCard(
    user: AppUser,
    isMember: Boolean,
    isLeader: Boolean,
    onAddMember: () -> Unit,
    onMakeLeader: () -> Unit,
    onRemove: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.White.copy(0.05f)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).background(Color.White.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                    Text(text = user.initials, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
                    Text(text = user.email, color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
                }
                if (isLeader) {
                    Surface(color = Color(0xFF67E8F9).copy(0.1f), shape = RoundedCornerShape(12.dp)) {
                        Text(text = "LEADER", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFF67E8F9), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                } else if (isMember) {
                    Surface(color = Color(0xFFEF4444).copy(0.1f), shape = RoundedCornerShape(12.dp)) {
                        Text(text = "MEMBER", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!isMember && !isLeader) {
                    Button(
                        onClick = onAddMember,
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF67E8F9), contentColor = Color.Black),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text(text = "ADD MEMBER", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                    }
                    Button(
                        onClick = onMakeLeader,
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text(text = "MAKE LEADER", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                    }
                } else {
                    if (isMember) {
                        Button(
                            onClick = onMakeLeader,
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Text(text = "UPGRADE TO LEADER", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                        }
                    } else if (isLeader) {
                        Button(
                            onClick = onAddMember,
                            modifier = Modifier.weight(1f).height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Text(text = "DEMOTE TO MEMBER", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                        }
                    }
                    Button(
                        onClick = onRemove,
                        modifier = Modifier.width(100.dp).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.1f), contentColor = Color.Red),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text(text = "REMOVE", fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                    }
                }
            }
        }
    }
}
