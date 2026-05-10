package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AppConfig
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun NewUserOption(title: String, subtitle: String, selected: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        color = if (selected) Color.White.copy(0.05f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22D3EE).copy(0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (selected) Color(0xFF22D3EE) else Color.White.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
                Text(text = subtitle, color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
            }
        }
    }
}

@Composable
fun CampSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    val config by viewModel.appConfig.collectAsState()

    var localConfig by remember(config) { mutableStateOf(config ?: AppConfig()) }
    var activeCampId by remember(config) { mutableStateOf(config?.activeCampId ?: "camp_2026") }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.LocalFireDepartment,
                title = "CAMP SETTINGS",
                subtitle = "Control how the app behaves during camp."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                CommandSwitch(
                    title = "Camp Mode",
                    description = "Turns the app into the live camp command center.",
                    checked = localConfig.campModeEnabled,
                    onCheckedChange = { localConfig = localConfig.copy(campModeEnabled = it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandInputField(
                    value = activeCampId,
                    onValueChange = { activeCampId = it },
                    placeholder = "camp_2026",
                    icon = Icons.Default.Tag
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "ACCOUNTS",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "NEW USERS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Default New Account Access", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
                        Text(text = "Choose what happens when someone creates a new KDYM account.", color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        NewUserOption("Public Account", "New users start with general KDYM access only.", true)
                        Spacer(modifier = Modifier.height(12.dp))
                        NewUserOption("Pending Camper", "New users become camper requests awaiting approval.", false)
                        Spacer(modifier = Modifier.height(12.dp))
                        NewUserOption("Approved Camper", "New users immediately receive approved camper access.", false)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "VISIBILITY",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )

                Text(
                    text = "CAMP CONTROLS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                CommandSwitch(
                    title = "Public Registration Open",
                    description = "Show registration prompts and allow users to request camp access.",
                    checked = localConfig.publicRegistrationOpen,
                    onCheckedChange = { localConfig = localConfig.copy(publicRegistrationOpen = it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandSwitch(
                    title = "Camp Schedule Visible",
                    description = "Approved campers can view camp schedule items.",
                    checked = localConfig.allowCampScheduleVisible,
                    onCheckedChange = { localConfig = localConfig.copy(allowCampScheduleVisible = it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandSwitch(
                    title = "Group Chat Enabled",
                    description = "Allows official group chats when groups are built.",
                    checked = localConfig.allowGroupChat,
                    onCheckedChange = { localConfig = localConfig.copy(allowGroupChat = it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandSwitch(
                    title = "Tribe Chat Enabled",
                    description = "Allows official tribe chats when tribes are built.",
                    checked = localConfig.allowTribeChat,
                    onCheckedChange = { localConfig = localConfig.copy(allowTribeChat = it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandSwitch(
                    title = "Maintenance Mode",
                    description = "Locks the app for maintenance. Only admins can enter.",
                    checked = localConfig.maintenanceMode,
                    onCheckedChange = { localConfig = localConfig.copy(maintenanceMode = it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.updateAppConfig(localConfig.copy(activeCampId = activeCampId)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = CircleShape
                ) {
                    Text(
                        text = "SAVE CAMP SETTINGS",
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}
