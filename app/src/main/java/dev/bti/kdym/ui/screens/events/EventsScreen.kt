package dev.bti.kdym.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun EventsScreen(
    onNavigateToEventDetail: (String) -> Unit,
    mainViewModel: MainViewModel = viewModel()
) {
    var selectedSegment by remember { mutableStateOf("ALL EVENTS") }
    val events by mainViewModel.allEvents.collectAsState()
    val appConfig by mainViewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false

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
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    EventsTopBar()
                    Spacer(modifier = Modifier.height(32.dp))
                }

                item {
                    SegmentedControl(
                        segments = listOf("ALL EVENTS", "CAMP SCHEDULE"),
                        selectedSegment = selectedSegment,
                        onSegmentSelected = { selectedSegment = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    EventList(
                        events = events,
                        filter = selectedSegment,
                        onEventClick = { event -> onNavigateToEventDetail(event.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }
    }
}

@Composable
fun EventsTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = null,
                    tint = RedAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "KDYM EVENTS",
                    color = RedAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
            Text(
                text = "THE YEAR",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                lineHeight = 44.sp
            )
            Text(
                text = "IN ONE FLOW",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                lineHeight = 44.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "District events and camp schedule items in one clean list.",
                color = TextSecondary,
                fontSize = 16.sp,
                fontFamily = RubikFontFamily
            )
        }

        Surface(
            modifier = Modifier.size(48.dp),
            color = Color.White,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
