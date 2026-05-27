package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun ManageTribesScreen(
    onNavigateBack: () -> Unit,
    onCreateTribe: () -> Unit,
    onEditTribe: (String) -> Unit,
    onNavigateToTribeDetail: (String) -> Unit,
    viewModel: AdminViewModel
) {
    val tribes by viewModel.tribes.collectAsState()
    val config by viewModel.appConfig.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("ACTIVE", "ARCHIVED", "ALL")

    val filteredTribes = remember(tribes, selectedTab) {
        when (selectedTab) {
            0 -> tribes.filter { it.isActive }
            1 -> tribes.filter { !it.isActive }
            else -> tribes
        }
    }

    var tribeToReset by remember { mutableStateOf<Tribe?>(null) }
    var tribeToArchive by remember { mutableStateOf<Tribe?>(null) }
    var tribeToDelete by remember { mutableStateOf<Tribe?>(null) }

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
                    onClick = onCreateTribe,
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
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "TRIBES",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = "Create, edit, archive, delete, and manage camp tribes.",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Active Camp Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        backgroundColor = Color.Black.copy(0.3f)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF22D3EE).copy(0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.FolderSpecial, contentDescription = null, tint = Color(0xFF22D3EE))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Active Camp",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuickSandFontFamily
                                )
                                Text(
                                    text = config?.activeCampId ?: "Current camp",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = QuickSandFontFamily
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = tribes.count { it.isActive }.toString(),
                                    color = Color(0xFF22D3EE),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = QuickSandFontFamily
                                )
                                Text(
                                    text = "ACTIVE",
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontFamily = QuickSandFontFamily
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Custom Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            TabItem(
                                title = title,
                                isSelected = selectedTab == index,
                                onClick = { selectedTab = index },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(filteredTribes) { tribe ->
                    TribeCardRedesign(
                        tribe = tribe,
                        onClick = { onNavigateToTribeDetail(tribe.id) },
                        onEdit = { onEditTribe(tribe.id) },
                        onResetScore = { tribeToReset = tribe },
                        onArchive = { tribeToArchive = tribe },
                        onDelete = { tribeToDelete = tribe }
                    )
                }
            }
        }

        // Confirmation Dialogs
        tribeToReset?.let { tribe ->
            AlertDialog(
                onDismissRequest = { tribeToReset = null },
                title = { Text("Reset Score?", color = Color.White) },
                text = { Text("This will set total points for ${tribe.name} to 0. This cannot be undone.", color = Color.White.copy(0.7f)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateTribe(tribe.copy(totalPoints = 0))
                        tribeToReset = null
                    }) {
                        Text("RESET", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tribeToReset = null }) {
                        Text("CANCEL", color = Color.White.copy(0.6f))
                    }
                },
                containerColor = Color(0xFF111111),
                shape = RoundedCornerShape(24.dp)
            )
        }

        tribeToArchive?.let { tribe ->
            AlertDialog(
                onDismissRequest = { tribeToArchive = null },
                title = { Text("Archive Tribe?", color = Color.White) },
                text = { Text("This will hide ${tribe.name} from active lists.", color = Color.White.copy(0.7f)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateTribe(tribe.copy(isActive = false))
                        tribeToArchive = null
                    }) {
                        Text("ARCHIVE", color = Color(0xFFEAB308), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tribeToArchive = null }) {
                        Text("CANCEL", color = Color.White.copy(0.6f))
                    }
                },
                containerColor = Color(0xFF111111),
                shape = RoundedCornerShape(24.dp)
            )
        }

        tribeToDelete?.let { tribe ->
            AlertDialog(
                onDismissRequest = { tribeToDelete = null },
                title = { Text("Delete Tribe?", color = Color.White) },
                text = { Text("Are you sure you want to permanently delete ${tribe.name}?", color = Color.White.copy(0.7f)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.updateTribe(tribe.copy(isActive = false)) // Mocking delete as deactivate for now
                        tribeToDelete = null
                    }) {
                        Text("DELETE", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tribeToDelete = null }) {
                        Text("CANCEL", color = Color.White.copy(0.6f))
                    }
                },
                containerColor = Color(0xFF111111),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun TabItem(title: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White else Color.White.copy(0.05f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = if (isSelected) Color.Black else TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}

@Composable
fun TribeCardRedesign(
    tribe: Tribe,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onResetScore: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    val color = try {
        Color(tribe.colorHex.toColorInt())
    } catch (_: Exception) {
        Color(0xFFEF4444)
    }
    
    var showMenu by remember { mutableStateOf(false) }
    
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
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
                    .background(color.copy(0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tribe.name.uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    if (!tribe.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color.White.copy(0.1f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "ARCHIVED",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = Color.White.copy(0.4f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = tribe.subtitle ?: "Camp tribe",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TribeStatItem(count = tribe.memberIds.size, label = "MEMBERS")
                    TribeStatItem(count = tribe.leaderIds.size, label = "LEADERS")
                    TribeStatItem(count = tribe.totalPoints, label = "POINTS")
                }
            }
            
            Box {
                Surface(
                    onClick = { showMenu = true },
                    color = Color.White.copy(0.05f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(20.dp))
                    }
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF111111))
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Tribe", color = Color.White) },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White.copy(0.6f)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Reset Score", color = Color.White) },
                        onClick = { showMenu = false; onResetScore() },
                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White.copy(0.6f)) }
                    )
                    if (tribe.isActive) {
                        DropdownMenuItem(
                            text = { Text("Archive Tribe", color = Color.White) },
                            onClick = { showMenu = false; onArchive() },
                            leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null, tint = Color.White.copy(0.6f)) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Delete Tribe", color = Color(0xFFEF4444)) },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444).copy(0.6f)) }
                    )
                }
            }
        }
    }
}

@Composable
fun TribeStatItem(count: Int, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 10.sp,
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
