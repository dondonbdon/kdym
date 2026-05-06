package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun CommandCenterHubScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    onNavigateToTribes: () -> Unit,
    onNavigateToTribeWars: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToGroups: () -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToPostPlay: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val config by viewModel.appConfig.collectAsState()

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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFEF4444).copy(0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "COMMAND",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                lineHeight = 44.sp
            )
            Text(
                text = "Control the KDYM app experience.",
                color = TextSecondary,
                fontSize = 18.sp,
                fontFamily = RubikFontFamily
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Camp Status Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 32.dp,
                backgroundColor = Color.White.copy(0.05f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFEF4444)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (config?.campModeEnabled == true) "Camp Mode On" else "Camp Mode Off",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = RubikFontFamily
                        )
                        Text(
                            text = "Active Camp ID: ${config?.activeCampId ?: "camp_2026"}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = RubikFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ADMIN",
                color = Color(0xFFEF4444),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            Text(
                text = "LIVE CONTROLS",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )

            Spacer(modifier = Modifier.height(16.dp))

            CommandActionCard(
                icon = Icons.Default.LocalFireDepartment,
                iconColor = Color(0xFFEF4444),
                title = "CAMP SETTINGS",
                subtitle = "Toggle camp mode, registration, chat, schedule visibility, and active camp ID.",
                onClick = onNavigateToSettings
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.PersonAdd,
                iconColor = Color(0xFF22D3EE),
                title = "APPROVE CAMP ACCESS",
                subtitle = "Approve campers, assign roles, connect them to camp, and optionally assign tribes.",
                onClick = onNavigateToApprovals
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Shield,
                iconColor = Color.White,
                title = "MANAGE TRIBES",
                subtitle = "Create camp tribes and prepare the foundation for Tribe Wars scoring.",
                onClick = onNavigateToTribes
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Flag,
                iconColor = Color(0xFFEAB308),
                title = "TRIBE WARS",
                subtitle = "Create Tribe War events, add points, and monitor the live scoreboard.",
                onClick = onNavigateToTribeWars
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Campaign,
                iconColor = Color(0xFFEF4444),
                title = "ANNOUNCEMENTS",
                subtitle = "Send global alerts, updates, and messages to specific audiences.",
                onClick = onNavigateToAnnouncements
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Forum,
                iconColor = Color(0xFF22D3EE),
                title = "MANAGE GROUPS",
                subtitle = "Create official channels and control posting permissions.",
                onClick = onNavigateToGroups
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Event,
                iconColor = Color.White,
                title = "CREATE EVENTS",
                subtitle = "Post rallies, conventions, and camp schedule items.",
                onClick = onNavigateToCreateEvent
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.PlayCircle,
                iconColor = Color(0xFFEAB308),
                title = "POST PLAY CONTENT",
                subtitle = "Upload videos, worship clips, and photo galleries.",
                onClick = onNavigateToPostPlay
            )

            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}
