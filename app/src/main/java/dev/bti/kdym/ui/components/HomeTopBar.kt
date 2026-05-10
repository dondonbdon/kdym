package dev.bti.kdym.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.R
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily

@Composable
fun HomeTopBar(
    isCampMode: Boolean = false
) {
    val accentColor = if (isCampMode) Color(0xFF10B981) else RedAccent
    val liveText = if (isCampMode) "CAMP MODE" else "KDYM LIVE"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.white_kdym_logo),
                contentDescription = "KDYM Logo",
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "KDYM",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "OUTPOUR",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(0.5f),
                    lineHeight = 10.sp
                )
            }
        }

        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(accentColor, MaterialTheme.shapes.extraSmall)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = liveText,
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }
    }
}
