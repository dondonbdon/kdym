package dev.bti.kdym.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.KDYMEvent
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily

@Composable
fun EventList(events: List<KDYMEvent>, filter: String) {

    if (events.isEmpty()) {
        Text("No events yet...", color = Color.White)
        return
    }

    val filteredEvents = when (filter) {
        "CAMP SCHEDULE" -> events.filter { it.isCampEvent }
        else -> events
    }

    val grouped = filteredEvents
        .sortedBy { it.startDate }
        .groupBy { event ->
            event.startDate.toDate()
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .month
        }

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        grouped.forEach { (month, monthEvents) ->

            Column {

                Text(
                    text = "SCHEDULE",
                    color = RedAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )

                Text(
                    text = month?.name ?: "MONTH",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }

            monthEvents.forEach { event ->
                EventListCard(event = event)
            }
        }
    }
}