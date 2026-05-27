package dev.bti.kdym.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.CyanAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import kotlinx.coroutines.delay

@Composable
fun PostAuthWelcomeScreen(
    name: String,
    onContinue: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
        delay(2500) // Show for 2.5 seconds
        onContinue()
    }

    OutpourBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(1000)) + scaleIn(tween(1000), initialScale = 0.8f),
                exit = fadeOut(tween(500))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(CyanAccent.copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.white_kdym_logo),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "WELCOME BACK",
                        color = CyanAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = name.uppercase(),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your digital home for movement is ready.",
                        color = Color.White.copy(0.6f),
                        fontSize = 16.sp,
                        fontFamily = QuickSandFontFamily,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    CircularProgressIndicator(
                        color = CyanAccent,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
