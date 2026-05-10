package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun GroupListCard(
    group: AppGroup,
    modifier: Modifier = Modifier,
    showChevron: Boolean = true,
    onClick: () -> Unit = {}
) {
    val icon = when (group.type) {
        AppGroupType.tribe -> Icons.Default.Flag
        else -> Icons.AutoMirrored.Filled.Chat
    }

    val iconBgColor = when (group.type) {
        AppGroupType.tribe -> Color(0xFFEAB308).copy(alpha = 0.1f)
        else -> Color(0xFF22D3EE).copy(alpha = 0.1f)
    }

    val iconTint = when (group.type) {
        AppGroupType.tribe -> Color(0xFFEAB308)
        else -> Color(0xFF22D3EE)
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 24.dp,
        backgroundColor = Color.White.copy(alpha = 0.05f),
        contentPadding = 12.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(iconBgColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    if (group.isOfficial) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "${group.memberCount} MEMBERS",
                            color = Color.White.copy(0.7f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            letterSpacing = 0.5.sp,
                            fontFamily = RubikFontFamily
                        )
                    }
                }
            }

            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun Surface(
    modifier: Modifier = Modifier,
    color: Color,
    shape: androidx.compose.ui.graphics.Shape,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        color = color,
        shape = shape
    ) {
        content()
    }
}
