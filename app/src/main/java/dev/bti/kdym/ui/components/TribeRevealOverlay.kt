package dev.bti.kdym.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.ui.components.GlitchText
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import androidx.core.graphics.toColorInt

@Composable
fun TribeRevealOverlay(
    tribe: Tribe,
    tribeGroupId: String?,
    onDismiss: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val tribeColor = remember(tribe.colorHex) {
        try { Color(tribe.colorHex.toColorInt()) } catch (e: Exception) { Color(0xFF22D3EE) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(0.95f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(tribeColor.copy(0.2f), Color.Transparent),
                        radius = 1000f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "YOUR TRIBE HAS BEEN REVEALED",
                    color = tribeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = QuickSandFontFamily
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(tribeColor.copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        MappedIcon(
                            iosName = tribe.iconName ?: "shield.fill",
                            tint = tribeColor,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                Spacer(modifier = Modifier.height(32.dp))

                GlitchText(text = tribe.name.uppercase(), fontSize = 48.sp)
                
                tribe.subtitle?.let {
                    Text(
                        text = it,
                        color = Color.White.copy(0.7f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White.copy(0.05f),
                    cornerRadius = 24.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Welcome to the family! You've been drafted into ${tribe.name}. Connect with your teammates in the tribe chat.",
                            color = Color.White,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = QuickSandFontFamily,
                            lineHeight = 20.sp
                        )
                        
                        if (tribeGroupId != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = { 
                                    onNavigateToChat(tribeGroupId)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = tribeColor, contentColor = Color.Black),
                                shape = CircleShape
                            ) {
                                Icon(imageVector = Icons.Default.Forum, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "JOIN TRIBE CHAT", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(onClick = onDismiss) {
                    Text(text = "DISMISS", color = Color.White.copy(0.4f), fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                }
            }
        }
    }
}
