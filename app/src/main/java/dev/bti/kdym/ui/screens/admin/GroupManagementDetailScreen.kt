package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun GroupManagementDetailScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onInfoAndPerms: (String) -> Unit,
    onMembersAndLeaders: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val group = remember(groupId, groups) { groups.find { it.id == groupId } }

    var showArchiveDialog by remember { mutableStateOf(false) }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
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
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "BACK", fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                if (group == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    GroupOverviewCard(group)

                    Spacer(modifier = Modifier.height(24.dp))

                    ManagementActionCard(
                        icon = Icons.Default.Tune,
                        title = "Info & Permissions",
                        subtitle = "Edit group identity, leaders, members, posting rules, attachments, polls, and schedule permissions.",
                        onClick = { onInfoAndPerms(groupId) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ManagementActionCard(
                        icon = Icons.Default.Forum,
                        title = "Open Chat",
                        subtitle = "Enter the group as an admin and inspect live conversation behavior.",
                        onClick = { onOpenChat(groupId) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ManagementActionCard(
                        icon = Icons.Default.Archive,
                        iconColor = Color(0xFFEAB308),
                        title = "Archive Group",
                        subtitle = "Remove this group from active lists while preserving its data.",
                        onClick = { showArchiveDialog = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ManagementActionCard(
                        icon = Icons.Default.PersonAdd,
                        iconColor = Color(0xFF10B981),
                        title = "Members & Leaders",
                        subtitle = "Use Info & Permissions -> People to promote leaders or remove members.",
                        onClick = { onMembersAndLeaders(groupId) }
                    )

                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }

        if (showArchiveDialog && group != null) {
            AlertDialog(
                onDismissRequest = { showArchiveDialog = false },
                title = { Text("Archive Group?", color = Color.White) },
                text = { Text("Are you sure you want to archive ${group.name}?", color = Color.White.copy(0.7f)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateGroup(group.copy(isActive = false))
                        showArchiveDialog = false
                        onNavigateBack()
                    }) {
                        Text("ARCHIVE", color = Color(0xFFEAB308), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showArchiveDialog = false }) {
                        Text("CANCEL", color = Color.White.copy(0.6f))
                    }
                },
                containerColor = Color(0xFF111111),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun GroupOverviewCard(group: AppGroup) {
    val typeColor = when (group.type) {
        AppGroupType.tribe -> Color(0xFF22D3EE)
        AppGroupType.leadership -> Color(0xFFFBBF24)
        else -> Color(0xFFEF4444)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(typeColor.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (group.type) {
                        AppGroupType.tribe -> Icons.Default.Shield
                        AppGroupType.leadership -> Icons.Default.Grade
                        else -> Icons.Default.Forum
                    },
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!group.isActive) {
                Surface(color = Color(0xFFEAB308).copy(0.1f), shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = "ARCHIVED",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color(0xFFEAB308),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = group.name.uppercase(),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )
            Text(
                text = group.description ?: "Group channel",
                color = TextSecondary,
                fontSize = 14.sp,
                fontFamily = QuickSandFontFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OverviewStat(label = "TYPE", value = group.type.title.uppercase(), color = typeColor, modifier = Modifier.weight(1f))
                OverviewStat(label = "MEMBERS", value = group.memberIds.size.toString(), modifier = Modifier.weight(1f))
                OverviewStat(label = "ACCESS", value = if (group.isPublic) "PUBLIC" else "PRIVATE", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun OverviewStat(label: String, value: String, color: Color = Color.White, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
        Text(text = label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontFamily = QuickSandFontFamily)
    }
}

@Composable
fun ManagementActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = Color(0xFF22D3EE),
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        cornerRadius = 24.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
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
                    lineHeight = 16.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(0.2f), modifier = Modifier.size(20.dp))
        }
    }
}
