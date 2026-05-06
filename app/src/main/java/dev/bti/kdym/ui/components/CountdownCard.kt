package dev.bti.kdym.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun CountdownCard(targetDate: Date) {
    var timeLeft by remember { mutableStateOf(calculateTimeLeft(targetDate)) }

    LaunchedEffect(Unit) {
        while (true) {
            timeLeft = calculateTimeLeft(targetDate)
            delay(1000)
        }
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.4f),
        borderColor = Color.White.copy(alpha = 0.05f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF22D3EE),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Countdown to camp",
                    color = Color(0xFF22D3EE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "JUN 1",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = RubikFontFamily
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeComponent(value = timeLeft.days, label = "DAYS")
                Text(text = ":", color = Color(0xFFEF4444), fontSize = 24.sp, fontWeight = FontWeight.Black)
                TimeComponent(value = timeLeft.hours, label = "HRS")
                Text(text = ":", color = Color(0xFFEF4444), fontSize = 24.sp, fontWeight = FontWeight.Black)
                TimeComponent(value = timeLeft.minutes, label = "MIN")
                Text(text = ":", color = Color(0xFFEF4444), fontSize = 24.sp, fontWeight = FontWeight.Black)
                TimeComponent(value = timeLeft.seconds, label = "SEC")
            }
        }
    }
}

@Composable
fun TimeComponent(value: Long, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString().padStart(2, '0'),
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            fontFamily = RubikFontFamily
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            fontFamily = RubikFontFamily
        )
    }
}

data class TimeLeft(val days: Long, val hours: Long, val minutes: Long, val seconds: Long)

fun calculateTimeLeft(target: Date): TimeLeft {
    val diff = target.time - System.currentTimeMillis()
    if (diff <= 0) return TimeLeft(0, 0, 0, 0)
    
    val days = diff / (1000 * 60 * 60 * 24)
    val hours = (diff / (1000 * 60 * 60)) % 24
    val minutes = (diff / (1000 * 60)) % 60
    val seconds = (diff / 1000) % 60
    
    return TimeLeft(days, hours, minutes, seconds)
}
