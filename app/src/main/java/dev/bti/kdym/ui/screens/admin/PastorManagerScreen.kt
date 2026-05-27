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
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun PastorManagerScreen(
    churchId: String,
    onNavigateBack: () -> Unit,
    adminViewModel: AdminViewModel,
    mainViewModel: MainViewModel
) {
    val churches by mainViewModel.churches.collectAsState()
    val church = remember(churchId, churches) { churches.find { it.id == churchId } }
    
    val allUsers by adminViewModel.allUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val searchedUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else allUsers.filter { 
            it.displayName.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true) ||
            it.phoneNumber?.contains(searchQuery) == true
        }
    }

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
                
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "MANAGE PASTOR",
                    color = Color(0xFFEAB308),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = church?.name ?: "CHURCH",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Current Pastor", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Text(text = church?.pastorName ?: "No pastor assigned", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                CommandInputField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search user by name, email, username, phone",
                    icon = Icons.Default.Search
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* Trigger search if needed, but remember handles it */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.PersonSearch, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "SEARCH USERS", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(searchedUsers) { user ->
                        UserPickItem(user = user, onClick = {
                            // adminViewModel.assignPastor(churchId, user.uid, user.displayName)
                            onNavigateBack()
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun UserPickItem(user: AppUser, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        backgroundColor = Color.White.copy(0.05f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color.White.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Text(text = user.initials, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = user.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = user.email, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}
