package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun CreateTribeScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    var tribeName by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var iconName by remember { mutableStateOf("shield") }
    var colorHex by remember { mutableStateOf("#EF4444") }

    val allUsers by viewModel.allUsers.collectAsState()
    var selectedLeaderIds by remember { mutableStateOf(setOf<String>()) }
    var showLeaderPicker by remember { mutableStateOf(false) }

    if (showLeaderPicker) {
        UserSelectionDialog(
            title = "ASSIGN LEADERS",
            users = allUsers,
            selectedUserIds = selectedLeaderIds,
            onDismiss = { showLeaderPicker = false },
            onConfirmed = {
                selectedLeaderIds = it
                showLeaderPicker = false
            }
        )
    }

    val colors = listOf("#EF4444", "#22D3EE", "#EAB308", "#10B981", "#8B5CF6", "#F472B6")
    val icons = listOf(
        "shield" to Icons.Default.Shield,
        "flame" to Icons.Default.LocalFireDepartment,
        "bolt" to Icons.Default.Bolt,
        "crown" to Icons.Default.EmojiEvents,
        "star" to Icons.Default.Star,
        "rabbit" to Icons.Default.Pets,
        "leaf" to Icons.Default.Eco,
        "sun" to Icons.Default.LightMode
    )

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.Shield,
                title = "CREATE",
                subtitle = "Define a new camp tribe."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "TRIBE",
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily,
                    lineHeight = 44.sp
                )

                Text(
                    text = "Camp ID: camp_2026",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(32.dp))

                CommandInputField(
                    value = tribeName,
                    onValueChange = { tribeName = it },
                    placeholder = "Tribe Name",
                    icon = Icons.Default.Shield
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandInputField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    placeholder = "Subtitle",
                    icon = Icons.AutoMirrored.Default.Notes
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandInputField(
                    value = iconName,
                    onValueChange = { iconName = it },
                    placeholder = "shield.fill",
                    icon = Icons.Default.AutoAwesome
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandInputField(
                    value = colorHex,
                    onValueChange = { colorHex = it },
                    placeholder = "#EF4444",
                    icon = Icons.Default.Palette
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "QUICK PICK",
                    color = Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "COLORS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colors.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (colorHex == hex) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { colorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "QUICK PICK",
                    color = Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "ICONS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    icons.chunked(4).forEach { rowIcons ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowIcons.forEach { (name, vector) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .background(
                                            if (iconName == name) Color.White else Color.White.copy(
                                                alpha = 0.05f
                                            ),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { iconName = name },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = vector,
                                        contentDescription = name,
                                        tint = if (iconName == name) Color.Black else Color.White.copy(
                                            alpha = 0.6f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "LEADERSHIP",
                    color = Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ASSIGN LEADERS",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    IconButton(
                        onClick = { showLeaderPicker = true },
                        modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedLeaderIds.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        color = Color.White.copy(0.05f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "No leaders assigned", color = TextSecondary)
                        }
                    }
                } else {
                    selectedLeaderIds.forEach { uid ->
                        val user = allUsers.find { it.uid == uid }
                        user?.let {
                            Surface(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                color = Color.White.copy(0.05f),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(48.dp).background(Color(0xFFEF4444).copy(0.2f), CircleShape), contentAlignment = Alignment.Center) {
                                        Text(text = it.initials, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(text = it.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(text = it.email, color = TextSecondary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.createTribe(tribeName, subtitle, colorHex, iconName)
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = CircleShape,
                    enabled = tribeName.isNotEmpty()
                ) {
                    Text(
                        text = "CREATE TRIBE",
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}
