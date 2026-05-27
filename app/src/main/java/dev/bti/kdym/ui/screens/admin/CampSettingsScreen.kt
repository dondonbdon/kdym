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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Link
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
import dev.bti.kdym.data.models.NewAccountAccessDefault
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun NewUserOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // <-- Trigger callback here
        color = if (selected) Color.White.copy(0.05f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = if (selected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFF22D3EE).copy(0.3f)
        ) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (selected) Color(0xFF22D3EE) else Color.White.copy(0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
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
            // New Back Button Style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
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
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "CAMP SETTINGS",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "Control how the app behaves during camp.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
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
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "NEW USERS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Default New Account Access",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "Choose what happens when someone creates a new KDYM account.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontFamily = QuickSandFontFamily
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        NewUserOption(
                            title = "Public Account",
                            subtitle = "New users start with general KDYM access only.",
                            selected = localConfig.newAccountAccessDefault == NewAccountAccessDefault.PUBLIC,
                            onClick = {
                                localConfig =
                                    localConfig.copy(newAccountAccessDefault = NewAccountAccessDefault.PUBLIC)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NewUserOption(
                            title = "Pending Camper",
                            subtitle = "New users become camper requests awaiting approval.",
                            selected = localConfig.newAccountAccessDefault == NewAccountAccessDefault.PENDING_CAMPER,
                            onClick = {
                                localConfig =
                                    localConfig.copy(newAccountAccessDefault = NewAccountAccessDefault.PENDING_CAMPER)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        NewUserOption(
                            title = "Approved Camper",
                            subtitle = "New users immediately receive approved camper access.",
                            selected = localConfig.newAccountAccessDefault == NewAccountAccessDefault.APPROVED_CAMPER,
                            onClick = {
                                localConfig =
                                    localConfig.copy(newAccountAccessDefault = NewAccountAccessDefault.APPROVED_CAMPER)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "LIVE",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "FACEBOOK",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CommandSwitch(
                            title = "Facebook Live",
                            description = "Show a live card on Home when a stream URL is available.",
                            checked = localConfig.facebookLiveEnabled,
                            onCheckedChange = { localConfig = localConfig.copy(facebookLiveEnabled = it) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CommandInputField(
                            value = localConfig.facebookLiveURL ?: "",
                            onValueChange = { localConfig = localConfig.copy(facebookLiveURL = it) },
                            placeholder = "Stream URL",
                            icon = Icons.Default.Link
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CommandInputField(
                            value = localConfig.facebookLiveTitle ?: "",
                            onValueChange = { localConfig = localConfig.copy(facebookLiveTitle = it) },
                            placeholder = "Live Title",
                            icon = Icons.Default.Facebook
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "VISIBILITY",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )

                Text(
                    text = "CAMP CONTROLS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                CommandSwitch(
                    title = "Public Registration Open",
                    description = "Show registration prompts and allow users to request camp access.",
                    checked = localConfig.publicRegistrationOpen,
                    onCheckedChange = {
                        localConfig = localConfig.copy(publicRegistrationOpen = it)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandSwitch(
                    title = "Camp Schedule Visible",
                    description = "Approved campers can view camp schedule items.",
                    checked = localConfig.allowCampScheduleVisible,
                    onCheckedChange = {
                        localConfig = localConfig.copy(allowCampScheduleVisible = it)
                    }
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
                        fontFamily = QuickSandFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}
