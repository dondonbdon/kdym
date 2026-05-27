package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AccessStatus
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.data.models.UserRole
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.admin.ManagementFilterTabItem
import dev.bti.kdym.ui.components.admin.PickerBottomSheet
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun UsersScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val tribes by viewModel.tribes.collectAsState()
    val pendingCount by viewModel.pendingUserCount.collectAsState()
    val totalCount by viewModel.totalUserCount.collectAsState()
    val approvedCount by viewModel.approvedUserCount.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("All") }
    val roles = listOf("All", "Camper", "Worker", "Pastor", "Admin")

    val filteredUsers = remember(allUsers, searchQuery, selectedRoleFilter) {
        allUsers.filter { user ->
            val matchesSearch = user.displayName.contains(searchQuery, ignoreCase = true) ||
                    user.email.contains(searchQuery, ignoreCase = true) ||
                    user.username.contains(searchQuery, ignoreCase = true)
            val matchesRole = when (selectedRoleFilter) {
                "Camper" -> user.roleEnum == UserRole.camper
                "Worker" -> user.roleEnum == UserRole.staff
                "Pastor" -> user.roleEnum == UserRole.pastor
                "Admin" -> user.isAdmin
                else -> true
            }
            matchesSearch && matchesRole
        }
    }

    val pendingUsers = remember(filteredUsers) { filteredUsers.filter { it.statusEnum == AccessStatus.pending } }
    val otherUsers = remember(filteredUsers) { filteredUsers.filter { it.statusEnum != AccessStatus.pending } }

    var selectedUser by remember { mutableStateOf<AppUser?>(null) }
    val currentUser by viewModel.appUser.collectAsState()

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
                    onClick = { /* Add user manually? */ },
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
                                .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.People, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "USERS",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = "Approve access, manage roles and keep access clean.",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats Card
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AccessStatBox(count = pendingCount, label = "PENDING", color = Color(0xFFEAB308), modifier = Modifier.weight(1f))
                        AccessStatBox(count = totalCount, label = "USERS", color = Color(0xFF22D3EE), modifier = Modifier.weight(1f))
                        AccessStatBox(count = approvedCount, label = "APPROVED", color = Color(0xFF10B981), modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CommandInputField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search users...",
                        icon = Icons.Default.Search
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Role Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roles.forEach { role ->
                            ManagementFilterTabItem(
                                title = role.uppercase(),
                                isSelected = selectedRoleFilter == role,
                                onClick = { selectedRoleFilter = role },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (pendingUsers.isNotEmpty()) {
                    item {
                        Text(
                            text = "PENDING REQUESTS",
                            color = RedAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                    items(pendingUsers) { user ->
                        UserRow(user = user, onClick = { selectedUser = user })
                    }
                }

                if (otherUsers.isNotEmpty()) {
                    item {
                        Text(
                            text = "ALL USERS",
                            color = Color.White.copy(0.4f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                    
                    items(otherUsers) { user ->
                        UserRow(user = user, onClick = { selectedUser = user })
                    }
                }
            }
        }
        
        if (selectedUser != null) {
            ManageUserDialog(
                user = selectedUser!!,
                tribes = tribes,
                currentUser = currentUser,
                onDismiss = { selectedUser = null },
                onSave = { updatedUser ->
                    viewModel.updateUser(updatedUser.uid, mapOf(
                        "role" to updatedUser.role,
                        "accessStatus" to updatedUser.accessStatus,
                        "isAdmin" to updatedUser.isAdmin,
                        "isLeader" to updatedUser.isLeader,
                        "tribeId" to updatedUser.tribeId
                    ))
                    selectedUser = null
                }
            )
        }
    }
}

@Composable
fun AccessStatBox(count: Int, label: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.height(80.dp),
        backgroundColor = Color.Black.copy(0.3f),
        cornerRadius = 20.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), color = color, fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
            Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontFamily = QuickSandFontFamily)
        }
    }
}

