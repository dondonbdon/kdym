package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun TribeWarsAdminScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddScore: (String?) -> Unit,
    onNavigateToCreateEvent: (String?) -> Unit,
    mainViewModel: dev.bti.kdym.viewmodels.MainViewModel,
    viewModel: AdminViewModel
) {
    val tribes by viewModel.tribes.collectAsState()
    val tribeEvents by viewModel.tribeEvents.collectAsState()
    val user by mainViewModel.user.collectAsState()
    val isAdmin = user?.hasCommandAccess == true

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.Shield,
                title = "TRIBE WARS",
                subtitle = "Live competition management and scoring."
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
            ) {
                item {
                    Text(
                        text = "SCOREBOARD",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "RANKINGS",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(tribes) { tribe ->
                    TribeScoreCard(
                        tribe = tribe,
                        showAddButton = isAdmin,
                        onAddScore = { onNavigateToAddScore(tribe.id) }
                    )
                }

                if (isAdmin) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "ADMIN",
                            color = Color(0xFFEF4444),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "SCORE CONTROLS",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ScoreActionButton(
                                icon = Icons.Default.Flag,
                                label = "CREATE EVENT",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToCreateEvent(null) }
                            )
                            ScoreActionButton(
                                icon = Icons.Default.AddBusiness,
                                label = "ADD POINTS",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToAddScore(null) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.finalizeScoreboard() },
                            cornerRadius = 32.dp
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = Color(0xFFEAB308)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "FINALIZE SCOREBOARD",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = QuickSandFontFamily
                                    )
                                    Text(
                                        text = "Publish current rankings to the Home feed.",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontFamily = QuickSandFontFamily
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White.copy(0.3f)
                                )
                            }
                        }
                    }
                }
                
                // Tribe Events Section
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "UPCOMING",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "TRIBE EVENTS",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (tribeEvents.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "No active events",
                                color = TextSecondary,
                                modifier = Modifier.padding(16.dp),
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                } else {
                    items(tribeEvents) { event ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToCreateEvent(event.id) },
                            cornerRadius = 24.dp
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(40.dp).background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = event.title.uppercase(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                                    Text(text = "${event.maxPoints} PTS AVAILABLE", color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                                }
                                if (isAdmin) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.White.copy(0.3f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                
                // Recent Scores Section (Placeholder)
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "LOG",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "RECENT SCORES",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Score history will appear here.",
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TribeScoreCard(tribe: Tribe, showAddButton: Boolean, onAddScore: () -> Unit) {
    val color = try {
        Color(tribe.colorHex.toColorInt())
    } catch (_: Exception) {
        Color(0xFFEF4444)
    }
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = color.copy(alpha = 0.1f),
        borderColor = color.copy(alpha = 0.2f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Text(
                text = "#${tribe.rank}",
                color = if (tribe.rank == 1) Color(0xFFEAB308) else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                modifier = Modifier.width(40.dp)
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tribe.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "${tribe.memberCount} members",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = tribe.totalPoints.toString(),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "PTS",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            if (showAddButton) {
                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onAddScore() },
                    color = Color.White.copy(0.1f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Points",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier, cornerRadius = 24.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}
