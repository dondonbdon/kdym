package dev.bti.kdym.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.Event
import dev.bti.kdym.ui.components.EventListCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun EventsScreen() {
    val mockEvents = listOf(
        Event(title = "Vision Rally", category = "rally"),
        Event(title = "Kansas Youth Convention", category = "convention"),
        Event(title = "Section 2 Youth Rally", category = "rally"),
        Event(title = "Heartland Senior Camp", category = "camp")
    )

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "KDYM EVENTS",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            
            Text(
                text = "THE YEAR IN ONE FLOW.",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                lineHeight = 36.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Scope Switcher Placeholder
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, onClick = {}, label = { Text("All Events") })
                FilterChip(selected = false, onClick = {}, label = { Text("Camp Schedule") })
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(mockEvents) { event ->
                    EventListCard(event = event)
                }
            }
        }
    }
}
