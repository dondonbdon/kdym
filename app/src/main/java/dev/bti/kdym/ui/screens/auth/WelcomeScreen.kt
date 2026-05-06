package dev.bti.kdym.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.GlitchText
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToPlayPreview: () -> Unit
) {
    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Logo and Sign In link
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.white_kdym_logo),
                    contentDescription = "KDYM Logo",
                    modifier = Modifier.size(48.dp)
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "SIGN IN",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Theme Badge
            Surface(
                color = RedAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RedAccent.copy(alpha = 0.3f)),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment, // Using fire icon
                        contentDescription = null,
                        tint = RedAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2026 DISTRICT THEME",
                        fontFamily = RubikFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "HEARTLAND",
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Box(modifier = Modifier.align(Alignment.Start)) {
                GlitchText(text = "OUTPOUR")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "The digital home for Kansas District Youth Ministries — built for camp, tribe wars, events, Play media, announcements, and the move of God across this generation.",
                color = TextSecondary,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = RubikFontFamily,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Action Buttons
            Button(
                onClick = onNavigateToSignUp,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_profile_filled), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "CREATE ACCOUNT", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNavigateToPlayPreview,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f), contentColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_play), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "OPEN PLAY PREVIEW", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Info Card
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 32.dp, backgroundColor = Color.Black.copy(alpha = 0.3f)) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "KDYM LIVE",
                        color = Color(0xFF22D3EE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = RubikFontFamily
                    )
                    Text(
                        text = "Camp-ready. Year-round useful.",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoRow(label = "EVENTS", value = "RALLIES, CONVENTION, CAMP")
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(label = "CAMP", value = "SCHEDULE, TRIBES, GROUPS")
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(label = "PLAY", value = "VIDEOS, RECAPS, AUDIO")
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontFamily = RubikFontFamily,
                modifier = Modifier.width(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
        }
    }
}
