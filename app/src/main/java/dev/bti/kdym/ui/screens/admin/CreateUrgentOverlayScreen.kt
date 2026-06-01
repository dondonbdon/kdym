package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.map
import dev.bti.kdym.data.models.GlobalOverlay
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.MappedIcon
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.ToggleRow
import dev.bti.kdym.ui.screens.groups.StyledTextField
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import java.util.UUID

@Composable
fun CreateUrgentOverlayScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    val currentOverlay by viewModel.appConfig.map { null }.collectAsState(initial = null) // We'll just create new ones for now
    
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var buttonTitle by remember { mutableStateOf("") }
    var targetEmails by remember { mutableStateOf("") }
    
    var isVisibleNow by remember { mutableStateOf(false) }
    var excludeAdmins by remember { mutableStateOf(false) }
    var campAccessOnly by remember { mutableStateOf(false) }
    var selectedSymbol by remember { mutableStateOf("megaphone.fill") }

    val symbols = listOf(
        "megaphone.fill",
        "bubble.left.and.bubble.right.fill",
        "flame.fill",
        "sparkles",
        "bell.fill",
        "calendar.badge.exclamationmark"
    )

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
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
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Comment, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "URGENT OVERLAY",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "A full-screen message that appears over the app.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White.copy(0.02f)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        StyledTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = "Title",
                            leadingIcon = Icons.Default.Title,
                            iconColor = Color(0xFF22D3EE)
                        )
                        StyledTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            placeholder = "SubTitle",
                            leadingIcon = Icons.AutoMirrored.Filled.Subject,
                            iconColor = Color(0xFF22D3EE)
                        )
                        
                        Text(text = "MESSAGE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                        
                        StyledTextField(
                            value = message,
                            onValueChange = { message = it },
                            placeholder = "Message",
                            leadingIcon = Icons.AutoMirrored.Filled.Message,
                            iconColor = Color(0xFF22D3EE),
                            singleLine = false
                        )

                        StyledTextField(
                            value = buttonTitle,
                            onValueChange = { buttonTitle = it },
                            placeholder = "Button Text (Default: Got It)",
                            leadingIcon = Icons.Default.TouchApp,
                            iconColor = Color(0xFF22D3EE)
                        )

                        StyledTextField(
                            value = targetEmails,
                            onValueChange = { targetEmails = it },
                            placeholder = "Target emails (Comma separated.)",
                            leadingIcon = Icons.Default.Email,
                            iconColor = Color(0xFF22D3EE)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "DELIVERY",
                    color = Color(0xFF22D3EE),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White.copy(0.02f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ToggleRow(title = "Visible now", checked = isVisibleNow, onCheckedChange = { isVisibleNow = it })
                        ToggleRow(title = "Exclude admins", checked = excludeAdmins, onCheckedChange = { excludeAdmins = it })
                        ToggleRow(title = "Only approved camp accounts", checked = campAccessOnly, onCheckedChange = { campAccessOnly = it })
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            symbols.forEach { symbol ->
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (selectedSymbol == symbol) Color(0xFF22D3EE) else Color.White.copy(0.05f),
                                            CircleShape
                                        )
                                        .clickable { selectedSymbol = symbol },
                                    contentAlignment = Alignment.Center
                                ) {
                                    MappedIcon(
                                        iosName = symbol,
                                        tint = if (selectedSymbol == symbol) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val overlay = GlobalOverlay(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            subtitle = subtitle,
                            message = message,
                            buttonTitle = buttonTitle,
                            symbol = selectedSymbol,
                            isActive = isVisibleNow,
                            excludeAdmins = excludeAdmins,
                            campAccessOnly = campAccessOnly,
                            targetEmails = targetEmails.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            updatedAt = Timestamp.now(),
                            senderId = viewModel.appUser.value?.uid ?: ""
                        )
                        viewModel.updateGlobalOverlay(overlay)
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = CircleShape
                ) {
                    Text(text = "PUBLISH OVERLAY", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
