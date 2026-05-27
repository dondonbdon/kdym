package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.MappedIcon
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.ui.theme.toColor
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun ManageGroupsScreen(
    onNavigateBack: () -> Unit,
    onCreateGroup: () -> Unit,
    onManageGroup: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val groups by viewModel.groups.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredGroups = groups.filter { it.name.contains(searchQuery, ignoreCase = true) }

    val activeCount = remember(groups) { groups.count { it.isActive } }
    val publicCount = remember(groups) { groups.count { it.isPublic } }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "BACK", fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                }

                Surface(
                    onClick = onCreateGroup,
                    color = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 140.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "MANAGE",
                                color = Color(0xFFEF4444),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = "GROUPS",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Create channels, assign people, control permissions, open chat, and archive groups from one command screen.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Search Bar
                    Surface(
                        color = Color.White.copy(0.05f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontFamily = QuickSandFontFamily),
                                cursorBrush = SolidColor(Color.White),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(text = "Search groups", color = TextSecondary, fontSize = 15.sp, fontFamily = QuickSandFontFamily)
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Stat Boxes
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GroupStatBox(count = activeCount, label = "ACTIVE", modifier = Modifier.weight(1f))
                        GroupStatBox(count = publicCount, label = "PUBLIC", modifier = Modifier.weight(1f))
                        GroupStatBox(count = 0, label = "LOCKED", modifier = Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }

                items(filteredGroups) { group ->
                    GroupCardRedesign(group = group, onClick = { onManageGroup(group.id) })
                }
            }
        }
    }
}

@Composable
fun GroupStatBox(count: Int, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(64.dp),
        color = Color.White.copy(0.05f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}

@Composable
fun GroupCardRedesign(group: AppGroup, onClick: () -> Unit) {
    val typeColor = group.colorHex?.toColor() ?: when (group.type) {
        AppGroupType.tribe -> Color(0xFF22D3EE)
        AppGroupType.leadership -> Color(0xFFFBBF24)
        else -> Color(0xFFEF4444)
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(typeColor.copy(0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (group.iconName != null) {
                    MappedIcon(
                        iosName = group.iconName,
                        tint = typeColor,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = when (group.type) {
                            AppGroupType.tribe -> Icons.Default.Shield
                            AppGroupType.leadership -> Icons.Default.Grade
                            else -> Icons.Default.ChatBubble
                        },
                        contentDescription = null,
                        tint = typeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    if (group.isOfficial) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(14.dp))
                    }
                }
                Text(
                    text = group.description ?: group.type.title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = group.type.title.uppercase(),
                        color = typeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${group.memberCount} MEMBERS",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "PRIVATE",
                        color = Color(0xFFEAB308),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                }
            }
            
            Surface(
                onClick = onClick,
                color = Color.White.copy(0.05f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
