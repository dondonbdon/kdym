package dev.bti.kdym.ui.components.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun ManagementSectionHeader(title: String, subtitle: String) {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = subtitle,
            color = Color(0xFFEF4444),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            fontFamily = QuickSandFontFamily
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontFamily = QuickSandFontFamily
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ManagementEmptyStateCard(text: String) {
    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.White.copy(0.05f)) {
        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text(text = text, color = TextSecondary, fontSize = 14.sp, fontFamily = QuickSandFontFamily)
        }
    }
}

@Composable
fun ManagementFilterTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White else Color.White.copy(0.05f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = if (isSelected) Color.Black else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerBottomSheet(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111111),
        scrimColor = Color.Black.copy(0.6f)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
            Text(text = title, color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontFamily = QuickSandFontFamily)
            Spacer(modifier = Modifier.height(16.dp))
            options.forEach { option ->
                val isSelected = option == selectedOption
                Surface(
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    color = if (isSelected) Color.White.copy(0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = option, color = if (isSelected) Color.White else TextSecondary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontFamily = QuickSandFontFamily)
                        if (isSelected) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF22D3EE))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
