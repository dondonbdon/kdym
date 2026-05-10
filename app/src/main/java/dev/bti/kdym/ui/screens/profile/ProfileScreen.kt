package dev.bti.kdym.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
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
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ScreenHeader
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNotificationPrefs: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()
    
    var displayName by remember(user) { mutableStateOf(user?.displayName ?: "") }
    var phoneNumber by remember(user) { mutableStateOf(user?.phoneNumber ?: "") }
    var bio by remember(user) { mutableStateOf(user?.bio ?: "") }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.Person,
                iconColor = Color(0xFF22D3EE),
                title = "PROFILE",
                subtitle = "Your KDYM identity and preferences."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Profile Card with Photo
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 32.dp, backgroundColor = Color.Black.copy(0.3f)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF22D3EE))), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = user?.initials ?: "KM", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = user?.displayName ?: "KDYM Member", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                        Text(text = user?.email ?: "don@don.don", color = TextSecondary, fontSize = 14.sp, fontFamily = RubikFontFamily)
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.height(32.dp).clickable { /* Change Photo */ },
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(text = "CHANGE PHOTO", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                CommandInputField(value = displayName, onValueChange = { displayName = it }, placeholder = "Full Name", icon = Icons.Default.Person)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = phoneNumber, onValueChange = { phoneNumber = it }, placeholder = "Phone Number", icon = Icons.Default.Phone)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = bio, onValueChange = { bio = it }, placeholder = "Bio", icon = Icons.Default.Notes)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Account Stats Card
                GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.Black.copy(0.3f)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        AccountRow(label = "ROLE", value = user?.roleEnum?.title ?: "Public")
                        Spacer(modifier = Modifier.height(8.dp))
                        AccountRow(label = "ACCESS", value = user?.statusEnum?.title ?: "Public Account")
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Notification Prefs Shortcut
                GlassCard(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToNotificationPrefs() },
                    backgroundColor = Color.Black.copy(0.3f),
                    contentPadding = 16.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(Color(0xFF22D3EE).copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Notification Preferences", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                            Text(text = "Choose what KDYM can alert you about.", color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White.copy(0.3f), modifier = Modifier.size(20.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { viewModel.updateProfile(displayName, phoneNumber, bio) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = CircleShape
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "SAVE PROFILE", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                    }
                }
                
                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}

@Composable
fun AccountRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontFamily = RubikFontFamily)
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
    }
}
