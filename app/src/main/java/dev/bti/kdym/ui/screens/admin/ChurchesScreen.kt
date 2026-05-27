package dev.bti.kdym.ui.screens.admin

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.Church
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.admin.ManagementEmptyStateCard
import dev.bti.kdym.ui.components.admin.ManagementFilterTabItem
import dev.bti.kdym.ui.components.admin.ManagementSectionHeader
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun ChurchesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChurchDetail: (String) -> Unit,
    onAddChurch: () -> Unit,
    onEditChurch: (String) -> Unit,
    onManagePastor: (String) -> Unit,
    adminViewModel: AdminViewModel,
    mainViewModel: MainViewModel
) {
    val churches by mainViewModel.churches.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Active") }
    val filters = listOf("Active", "Inactive", "All")

    val filteredChurches = remember(churches, searchQuery, selectedFilter) {
        churches.filter { 
            val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || 
                    it.city.contains(searchQuery, ignoreCase = true) ||
                    it.pastorName.contains(searchQuery, ignoreCase = true)
            // Assuming we have an isActive field in the DB eventually, for now using dummy logic
            val matchesFilter = when(selectedFilter) {
                "Active" -> true // it.isActive
                "Inactive" -> false // !it.isActive
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    var isSyncing by remember { mutableStateOf(false) }
    val rotation = rememberInfiniteTransition(label = "rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

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
                    onClick = onAddChurch,
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF22D3EE).copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "CHURCHES",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = "District church directory and assignments.",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { 
                            isSyncing = true
                            // Mock refresh
                            // viewModel.refresh()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync, 
                            contentDescription = null, 
                            modifier = Modifier.size(20.dp).rotate(if (isSyncing) rotation.value else 0f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "SYNC DIRECTORY", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CommandInputField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search church, city, pastor, address",
                        icon = Icons.Default.Search
                    )
                }
                
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ChurchStatBox(count = churches.size, label = "CHURCHES", modifier = Modifier.weight(1f))
                        ChurchStatBox(count = 0, label = "CLAIMS", icon = Icons.Default.Key, modifier = Modifier.weight(1f))
                        ChurchStatBox(count = 0, label = "MEMBERS", icon = Icons.Default.Groups, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    ManagementSectionHeader(title = "PASTORS", subtitle = "MANAGEMENT")
                    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.Black.copy(0.3f)) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).background(Color(0xFFEAB308).copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.PersonSearch, contentDescription = null, tint = Color(0xFFEAB308))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = "Pastor Assignments", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                                Text(text = "0 linked • 0 pending claims", color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                            }
                        }
                    }
                }

                item {
                    ManagementSectionHeader(title = "CLAIMS", subtitle = "PASTORS")
                    ManagementEmptyStateCard(text = "No pending pastor claims.")
                }

                item {
                    ManagementSectionHeader(title = "DIRECTORY", subtitle = "ACTIVE")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filters.forEach { filter ->
                            ManagementFilterTabItem(
                                title = filter.uppercase(),
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                items(filteredChurches) { church ->
                    ChurchRowRedesign(
                        church = church,
                        onClick = { onNavigateToChurchDetail(Uri.encode(church.id)) },
                        onEdit = { onEditChurch(Uri.encode(church.id)) },
                        onManagePastor = { onManagePastor(Uri.encode(church.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChurchStatBox(count: Int, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Business, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.height(100.dp), backgroundColor = Color.Black.copy(0.3f)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(16.dp))
            Column {
                Text(text = count.toString(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                Text(
                    text = label, 
                    color = TextSecondary, 
                    fontSize = 9.sp, 
                    fontWeight = FontWeight.Black, 
                    letterSpacing = 1.sp, 
                    fontFamily = QuickSandFontFamily,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@Composable
fun ChurchRowRedesign(church: Church, onClick: () -> Unit, onEdit: () -> Unit, onManagePastor: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { showMenu = true }
                )
            },
        backgroundColor = Color.Black.copy(0.3f),
        contentPadding = 16.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color.White.copy(0.05f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color.White.copy(0.4f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = church.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = QuickSandFontFamily)
                Text(text = "Pastor ${church.pastorName} • ${church.city}, ${church.state}", color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
            }
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(0.2f))
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(Color(0xFF111111))
        ) {
            DropdownMenuItem(
                text = { Text("Edit Church", color = Color.White) },
                onClick = { showMenu = false; onEdit() },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White.copy(0.6f)) }
            )
            DropdownMenuItem(
                text = { Text("Manage Pastor", color = Color.White) },
                onClick = { showMenu = false; onManagePastor() },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White.copy(0.6f)) }
            )
        }
    }
}
