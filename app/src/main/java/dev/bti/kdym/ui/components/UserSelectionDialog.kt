package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun UserSelectionDialog(
    title: String,
    users: List<AppUser>,
    selectedUserIds: Set<String>,
    multiSelect: Boolean = true,
    onDismiss: () -> Unit,
    onConfirmed: (Set<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentSelection by remember { mutableStateOf(selectedUserIds) }

    val filteredUsers = users.filter {
        it.displayName.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            OutpourBackground {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SELECT",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = title,
                                color = TextSecondary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CommandInputField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Search users...",
                        icon = Icons.Default.Search
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(filteredUsers) { user ->
                            val isSelected = currentSelection.contains(user.uid)
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (multiSelect) {
                                            currentSelection = if (isSelected) {
                                                currentSelection - user.uid
                                            } else {
                                                currentSelection + user.uid
                                            }
                                        } else {
                                            currentSelection = setOf(user.uid)
                                        }
                                    },
                                cornerRadius = 24.dp,
                                backgroundColor = if (isSelected) Color.White.copy(0.1f) else Color.White.copy(0.05f),
                                borderColor = if (isSelected) Color(0xFF22D3EE).copy(0.5f) else Color.White.copy(0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(Color.White.copy(0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = user.initials, color = Color.White, fontWeight = FontWeight.Black)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = user.displayName, color = Color.White, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                                        Text(text = user.email, color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                                    }
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF22D3EE))
                                    }
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.padding(vertical = 16.dp).navigationBarsPadding()) {
                        Button(
                            onClick = { onConfirmed(currentSelection) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = CircleShape,
                            enabled = currentSelection.isNotEmpty()
                        ) {
                            Text(
                                text = if (multiSelect) "ADD SELECTED (${currentSelection.size})" else "SELECT USER",
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}
