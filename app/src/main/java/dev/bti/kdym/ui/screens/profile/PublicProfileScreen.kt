package dev.bti.kdym.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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

@Composable
fun PublicProfileScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel
) {
    val viewedUser by viewModel.getUserProfile(userId).collectAsState(initial = null)
    val context = LocalContext.current

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

                    Spacer(modifier = Modifier.height(140.dp))
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
