package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun ManageGroupsScreen(
    onNavigateBack: () -> Unit,
    onCreateGroup: () -> Unit,
    onEditGroup: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val groups by viewModel.groups.collectAsState()

    OutpourBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                ScreenHeader(
                    onNavigateBack = onNavigateBack,
                    icon = Icons.Default.Forum,
                    title = "GROUPS",
                    subtitle = "Manage official camp channels."
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    if (groups.isEmpty()) {
                        NoGroupsPlaceholder(onCreateGroup)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
                        ) {
                            items(groups) { group ->
                                GroupListItem(group = group, onClick = { onEditGroup(group.id) })
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = onCreateGroup,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 24.dp),
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Group")
            }
        }
    }
}

@Composable
fun NoGroupsPlaceholder(onCreateGroup: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.White.copy(0.05f)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Forum,
                contentDescription = null,
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No groups yet",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = RubikFontFamily
            )
            Text(
                text = "Create official groups for tribes, cabins, leaders, volunteers, prayer teams, or general camp updates.",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontFamily = RubikFontFamily
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCreateGroup,
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
                    text = "CREATE GROUP",
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
        }
    }
}

@Composable
fun GroupListItem(group: AppGroup, onClick: () -> Unit) {
    val typeColor = when (group.type) {
        AppGroupType.tribe -> Color(0xFFEF4444)
        AppGroupType.leadership -> Color(0xFFFBBF24)
        AppGroupType.cabin -> Color(0xFF10B981)
        else -> Color(0xFF22D3EE)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        cornerRadius = 24.dp,
        backgroundColor = Color.White.copy(0.05f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(typeColor.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (group.type) {
                        AppGroupType.tribe -> Icons.Default.Shield
                        AppGroupType.leadership -> Icons.Default.Grade
                        AppGroupType.cabin -> Icons.Default.Home
                        else -> Icons.Default.ChatBubble
                    },
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = group.description ?: group.type.title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = RubikFontFamily,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.People, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                        Text(text = "${group.memberCount} MEMBERS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                    if (group.isOfficial) {
                        Surface(color = Color(0xFFEF4444).copy(0.1f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "OFFICIAL", 
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                color = Color(0xFFEF4444), 
                                fontSize = 8.sp, 
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(0.3f)
            )
        }
    }
}
