package dev.bti.kdym.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.UserRole
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel
import androidx.core.net.toUri
import androidx.compose.material.icons.filled.Report
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PublicProfileScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel
) {
    val viewedUser by viewModel.getUserProfile(userId).collectAsState(initial = null)
    val currentUser by viewModel.user.collectAsState()
    val context = LocalContext.current
    
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("") }
    var reportDetails by remember { mutableStateOf("") }
    var isSubmittingReport by remember { mutableStateOf(false) }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header with Back button
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

            if (viewedUser == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                val user = viewedUser!!
                val isPublic = user.roleEnum.ordinal > UserRole.staff.ordinal && user.roleEnum.ordinal < UserRole.admin.ordinal

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    // Profile Header
                    PublicProfileHeader(user = user)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bio Section
                    if (!user.bio.isNullOrBlank()) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 24.dp,
                            backgroundColor = Color.Black.copy(0.3f)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "ABOUT",
                                    color = Color(0xFF22D3EE),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    fontFamily = QuickSandFontFamily
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = user.bio,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontFamily = QuickSandFontFamily,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }


                    if (isPublic) {
                        if (!user.phoneNumber.isNullOrBlank()) {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                cornerRadius = 24.dp,
                                backgroundColor = Color.Black.copy(0.3f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "PHONE NUMBER",
                                            color = Color(0xFF22D3EE),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp,
                                            fontFamily = QuickSandFontFamily
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = user.phoneNumber,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = QuickSandFontFamily
                                        )
                                    }
                                    
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL,
                                                "tel:${user.phoneNumber}".toUri())
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                                    }
                                }
                            }

                        }
                    }

                    if (user.uid != currentUser?.uid) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { showReportDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black.copy(0.3f),
                                contentColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(0.3f))
                        ) {
                            Icon(imageVector = Icons.Default.Report, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "REPORT USER",
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(140.dp))
                }
            }
        }
    }

    if (showReportDialog && viewedUser != null && currentUser != null) {
        Dialog(
            onDismissRequest = { if (!isSubmittingReport) showReportDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF1A1A1A),
                    cornerRadius = 32.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "REPORT ${viewedUser!!.displayName.uppercase()}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Help us understand what's happening. Your report is confidential.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        val reasons = listOf("Harassment", "Spam", "Inappropriate Profile", "Other")
                        reasons.forEach { reason ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { reportReason = reason }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = reportReason == reason,
                                    onClick = { reportReason = reason },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFEF4444))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = reason, color = Color.White, fontFamily = QuickSandFontFamily)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextField(
                            value = reportDetails,
                            onValueChange = { reportDetails = it },
                            placeholder = { Text("Additional details...", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(0.05f),
                                unfocusedContainerColor = Color.White.copy(0.05f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { showReportDialog = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f))
                            ) {
                                Text("CANCEL", fontFamily = QuickSandFontFamily)
                            }
                            Button(
                                onClick = {
                                    isSubmittingReport = true
                                    viewModel.submitUserReport(
                                        targetUser = viewedUser!!,
                                        reporter = currentUser!!,
                                        reason = reportReason,
                                        details = reportDetails,
                                        onSuccess = {
                                            isSubmittingReport = false
                                            showReportDialog = false
                                            viewModel.showFeedback("User reported successfully.")
                                        }
                                    )
                                },
                                enabled = reportReason.isNotBlank() && !isSubmittingReport,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                if (isSubmittingReport) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Text("SUBMIT", fontFamily = QuickSandFontFamily)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PublicProfileHeader(user: AppUser) {
    val isLeader = user.isAdmin || user.isLeader || user.roleEnum.isLeader
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    Brush.linearGradient(
                        if (isLeader) listOf(Color(0xFFEF4444), Color(0xFF22D3EE))
                        else listOf(Color.White.copy(0.1f), Color.White.copy(0.05f))
                    ),
                    CircleShape
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (user.photoURL != null) {
                AsyncImage(
                    model = user.photoURL,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = user.initials,
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = user.displayName,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily,
            textAlign = TextAlign.Center
        )
        
        Surface(
            color = if (isLeader) Color(0xFFEF4444).copy(0.2f) else Color.White.copy(0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = user.roleEnum.title.uppercase(),
                color = if (isLeader) Color(0xFFEF4444) else Color.White.copy(0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                fontFamily = QuickSandFontFamily
            )
        }
    }
}
