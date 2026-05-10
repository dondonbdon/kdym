package dev.bti.kdym.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val events by viewModel.allEvents.collectAsState()
    val event = events.find { it.id == eventId } ?: return

    val accentColor = if (event.isCampEvent) Color(0xFF22D3EE) else RedAccent

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.CalendarToday,
                iconColor = accentColor,
                title = event.title,
                subtitle = event.subtitle
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = event.category.title.uppercase(),
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(32.dp))

                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp, backgroundColor = Color.Black.copy(0.3f)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailItem(icon = Icons.Default.CalendarToday, label = "DATE", value = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(event.startDate.toDate()))
                        Spacer(modifier = Modifier.height(16.dp))
                        DetailItem(icon = Icons.Default.Schedule, label = "TIME", value = event.timeText)
                        event.location?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            DetailItem(icon = Icons.Default.LocationOn, label = "LOCATION", value = it)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "ABOUT THE EVENT",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = event.description,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}

@Composable
fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
