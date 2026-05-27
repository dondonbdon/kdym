package dev.bti.kdym.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.KDYMEvent
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily

@Composable
fun EventList(
    events: List<KDYMEvent>,
    isCampSchedule: Boolean,
    onEventClick: (KDYMEvent) -> Unit
) {
    if (events.isEmpty()) {
        Text("No events yet...", color = Color.White)
        return
    }

    val grouped = events
        .sortedBy { it.startDate }
        .groupBy { event ->
            if (isCampSchedule) "SCHEDULE"
            else event.startDate.toDate()
                .toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .month.name
        }

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        grouped.forEach { (header, monthEvents) ->
            Column {
                Text(
                    text = if (isCampSchedule) "CAMP" else "SCHEDULE",
                    color = RedAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )

                Text(
                    text = header,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            monthEvents.forEach { event ->
                EventListCard(event = event, onClick = { onEventClick(event) })
            }
        }
    }
}