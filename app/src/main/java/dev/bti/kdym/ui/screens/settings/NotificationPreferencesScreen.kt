package dev.bti.kdym.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.NotificationPreferences
import dev.bti.kdym.ui.components.CommandSwitch
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun NotificationPreferencesScreen(
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = viewModel()
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFF22D3EE)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "NOTIFICATIONS",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                lineHeight = 44.sp
            )
            Text(
                text = "Choose what KDYM can alert you about.",
                color = TextSecondary,
                fontSize = 18.sp,
                fontFamily = RubikFontFamily
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "PREFERENCES",
                color = Color(0xFFEF4444),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            Text(
                text = "PHONE ALERTS",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            CommandSwitch(
                title = "Push Notifications",
                description = "Enable global push notifications for this device.",
                checked = preferences.pushEnabled,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(pushEnabled = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandSwitch(
                title = "Announcements",
                description = "Official news and KDYM updates.",
                checked = preferences.announcements,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(announcements = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandSwitch(
                title = "Urgent Updates",
                description = "Critical alerts and immediate camp news.",
                checked = preferences.urgentAlerts,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(urgentAlerts = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandSwitch(
                title = "Camp Updates",
                description = "General information about the current camp.",
                checked = preferences.campUpdates,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(campUpdates = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandSwitch(
                title = "Tribe Updates",
                description = "Notifications specific to your tribe.",
                checked = preferences.tribeUpdates,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(tribeUpdates = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandSwitch(
                title = "Group Messages",
                description = "Alerts when someone messages in your groups.",
                checked = preferences.groupMessages,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(groupMessages = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandSwitch(
                title = "Play Drops",
                description = "New video, audio, or gallery content alerts.",
                checked = preferences.playDrops,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(playDrops = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandSwitch(
                title = "Event Reminders",
                description = "Alerts for upcoming events on your schedule.",
                checked = preferences.eventReminders,
                onCheckedChange = { checked ->
                    preferences = preferences.copy(eventReminders = checked)
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.updateNotificationPreferences(preferences)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Text(
                    text = "SAVE PREFERENCES",
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
            
            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}
