package dev.bti.kdym.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCampAccessScreen(
    campId: String = "camp_2026",
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel
) {
    val user by viewModel.user.collectAsState()
    val isPending = user?.accessStatus == "pending"
    
    var selectedRole by remember { mutableStateOf("Camper") }
    val roles = listOf(
        "Camper",
        "Worker",
        "Pastor"
    )
    var isRoleMenuExpanded by remember { mutableStateOf(false) }

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
                        text = isPending.let { if (it) "PENDING" else "REQUEST" },
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "CAMP ACCESS",
                        color = TextSecondary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (isPending) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFF22D3EE).copy(alpha = 0.1f),
                        borderColor = Color(0xFF22D3EE).copy(alpha = 0.2f),
                        contentPadding = 24.dp
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF22D3EE),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Request is Pending",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = QuickSandFontFamily
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "An administrator is reviewing your request for the ${user?.requestedRole ?: "Camper"} role. You'll get access to camp features once approved.",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                fontFamily = QuickSandFontFamily
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onNavigateBack,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f), contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "GOT IT", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                            }
                        }
                    }
                } else {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.Black.copy(alpha = 0.4f),
                        borderColor = Color.Red.copy(alpha = 0.2f),
                        contentPadding = 20.dp
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Camp features require approval",
                                    color = Color(0xFFEF4444),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = QuickSandFontFamily
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your public account stays active either way. This request only unlocks camp-specific features like schedule, tribes, Tribe Wars, and groups.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "REQUESTED ROLE",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        CommandInputField(
                            value = selectedRole,
                            onValueChange = {},
                            placeholder = "Select Role",
                            icon = Icons.Default.Lock,
                            enabled = false,
                            modifier = Modifier.clickable { isRoleMenuExpanded = true }
                        )
                        
                        DropdownMenu(
                            expanded = isRoleMenuExpanded,
                            onDismissRequest = { isRoleMenuExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp))
                        ) {
                            roles.forEach { role ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = role, 
                                            color = Color.White, 
                                            fontFamily = QuickSandFontFamily,
                                            fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                                        ) 
                                    },
                                    onClick = { 
                                        selectedRole = role
                                        isRoleMenuExpanded = false
                                    },
                                    trailingIcon = {
                                        if (selectedRole == role) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF22D3EE))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = {
                            viewModel.requestCampAccess(campId, selectedRole)
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "SUBMIT REQUEST", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                    }
                }
            }
        }
    }
}
