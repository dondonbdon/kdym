package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import dev.bti.kdym.data.models.UserRole
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun CommandCenterScreen(
    onNavigateToHub: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToTribeWars: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChurches: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                title = "COMMAND",
                subtitle = "Admin and leadership controls.",
                icon = Icons.Default.Tune
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                ProfileHeader(user)

                Spacer(modifier = Modifier.height(24.dp))

                val role = user?.roleEnum ?: UserRole.`public`

                if (role.canAccessCommand) {
                    CommandActionCard(
                        icon = Icons.Default.Tune,
                        iconColor = Color(0xFFEF4444),
                        title = "Command Center",
                        subtitle = "Manage camp mode, users, tribes, events, scoring, groups, and media.",
                        onClick = onNavigateToHub
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (role == UserRole.pastor || role.isAdmin || user?.isLeader == true) {
                    CommandActionCard(
                        icon = Icons.Default.AccountBalance,
                        iconColor = Color(0xFF22D3EE),
                        title = "Churches",
                        subtitle = "Manage district churches and pastors.",
                        onClick = onNavigateToChurches
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                CommandActionCard(
                    icon = Icons.Default.Campaign,
                    iconColor = Color(0xFF22D3EE),
                    title = "Announcements",
                    subtitle = "Camp alerts, KDYM updates, and leadership messages.",
                    onClick = onNavigateToAnnouncements
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (role.canAccessCampContent && isCampMode) {
                    CommandActionCard(
                        icon = Icons.Default.Flag,
                        iconColor = Color(0xFFEAB308),
                        title = "Tribe Wars",
                        subtitle = "Live scoreboard and recent score updates.",
                        onClick = onNavigateToTribeWars
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                CommandActionCard(
                    icon = Icons.Default.Settings,
                    iconColor = Color.White,
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
}

@Composable
fun ProfileHeader(user: AppUser?) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White.copy(alpha = 0.05f),
        contentPadding = 24.dp,
        cornerRadius = 32.dp,
        borderColor = Color.White.copy(alpha = 0.1f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFEF4444), Color(0xFF22D3EE))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.initials ?: "KM",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user?.displayName ?: "KDYM Member",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = user?.email ?: "don@don.don",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.2f))
                ) {
                    Text(
                        text = user?.roleEnum?.title?.uppercase() ?: "public",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(24.dp)
            )
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
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
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
                fontFamily = QuickSandFontFamily
            )
        }
    }
}
