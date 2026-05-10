package dev.bti.kdym.ui.screens.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.GroupsViewModel

@Composable
fun ExploreGroupsScreen(
    onNavigateBack: () -> Unit,
    viewModel: GroupsViewModel
) {
    // For now, we use a placeholder list or fetch public groups if repo supports it
    // The requirement is to show "JOIN GROUPS" (Image 1)
    
    // Placeholder groups for demonstration based on Image 1
    val placeholderGroups = listOf(
        AppGroup(id = "1", name = "ANOTHER GROUP", description = "Leadership group", colorHex = "#22D3EE"),
        AppGroup(id = "2", name = "SADASDSDADASDSDADSADASKD...", description = "Sadasdsadasdsadsadaskdjhasdjhsal...", colorHex = "#EF4444"),
        AppGroup(id = "3", name = "DORM DADSD", description = "Asdsadas", colorHex = "#22D3EE")
    )

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EXPLORE",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    Text(
                        text = "GROUPS",
                        color = TextSecondary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "EXPLORE",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "JOIN GROUPS",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(placeholderGroups) { group ->
                    ExploreGroupCard(group = group)
                }
            }
        }
    }
}

@Composable
fun ExploreGroupCard(group: AppGroup) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.White.copy(0.05f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = Color(0xFF22D3EE))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = group.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily,
                        maxLines = 1
                    )
                    Text(
                        text = group.description ?: "",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = RubikFontFamily,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Request to join */ },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = RoundedCornerShape(22.dp)
            ) {
                Text(text = "REQUEST TO JOIN", fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = RubikFontFamily)
            }
        }
    }
}
