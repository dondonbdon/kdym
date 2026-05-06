package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.ui.components.CommandActionCard
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun CommandCenterScreen(
    onNavigateToHub: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToNotificationPrefs: () -> Unit,
    onNavigateToTribeWars: () -> Unit,
    onNavigateToAccessStatus: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "COMMAND",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
            Text(
                text = "Admin and leadership controls.",
                color = TextSecondary,
                fontSize = 18.sp,
                fontFamily = RubikFontFamily
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileHeader(user)

            Spacer(modifier = Modifier.height(24.dp))

            CommandActionCard(
                icon = Icons.Default.Tune,
                iconColor = Color(0xFFEF4444),
                title = "Command Center",
                subtitle = "Manage camp mode, users, tribes, events, scoring, groups, and media.",
                onClick = onNavigateToHub
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Campaign,
                iconColor = Color(0xFF22D3EE),
                title = "Announcements",
                subtitle = "Camp alerts, KDYM updates, and leadership messages.",
                onClick = onNavigateToAnnouncements
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.NotificationsActive,
                iconColor = Color(0xFF22D3EE),
                title = "Notification Preferences",
                subtitle = "Choose what KDYM can alert you about.",
                onClick = onNavigateToNotificationPrefs
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Flag,
                iconColor = Color(0xFF22D3EE),
                title = "Tribe Wars",
                subtitle = "Live scoreboard and recent score updates.",
                onClick = onNavigateToTribeWars
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Person,
                iconColor = Color(0xFF22D3EE),
                title = "Access Status",
                subtitle = user?.accessStatus ?: "Public Account",
                onClick = onNavigateToAccessStatus
            )

            Spacer(modifier = Modifier.height(12.dp))

            CommandActionCard(
                icon = Icons.Default.Settings,
                iconColor = Color(0xFF22D3EE),
                title = "Profile & Settings",
                subtitle = "Edit profile, account details, and preferences.",
                onClick = onNavigateToProfile
            )

            Spacer(modifier = Modifier.height(32.dp))

            SignOutButton(onSignOut = { viewModel.signOut() })

            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}

@Composable
fun ProfileHeader(user: AppUser?) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        contentPadding = 20.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF22D3EE))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.initials ?: "KM",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = user?.displayName ?: "KDYM Member",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = user?.email ?: "don@don.don",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = RubikFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = user?.roleEnum?.title?.uppercase() ?: "public",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontFamily = RubikFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun CommandActionCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        contentPadding = 16.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = RubikFontFamily
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SignOutButton(onSignOut: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onSignOut() },
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(28.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "SIGN OUT",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
        }
    }
}
