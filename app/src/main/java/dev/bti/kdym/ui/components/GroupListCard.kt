package dev.bti.kdym.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.AppGroupType
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.ui.utils.TimeUtils

@Composable
fun GroupListCard(
    group: AppGroup,
    modifier: Modifier = Modifier,
    currentUserId: String? = null,
    showChevron: Boolean = true,
    onClick: () -> Unit = {},
) {
    val isTribeGroup = group.type == AppGroupType.tribe

    val color = remember(group.colorHex, isTribeGroup) {
        try {
            if (group.colorHex != null) Color(group.colorHex.toColorInt())
            else Color(0xFF22D3EE)
        } catch (_: Exception) {
            Color(0xFF22D3EE)
        }
    }
    val unreadCount = currentUserId?.let { group.unreadCounts[it] } ?: 0

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
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                MappedIcon(
                    iosName = group.iconName ?: "",
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .background(Color.Red, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = group.name.uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (group.isOfficial || isTribeGroup) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(id = if (isTribeGroup) dev.bti.kdym.R.drawable.ic_tribe_checkmark else dev.bti.kdym.R.drawable.ic_checkmark),
                            contentDescription = null,
                            tint = if (isTribeGroup) color else Color(0xFF22D3EE),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                val subText = when {
                    !group.lastMessageSenderName.isNullOrBlank() -> {
                        val sender = "${group.lastMessageSenderName}: "
                        val content = if (!group.lastMessageText.isNullOrBlank()) {
                            group.lastMessageText
                        } else {
                            "Sent an attachment"
                        }
                        "$sender$content"
                    }
                    !group.lastMessageText.isNullOrBlank() -> {
                        group.lastMessageText
                    }
                    else -> {
                        group.description ?: ""
                    }
                }

                Text(
                    text = subText,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val timeText =
                        TimeUtils.formatRelativeTime(group.lastMessageAt ?: group.createdAt)
                    Text(
                        text = timeText,
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${group.memberCount} MEMBERS",
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily
                    )
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
