package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.UserRole
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.StandardTopBar
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun ApprovalsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val users by viewModel.allUsers.collectAsState()
    val pendingUsers = users.filter { it.accessStatus == "pending" }
    val otherUsers = users.filter { it.accessStatus != "pending" }

    OutpourBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp)
        ) {
            item {
                StandardTopBar(
                    onNavigateBack = onNavigateBack,
                    icon = Icons.Default.PersonAdd
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "APPROVALS",
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily,
                    lineHeight = 44.sp
                )
                Text(
                    text = "Approve camp access and assign roles.",
                    color = TextSecondary,
                    fontSize = 18.sp,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                if (pendingUsers.isEmpty()) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp,
                        backgroundColor = Color.White.copy(0.05f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "No pending requests",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = RubikFontFamily
                                )
                                Text(
                                    text = "When public users request camp access, they will appear here for admin approval.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = RubikFontFamily
                                )
                            }
                        }
                    }
                }
            }

            items(pendingUsers) { user ->
                UserApprovalCard(
                    user,
                    onApprove = { role -> viewModel.approveUser(user.uid, role) })
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "ALL USERS",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "RECENT ACCOUNTS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(otherUsers) { user ->
                UserListItem(user)
            }
        }
    }
}

@Composable
fun UserApprovalCard(user: AppUser, onApprove: (UserRole) -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 20.dp) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF22D3EE))),
                            CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.initials,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = user.displayName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RubikFontFamily
                    )
                    Text(
                        text = user.email,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = RubikFontFamily
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onApprove(UserRole.camper) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "APPROVE",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = RubikFontFamily
                    )
                }
                OutlinedButton(
                    onClick = { /* Handle reject */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.2f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "REJECT",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = RubikFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: AppUser) {
    GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 16.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RubikFontFamily
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.displayName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = user.email,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = RubikFontFamily
                )
            }
            Surface(
                color = Color.White.copy(0.05f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
            ) {
                Text(
                    text = (user.role ?: "PUBLIC").uppercase(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontFamily = RubikFontFamily
                )
            }
        }
    }
}
