package dev.bti.kdym.ui.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.GroupsViewModel

@Composable
fun ExploreGroupsScreen(
    onNavigateBack: () -> Unit,
    viewModel: GroupsViewModel
) {
    val allGroups by viewModel.groups.collectAsState()
    val userRequests by viewModel.userRequests.collectAsState()
    
    val pendingGroupIds = remember(userRequests) { 
        userRequests.filter { it.status == "pending" }.map { it.groupId }.toSet() 
    }
    
    val publicGroups = remember(allGroups, pendingGroupIds) { 
        val currentUserId = viewModel.currentUserId
        allGroups.filter { 
            it.isPublic && 
            it.type != AppGroupType.tribe && 
            it.id !in pendingGroupIds &&
            currentUserId !in (it.memberIds + it.leaderIds)
        } 
    }
    
    val requestedGroups = remember(allGroups, userRequests) {
        val pendingIds = userRequests.filter { it.status == "pending" }.map { it.groupId }
        allGroups.filter { it.id in pendingIds }
    }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // New Back Button Style
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

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "EXPLORE",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "JOIN GROUPS",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (requestedGroups.isNotEmpty()) {
                    item {
                        Text(
                            text = "PENDING REQUESTS",
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                    items(requestedGroups, key = { "pending_${it.id}" }) { group ->
                        ExploreGroupCard(
                            group = group,
                            isRequested = true,
                            onRequestJoin = {}
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                item {
                    Text(
                        text = "AVAILABLE GROUPS",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
                
                if (publicGroups.isEmpty()) {
                    item {
                        Text(
                            text = "No new groups available to join at this time.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(publicGroups, key = { it.id }) { group ->
                        ExploreGroupCard(
                            group = group,
                            onRequestJoin = { viewModel.requestJoinGroup(group) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreGroupCard(
    group: AppGroup,
    isRequested: Boolean = false,
    onRequestJoin: () -> Unit
) {
    var requestedLocal by remember { mutableStateOf(false) }
    val currentlyRequested = isRequested || requestedLocal

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.White.copy(0.05f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = Color(0xFF22D3EE))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = group.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        maxLines = 1
                    )
                    Text(
                        text = group.description ?: "",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    onRequestJoin()
                    requestedLocal = true
                },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentlyRequested) Color.White.copy(0.1f) else Color.White, 
                    contentColor = if (currentlyRequested) Color.White.copy(0.3f) else Color.Black
                ),
                shape = RoundedCornerShape(22.dp),
                enabled = !currentlyRequested
            ) {
                Text(
                    text = if (currentlyRequested) "REQUESTED" else "REQUEST TO JOIN",
                    fontWeight = FontWeight.Black, 
                    fontSize = 12.sp, 
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}
