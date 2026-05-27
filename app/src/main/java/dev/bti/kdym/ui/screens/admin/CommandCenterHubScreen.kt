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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.MainViewModel

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
    onNavigateToUsers: () -> Unit,
    onNavigateToChurches: () -> Unit,
    viewModel: AdminViewModel,
    mainViewModel: MainViewModel
) {
    val config by viewModel.appConfig.collectAsState()
    val pendingCount by viewModel.pendingUserCount.collectAsState()
    val totalCount by viewModel.totalUserCount.collectAsState()
    val approvedCount by viewModel.approvedUserCount.collectAsState()

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
                            .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "COMMAND",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "Manage KDYM without the technical clutter.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats and Mode Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 32.dp,
                    backgroundColor = Color.Black.copy(0.3f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(if (config?.campModeEnabled == true) Color(0xFF10B981) else Color(0xFFEF4444), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (config?.campModeEnabled == true) "Camp Mode is on" else "Camp Mode is off",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuickSandFontFamily
                                )
                                Text(
                                    text = if (config?.campModeEnabled == true) "Live camp experience is active." else "Year-round experience is active.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = QuickSandFontFamily
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatBox(count = pendingCount, label = "PENDING", modifier = Modifier.weight(1f))
                            StatBox(count = totalCount, label = "USERS", modifier = Modifier.weight(1f))
                            StatBox(count = approvedCount, label = "APPROVED", modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "MANAGE",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                Text(
                    text = "MINISTRY TOOLS",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MinistryToolCard(
                        icon = Icons.Default.Person,
                        iconColor = Color(0xFF22D3EE),
                        title = "ACCESS",
                        subtitle = "Approve requests and roles",
                        onClick = onNavigateToApprovals,
                        modifier = Modifier.weight(1f)
                    )
                    MinistryToolCard(
                        icon = Icons.Default.LocalFireDepartment,
                        iconColor = Color(0xFFEF4444),
                        title = "CAMP",
                        subtitle = "Mode, live links, settings",
                        onClick = onNavigateToSettings,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MinistryToolCard(
                        icon = Icons.Default.Shield,
                        iconColor = Color(0xFFEAB308),
                        title = "TRIBES",
                        subtitle = "Teams, leaders, scores",
                        onClick = onNavigateToTribes,
                        modifier = Modifier.weight(1f)
                    )
                    MinistryToolCard(
                        icon = Icons.Default.Forum,
                        iconColor = Color(0xFF22D3EE),
                        title = "GROUPS",
                        subtitle = "Chats and permissions",
                        onClick = onNavigateToGroups,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "PUBLISH",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                Text(
                    text = "HOME",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToCreateEvent() },
                    cornerRadius = 32.dp,
                    backgroundColor = Color.Black.copy(0.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFEF4444))
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CREATE HOME UPDATE",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = "Post announcements, urgent alerts, links, images, or group-targeted updates.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}

@Composable
fun StatBox(count: Int, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(72.dp),
        color = Color.White.copy(0.05f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                color = if (label == "USERS") Color(0xFF22D3EE) else if (label == "PENDING") Color(0xFFEAB308) else Color(0xFF10B981),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}

@Composable
fun MinistryToolCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .heightIn(min = 160.dp)
            .clickable { onClick() },
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
