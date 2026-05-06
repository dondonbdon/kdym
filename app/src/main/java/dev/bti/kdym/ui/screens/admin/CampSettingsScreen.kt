package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.AppConfig
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.CommandSwitch
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.StandardTopBar
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun CampSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val config by viewModel.appConfig.collectAsState()

    var localConfig by remember(config) { mutableStateOf(config ?: AppConfig()) }
    var activeCampId by remember(config) { mutableStateOf(config?.activeCampId ?: "camp_2026") }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            StandardTopBar(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.LocalFireDepartment
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CAMP SETTINGS",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                lineHeight = 44.sp
            )

            Text(
                text = "Control how the app behaves during camp.",
                color = TextSecondary,
                fontSize = 18.sp,
                fontFamily = RubikFontFamily
            )

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
