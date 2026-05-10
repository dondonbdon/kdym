package dev.bti.kdym.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.CountdownCard
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.GlitchText
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import java.util.Date

@Composable
fun HeroCard(campDate: Date) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        contentPadding = 24.dp
    ) {
        Column {
            Surface(
                color = RedAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HEARTLAND",
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color.White
            )

            GlitchText(text = "OUTPOUR")

            Text(
                text = "YOUTH CAMP",
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            CountdownCard(targetDate = campDate)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillStat(label = "DATES", value = "JUN 1-4", modifier = Modifier.weight(1f))
                PillStat(label = "PLACE", value = "TABOR", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PillStat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}
