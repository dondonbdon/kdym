package dev.bti.kdym.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.bti.kdym.data.models.GlobalOverlay
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import kotlinx.coroutines.delay

@Composable
fun UrgentOverlay(
    overlay: GlobalOverlay,
    onDismiss: () -> Unit
) {
    var timerSeconds by remember(overlay.id) { mutableIntStateOf(5) }

    LaunchedEffect(overlay.id) {
        timerSeconds = 5
        while (timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }
    }

    Dialog(
        onDismissRequest = { /* Explicitly block dismiss here, handled by button */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false, // Allows the dialog to expand horizontally
            decorFitsSystemWindows = false   // Allows the dialog to draw behind system bars
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background (Now stretches edge-to-edge)
            OutpourBackground(gridAlpha = 0.05f) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.7f)))
            }

            // Content (Keeps padding so it remains readable/clickable)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White.copy(0.05f), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    MappedIcon(
                        iosName = overlay.symbol,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = overlay.subtitle.uppercase(),
                    color = Color(0xFF22D3EE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = overlay.title.uppercase(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = overlay.message,
                    color = Color.White.copy(0.7f),
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = QuickSandFontFamily,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(64.dp))

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
                        text = if (timerSeconds > 0) "PLEASE READ ($timerSeconds)" else overlay.buttonTitle.uppercase(),
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