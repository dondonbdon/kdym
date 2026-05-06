package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
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
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun GroupListCard(
    group: AppGroup,
    modifier: Modifier = Modifier,
    showChevron: Boolean = true
) {
    val icon = when (group.type) {
        AppGroupType.tribe -> Icons.Default.Flag
        AppGroupType.general -> Icons.AutoMirrored.Filled.Chat // announcement is not a group type in AppGroupType enum from swift, but let's check
        else -> Icons.AutoMirrored.Filled.Chat
    }

    val iconBgColor = when (group.type) {
        AppGroupType.tribe -> Color(0xFFEAB308).copy(alpha = 0.15f)
        else -> Color(0xFF22D3EE).copy(alpha = 0.1f)
    }

    val iconTint = when (group.type) {
        AppGroupType.tribe -> Color(0xFFEAB308)
        else -> Color(0xFF22D3EE)
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        contentPadding = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBgColor, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.name.uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    if (group.isOfficial) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_verified),
                            contentDescription = null,
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (!group.description.isNullOrEmpty()) {
                    Text(
                        text = group.description,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = RubikFontFamily,
                        maxLines = 2
                    )
                }

                if (group.type != AppGroupType.tribe) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${group.type.title.uppercase()}  •  ${group.memberIds.size} MEMBERS",
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontFamily = RubikFontFamily
                    )
                }
            }

            if (showChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
