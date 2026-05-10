package dev.bti.kdym.ui.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Icon
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
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.components.GroupListCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun GroupsScreen(
    onNavigateToChat: (String) -> Unit,
    viewModel: GroupsViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val appConfig by mainViewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false
    val accentColor = if (isCampMode) Color(0xFF10B981) else Color(0xFFEF4444)

    val tribeWars = AppGroup(
        name = "TRIBE WARS",
        description = "View live rankings, current scores, and recent point changes.",
        type = AppGroupType.tribe,
        isOfficial = false
    )

    val announcements = AppGroup(
        name = "ANNOUNCEMENTS",
        description = "Camp alerts, KDYM updates, and leadership messages.",
        type = AppGroupType.general,
        isOfficial = false
    )

    val groups by viewModel.groups.collectAsState()

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(isPrimary = true, isCampMode = isCampMode)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
            ) {

                item {
                    // Header Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(accentColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "GROUPS",
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily,
                        lineHeight = 44.sp
                    )

                    Text(
                        text = "Your tribe, cabin, and leadership groups.",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = RubikFontFamily
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Fixed Cards
                if (isCampMode) {
                    item {
                        GroupListCard(group = tribeWars)
                    }
                }

                item {
                    GroupListCard(group = announcements)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Your Channels Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "YOUR CHANNELS",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(groups) { group ->
                    GroupListCard(group = group, onClick = { onNavigateToChat(group.id) })
                }
            }
        }
    }
}
