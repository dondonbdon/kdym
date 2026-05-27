package dev.bti.kdym.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.QuickSandFontFamily

@Composable
fun VerseCard(
    accentColor: Color,
    verse: String? = null,
    reference: String? = null,
    tagline: String? = null
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        backgroundColor = Color.Black.copy(alpha = 0.75f),
        borderColor = Color.White.copy(alpha = 0.05f),
        contentPadding = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reference?.uppercase() ?: "JOEL 2:28",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (verse != null) "\"$verse\"" else "\"I will pour out my spirit upon all flesh.\"",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 32.sp,
                fontFamily = QuickSandFontFamily,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )

            if (tagline != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = tagline.uppercase(),
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}