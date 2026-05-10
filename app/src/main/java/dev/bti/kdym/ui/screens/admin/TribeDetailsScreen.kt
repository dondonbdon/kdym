package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun TribeDetailsScreen(
    tribeId: String,
    onNavigateBack: () -> Unit,
    onManagePeople: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val tribes by viewModel.tribes.collectAsState()
    val tribe = remember(tribeId, tribes) { tribes.find { it.id == tribeId } }
    val allUsers by viewModel.allUsers.collectAsState()

    val tribeLeaders = remember(tribe, allUsers) {
        allUsers.filter { it.uid in (tribe?.leaderIds ?: emptyList()) }
    }
    val tribeMembers = remember(tribe, allUsers) {
        allUsers.filter { it.uid in (tribe?.memberIds ?: emptyList()) }
    }

    val defaultColor = 0xFFEF4444.toInt()

    val tribeColor = try {
        Color((tribe?.colorHex?.toColorInt() ?: defaultColor).toLong())
    } catch (_: Exception) {
        Color(0xFFEF4444)
    }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.LocalFireDepartment,
                title = tribe?.name?.uppercase() ?: "TRIBE",
                subtitle = "Camp tribe"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
            ) {
                // Stats Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TribeStatCard(label = "MEMBERS", value = tribe?.memberCount?.toString() ?: "0", color = tribeColor, modifier = Modifier.weight(1f))
                        TribeStatCard(label = "LEADERS", value = tribe?.leaderIds?.size?.toString() ?: "0", color = tribeColor, modifier = Modifier.weight(1f))
                        TribeStatCard(label = "POINTS", value = tribe?.totalPoints?.toString() ?: "0", color = Color(0xFFFBBF24), modifier = Modifier.weight(1f))
                    }
                }

                // Official Group Status
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.White.copy(0.05f)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Official group pending", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
                                Text(text = "The Cloud Function will create the official tribe group automatically after this tribe syncs.", color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
                            }
                        }
                    }
                }

                // Manage People Button
                item {
                    Button(
                        onClick = { onManagePeople(tribeId) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "MANAGE TRIBE PEOPLE", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                        }
                    }
                }

                // Leaders List
                item {
                    Text(text = "LEADERSHIP", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "TRIBE LEADERS", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                }

                if (tribeLeaders.isEmpty()) {
                    item {
                        Text(text = "No leaders assigned.", color = TextSecondary, modifier = Modifier.padding(vertical = 16.dp))
                    }
                } else {
                    items(tribeLeaders) { leader ->
                        TribeUserItem(user = leader, role = "LEADER", color = Color(0xFF67E8F9))
                    }
                }

                // Members List
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "CAMPERS", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "TRIBE MEMBERS", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                }

                if (tribeMembers.isEmpty()) {
                    item {
                        Text(text = "No members assigned.", color = TextSecondary, modifier = Modifier.padding(vertical = 16.dp))
                    }
                } else {
                    items(tribeMembers) { member ->
                        TribeUserItem(user = member, role = "MEMBER", color = tribeColor)
                    }
                }
            }
        }
    }
}

@Composable
fun TribeStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, backgroundColor = Color.White.copy(0.05f)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
        }
    }
}

@Composable
fun TribeUserItem(user: AppUser, role: String, color: Color) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), backgroundColor = Color.White.copy(0.05f)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color.White.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = user.initials, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
                Text(text = user.email, color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
            }
            Surface(color = color.copy(0.1f), shape = RoundedCornerShape(12.dp)) {
                Text(
                    text = role, 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), 
                    color = color, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
