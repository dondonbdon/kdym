package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.Announcement
import dev.bti.kdym.data.models.AnnouncementPriority
import dev.bti.kdym.ui.components.AnimatedFab
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.components.SendAnnouncementDialog
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnnouncementsScreen(
    onNavigateBack: () -> Unit,
    mainViewModel: MainViewModel,
    adminViewModel: AdminViewModel
) {
    val announcements by mainViewModel.announcements.collectAsState()
    val user by mainViewModel.user.collectAsState()
    val isAdmin = user?.hasCommandAccess == true
    
    var showSendDialog by remember { mutableStateOf(false) }
    var editingAnnouncement by remember { mutableStateOf<Announcement?>(null) }

    if (showSendDialog || editingAnnouncement != null) {
        SendAnnouncementDialog(
            announcement = editingAnnouncement,
            onDismiss = { 
                showSendDialog = false
                editingAnnouncement = null
            },
            onConfirm = { title, message, priority ->
                if (editingAnnouncement != null) {
                    adminViewModel.updateAnnouncement(editingAnnouncement!!.copy(
                        title = title,
                        body = message,
                        priority = priority
                    ))
                } else {
                    adminViewModel.sendAnnouncement(title, message, priority)
                }
                showSendDialog = false
                editingAnnouncement = null
            }
        )
    }

    OutpourBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                ScreenHeader(
                    onNavigateBack = onNavigateBack,
                    icon = Icons.Default.Campaign,
                    title = "ANNOUNCEMENTS",
                    subtitle = "Camp alerts and leadership messages.",
                    titleSize = 32.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
                ) {
                    if (announcements.isEmpty()) {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF22D3EE))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(text = "No announcements yet", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
                                    Text(text = "Announcements from KDYM leadership will appear here.", color = TextSecondary, fontFamily = RubikFontFamily)
                                }
                            }
                        }
                    } else {
                        items(announcements) { announcement ->
                            AnnouncementCard(
                                announcement = announcement,
                                isAdmin = isAdmin,
                                onEdit = { editingAnnouncement = announcement },
                                onDelete = { adminViewModel.deleteAnnouncement(announcement.id) }
                            )
                        }
                    }
                }
            }

            if (isAdmin) {
                AnimatedFab(
                    onClick = { showSendDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 100.dp, end = 24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Announcement")
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    isAdmin: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val priorityColor = when (announcement.priority) {
        AnnouncementPriority.urgent -> Color(0xFFEF4444)
        AnnouncementPriority.important -> Color(0xFFFBBF24)
        else -> Color(0xFF22D3EE)
    }

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, backgroundColor = Color.White.copy(0.05f)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = priorityColor.copy(0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, priorityColor.copy(0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (announcement.priority == AnnouncementPriority.normal) Icons.Default.Campaign else Icons.Default.Info,
                                contentDescription = null,
                                tint = priorityColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = announcement.priority.name.uppercase(),
                                color = priorityColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontFamily = RubikFontFamily
                            )
                        }
                    }
                }
                
                Text(
                    text = "EVERYONE",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = RubikFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = announcement.title.uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = announcement.body,
                color = TextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = RubikFontFamily,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KDYM MEMBER",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily,
                    letterSpacing = 1.sp
                )

                val dateStr = announcement.createdAt?.let { 
                    SimpleDateFormat("MMMM d, yyyy", Locale.US).format(it.toDate()) 
                } ?: ""
                
                Text(
                    text = dateStr.uppercase(),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily,
                    letterSpacing = 1.sp
                )
            }

            if (isAdmin) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp).background(Color.White.copy(0.05f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp).background(Color.White.copy(0.05f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
