package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.QuickSandFontFamily

@Composable
fun SegmentedControl(
    segments: List<String>,
    selectedSegment: String,
    onSegmentSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        segments.forEach { segment ->
            val isSelected = segment == selectedSegment
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isSelected) Color.White else Color.White.copy(alpha = 0.1f),
                        RoundedCornerShape(22.dp)
                    )
                    .clickable { onSegmentSelected(segment) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = segment,
                    color = if (isSelected) Color.Black else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}
