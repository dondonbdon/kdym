package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Shield
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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun ManageTribesScreen(
    onNavigateBack: () -> Unit,
    onCreateTribe: () -> Unit,
    onNavigateToTribeDetail: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val tribes by viewModel.tribes.collectAsState()
    val config by viewModel.appConfig.collectAsState()

    OutpourBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                ScreenHeader(
                    onNavigateBack = onNavigateBack,
                    icon = Icons.Default.Shield,
                    title = "TRIBES",
                    subtitle = "Create tribes and manage membership."
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Active Camp ID: ${config?.activeCampId ?: "camp_2026"}",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontFamily = RubikFontFamily
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (tribes.isEmpty()) {
                        NoTribesPlaceholder(onCreateTribe)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 140.dp)
                        ) {
                            items(tribes) { tribe ->
                                TribeListItem(tribe = tribe, onClick = { onNavigateToTribeDetail(tribe.id) })
                            }
                        }
                    }
                }
            }

            AnimatedFab(
                onClick = onCreateTribe,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 24.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Tribe")
            }
        }
    }
}

@Composable
fun NoTribesPlaceholder(onCreateTribe: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.White.copy(0.05f)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tribes yet",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = RubikFontFamily
            )
            Text(
                text = "Create the tribes for the active camp. Then assign leaders and members from each tribe detail page.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontFamily = RubikFontFamily
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCreateTribe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CREATE TRIBE",
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
        }
    }
}

@Composable
fun TribeListItem(tribe: Tribe, onClick: () -> Unit) {
    val color = try {
        Color(tribe.colorHex.toColorInt())
    } catch (_: Exception) {
        Color(0xFFEF4444)
    }
    GlassCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tribe.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = tribe.subtitle ?: "${tribe.memberCount} members",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = RubikFontFamily
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
