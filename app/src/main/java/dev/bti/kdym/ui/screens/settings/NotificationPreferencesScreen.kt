package dev.bti.kdym.ui.screens.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.bti.kdym.data.models.NotificationPreferences
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun NotificationPreferencesScreen(
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()

    var preferences by remember(user) {
        mutableStateOf(user?.notificationPreferences ?: NotificationPreferences())
    }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Custom Header with Save button top left (or near back)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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

                Button(
                    onClick = {
                        viewModel.updateNotificationPreferences(preferences)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "SAVE", fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF22D3EE).copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "NOTIFICATIONS",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "Choose what KDYM can alert you about.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val context = LocalContext.current
                var hasPermission by remember {
                    mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                        } else true
                    )
                }

                // Permission Status Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    backgroundColor = Color.Black.copy(0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (hasPermission) Color(0xFF10B981).copy(0.1f) else Color(0xFFEF4444).copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasPermission) Icons.Default.CheckCircle else Icons.Default.Error, 
                                contentDescription = null, 
                                tint = if (hasPermission) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (hasPermission) "Permission: Authorized" else "Permission: Denied",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = if (hasPermission) "This device can receive KDYM push notifications." else "Enable notifications to stay updated.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                        
                        if (!hasPermission) {
                            Button(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        viewModel.triggerPermissionRequest()
                                    } else {
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                            data = Uri.fromParts("package", context.packageName, null)
                                        }
                                        context.startActivity(intent)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("GRANT", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Section: Phone Alerts
                NotificationSection(title = "PHONE ALERTS", subtitle = "PREFERENCES") {
                    CommandSwitch(
                        title = "Push Notifications",
                        description = "Master switch for phone alerts from KDYM.",
                        checked = preferences.pushEnabled,
                        onCheckedChange = { preferences = preferences.copy(pushEnabled = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Announcements",
                        description = "General KDYM and ministry-wide updates.",
                        checked = preferences.announcements,
                        onCheckedChange = { preferences = preferences.copy(announcements = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Urgent Alerts",
                        description = "Important time-sensitive alerts from KDYM leadership.",
                        checked = preferences.urgentAlerts,
                        onCheckedChange = { preferences = preferences.copy(urgentAlerts = it) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Section: Camp Mode
                NotificationSection(title = "CAMP MODE", subtitle = "CAMP") {
                    CommandSwitch(
                        title = "Camp Updates",
                        description = "Camp schedule, access, location, and operational updates.",
                        checked = preferences.campUpdates,
                        onCheckedChange = { preferences = preferences.copy(campUpdates = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Tribe Updates",
                        description = "Tribe-related announcements and messages.",
                        checked = preferences.tribeUpdates,
                        onCheckedChange = { preferences = preferences.copy(tribeUpdates = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Tribe Wars",
                        description = "Score updates, event results, and standings alerts.",
                        checked = preferences.tribeWarUpdates,
                        onCheckedChange = { preferences = preferences.copy(tribeWarUpdates = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Event Updates",
                        description = "Newly published events and schedule changes.",
                        checked = preferences.eventUpdates,
                        onCheckedChange = { preferences = preferences.copy(eventUpdates = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Event Reminders",
                        description = "Upcoming calendar event alerts and reminders.",
                        checked = preferences.eventReminders,
                        onCheckedChange = { preferences = preferences.copy(eventReminders = it) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Section: Community/Groups
                NotificationSection(title = "GROUPS", subtitle = "COMMUNITY") {
                    CommandSwitch(
                        title = "Group Messages",
                        description = "New message alerts from your joined groups.",
                        checked = preferences.groupMessages,
                        onCheckedChange = { preferences = preferences.copy(groupMessages = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Group Membership",
                        description = "Alerts when you are added or removed from groups.",
                        checked = preferences.groupMembership,
                        onCheckedChange = { preferences = preferences.copy(groupMembership = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Group Join Requests",
                        description = "Alerts when someone wants to join your private groups.",
                        checked = preferences.groupJoinRequests,
                        onCheckedChange = { preferences = preferences.copy(groupJoinRequests = it) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Section: Play/Feed
                NotificationSection(title = "PLAY & FEED", subtitle = "MEDIA") {
                    CommandSwitch(
                        title = "Play Drops",
                        description = "Alerts for new video, audio, or gallery content.",
                        checked = preferences.playDrops,
                        onCheckedChange = { preferences = preferences.copy(playDrops = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Comments",
                        description = "Alerts when someone comments on your content.",
                        checked = preferences.comments,
                        onCheckedChange = { preferences = preferences.copy(comments = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Reactions",
                        description = "Alerts when someone reacts to your content.",
                        checked = preferences.reactions,
                        onCheckedChange = { preferences = preferences.copy(reactions = it) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Section: Leadership/Admin
                NotificationSection(title = "ADMIN ALERTS", subtitle = "LEADERSHIP") {
                    CommandSwitch(
                        title = "Access Requests",
                        description = "New camp access requests for review.",
                        checked = preferences.accessRequests,
                        onCheckedChange = { preferences = preferences.copy(accessRequests = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Moderation Alerts",
                        description = "Alerts about flagged content or community reports.",
                        checked = preferences.adminModeration,
                        onCheckedChange = { preferences = preferences.copy(adminModeration = it) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CommandSwitch(
                        title = "Leader Alerts",
                        description = "Special instructions and alerts for camp leadership.",
                        checked = preferences.leaderAlerts,
                        onCheckedChange = { preferences = preferences.copy(leaderAlerts = it) }
                    )
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}

@Composable
fun NotificationSection(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = subtitle,
            color = Color(0xFFEF4444),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            fontFamily = QuickSandFontFamily
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(content = content)
    }
}
