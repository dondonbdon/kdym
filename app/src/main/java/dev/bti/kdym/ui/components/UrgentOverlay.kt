package dev.bti.kdym.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.bti.kdym.data.models.UrgentOverlayConfig
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun UrgentOverlay(
    config: UrgentOverlayConfig,
    onDismiss: () -> Unit
) {
    var timerSeconds by remember(config) { mutableIntStateOf(config.minViewTimeSeconds) }
    
    LaunchedEffect(config) {
        while (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }
    }

    Popup(
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Dreamy Background
            OutpourBackground(gridAlpha = 0.02f) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = config.title.uppercase(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                
                Text(
                    text = config.subtitle,
                    color = Color(0xFF22D3EE),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 32.dp,
                    backgroundColor = Color.White.copy(0.05f)
                ) {
                    Text(
                        text = config.message,
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontFamily = QuickSandFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = onDismiss,
                    enabled = timerSeconds <= 0,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White.copy(0.3f),
                        disabledContentColor = Color.Black.copy(0.5f)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        text = if (timerSeconds > 0) "PLEASE READ ($timerSeconds)" else config.buttonLabel.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
