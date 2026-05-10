package dev.bti.kdym.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.KDYMEvent
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

import androidx.compose.foundation.clickable

@Composable
fun EventListCard(
    event: KDYMEvent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val accentColor = if (event.isCampEvent) Color(0xFF22D3EE) else RedAccent
    
    GlassCard(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        cornerRadius = 24.dp,
        borderColor = accentColor.copy(alpha = 0.1f)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {

            Column(
                modifier = Modifier
                    .width(60.dp)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val date = event.startDate.toDate()
                val zoned = date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())

                Text(
                    text = zoned.month.name.take(3),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )

                Text(
                    text = zoned.dayOfMonth.toString(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )

                Text(
                    text = zoned.dayOfWeek.name.take(3),
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ─────────────────────────────
            // CONTENT COLUMN (restored full UI)
            // ─────────────────────────────
            Column(modifier = Modifier.weight(1f)) {

                // Category row (CAMP / EVENT etc)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.category.title.uppercase(),
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = RubikFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = event.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily,
                    lineHeight = 28.sp
                )

                // Subtitle (was in your hardcoded version)
                event.subtitle?.let {
                    Text(
                        text = it,
                        color = accentColor.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time + Location row
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = event.timeText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RubikFontFamily
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    event.location?.let {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = it,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RubikFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description preview (like your hardcoded paragraph)
                Text(
                    text = event.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = RubikFontFamily,
                    maxLines = 2
                )
            }
        }
    }
}