@Composable
fun UserRow(user: AppUser, onClick: () -> Unit) {
    val isDeleted = user.isDeleted

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isDeleted) { onClick() }
            .alpha(if (isDeleted) 0.5f else 1f),
        backgroundColor = Color.White.copy(0.045f),
        cornerRadius = 24.dp,
        contentPadding = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = if (isDeleted) {
                            SolidColor(Color.White.copy(0.1f))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF22D3EE)))
                        },
                        shape = CircleShape
                    )
                    .border(1.dp, Color.White.copy(if (isDeleted) 0.05f else 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials,
                    color = if (isDeleted) TextSecondary else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.width(16.dp))


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = user.displayName,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (isDeleted) TextDecoration.LineThrough else null
                )

                Text(
                    text = user.email,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isDeleted) {
                        StatusBadge(
                            text = "DELETED",
                            color = Color(0xFFEF4444) // RedAccent
                        )
                    } else {
                        StatusBadge(
                            text = user.statusEnum.title.uppercase(),
                            color = if (user.statusEnum == AccessStatus.approved) Color(0xFF22D3EE) else Color.White.copy(0.4f)
                        )
                        StatusBadge(
                            text = user.roleEnum.title.uppercase(),
                            color = Color(0xFFEF4444) // RedAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Trailing Icon
            if (isDeleted) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = "Deleted",
                    tint = Color(0xFFEF4444).copy(0.5f),
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight, // ChevronRight looks cleaner for list items than ArrowForward
                    contentDescription = "View Profile",
                    tint = Color.White.copy(0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontFamily = QuickSandFontFamily
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUserDialog(
    user: AppUser,
    tribes: List<Tribe>,
    currentUser: AppUser?,
    onDismiss: () -> Unit,
    onSave: (AppUser) -> Unit
) {
    var role by remember { mutableStateOf(user.roleEnum) }
    var status by remember { mutableStateOf(user.statusEnum) }
    var isAdmin by remember { mutableStateOf(user.isAdmin) }
    var isLeader by remember { mutableStateOf(user.isLeader) }
    var tribeId by remember { mutableStateOf(user.tribeId) }
    
    var showTribePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111111),
        scrimColor = Color.Black.copy(0.6f)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState())) {
            Text(text = "MANAGE", color = Color(0xFF22D3EE), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontFamily = QuickSandFontFamily)
            Text(text = "USER ACCESS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.White.copy(0.05f)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(56.dp).background(Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF22D3EE))), CircleShape), contentAlignment = Alignment.Center) {
                        Text(text = user.initials, color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp, fontFamily = QuickSandFontFamily)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = user.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = QuickSandFontFamily)
                        Text(text = user.email, color = TextSecondary, fontSize = 14.sp, fontFamily = QuickSandFontFamily)
                        Text(text = user.uid, color = Color.White.copy(0.2f), fontSize = 10.sp, fontFamily = QuickSandFontFamily)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Role & Status", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
            Spacer(modifier = Modifier.height(12.dp))
            
            RoleSelector(
                currentRole = role,
                currentUser = currentUser,
                onRoleSelected = { role = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            // Tribe Selector
            GlassCard(
                modifier = Modifier.fillMaxWidth().clickable { showTribePicker = true },
                backgroundColor = Color.White.copy(0.05f)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "TRIBE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                        Text(text = tribes.find { it.id == tribeId }?.name ?: "NONE", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                    }
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFEAB308))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            PermissionToggle("Admin privileges", isAdmin) { 
                if (currentUser?.isAdmin == true || currentUser?.roleEnum == UserRole.superAdmin) {
                    isAdmin = it 
                }
            }
            PermissionToggle("Leader privileges", isLeader) { 
                if (currentUser?.isLeader == true || currentUser?.isAdmin == true) {
                    isLeader = it 
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton(text = "APPROVE", color = Color(0xFF10B981).copy(0.2f), textColor = Color(0xFF10B981), modifier = Modifier.weight(1f)) {
                    status = AccessStatus.approved
                }
                ActionButton(text = "PUBLIC", color = Color.White.copy(0.1f), textColor = Color.White, modifier = Modifier.weight(1f)) {
                    status = AccessStatus.public
                }
                ActionButton(text = "SUSPEND", color = Color(0xFFEF4444).copy(0.2f), textColor = Color(0xFFEF4444), modifier = Modifier.weight(1f)) {
                    status = AccessStatus.suspended
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { onSave(user.copy(role = role.name, accessStatus = status.name, isAdmin = isAdmin, isLeader = isLeader, tribeId = tribeId)) },
                modifier = Modifier.fillMaxWidth().height(56.dp).navigationBarsPadding(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "SAVE USER", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
            }
        }

        if (showTribePicker) {
            PickerBottomSheet(
                title = "SELECT TRIBE",
                options = listOf("NONE") + tribes.map { it.name },
                selectedOption = tribes.find { it.id == tribeId }?.name ?: "NONE",
                onOptionSelected = { name ->
                    tribeId = if (name == "NONE") null else tribes.find { it.name == name }?.id
                    showTribePicker = false
                },
                onDismiss = { showTribePicker = false }
            )
        }
    }
}

@Composable
fun RoleSelector(
    currentRole: UserRole,
    currentUser: AppUser?,
    onRoleSelected: (UserRole) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        GlassCard(
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            backgroundColor = Color.White.copy(0.05f)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "ROLE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                    Text(text = currentRole.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                }
                Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = Color.White.copy(0.4f))
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF111111)).fillMaxWidth(0.9f)
        ) {
            UserRole.entries.forEach { r ->
                // Basic permission check: can only assign roles if you have higher or equal rank
                // Super Admin can assign anything.
                // Admin can assign anything except Super Admin.
                // Leader can assign Camper, Group Leader, Tribe Leader, Staff, Point Manager, Leader.
                val canAssign = when (currentUser?.roleEnum) {
                    UserRole.superAdmin -> true
                    UserRole.admin -> r != UserRole.superAdmin
                    UserRole.leader -> r != UserRole.superAdmin && r != UserRole.admin
                    else -> currentUser?.isAdmin == true && r != UserRole.superAdmin
                }

                if (canAssign) {
                    DropdownMenuItem(
                        text = { Text(text = r.title, color = Color.White, fontFamily = QuickSandFontFamily) },
                        onClick = { 
                            onRoleSelected(r)
                            expanded = false
                        },
                        trailingIcon = { if (currentRole == r) Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF22D3EE)) else null }
                    )
                }
            }
        }
    }
}


@Composable
fun PermissionToggle(title: String, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = Color.White, fontSize = 16.sp, fontFamily = QuickSandFontFamily)
        Switch(
            checked = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF22D3EE),
                uncheckedThumbColor = Color.White.copy(0.6f),
                uncheckedTrackColor = Color.White.copy(0.1f)
            )
        )
    }
}

@Composable
fun ActionButton(text: String, color: Color, textColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(40.dp).clickable { onClick() },
        color = color,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontFamily = QuickSandFontFamily)
        }
    }
}
