package dev.bti.kdym.ui.screens.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.Group
import dev.bti.kdym.ui.components.GroupListCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RubikFontFamily

@Composable
fun GroupsScreen() {
    val mockGroups = listOf(
        Group(name = "Tribe Wars", description = "Official competition updates", isOfficial = true),
        Group(name = "Announcements", description = "District-wide news", isOfficial = true),
        Group(name = "Senior Camp 2026", description = "Private group for campers", isOfficial = false)
    )

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "YOUR CHANNELS",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            
            Text(
                text = "GROUPS",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(mockGroups) { group ->
                    GroupListCard(group = group)
                }
            }
        }
    }
}
