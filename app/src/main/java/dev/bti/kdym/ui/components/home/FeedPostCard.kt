package dev.bti.kdym.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.data.models.FeedPostPriority
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import dev.bti.kdym.R

@Composable
fun FeedPostCard(
    post: FeedPost,
    userReaction: String?,
    onReactionClick: (postId: String, reaction: String) -> Unit,
    onClick: () -> Unit = {}
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (post.isPinned) {
                    Surface(
                        color = RedAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = null,
                                tint = RedAccent,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PINNED",
                                color = RedAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }
                }

                Surface(
                    color = (if (post.priority == FeedPostPriority.important) Color(0xFFEAB308) else Color(
                        0xFF22D3EE
                    )).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (post.priority == FeedPostPriority.important) Icons.Default.Info else Icons.Default.Campaign,
                            contentDescription = null,
                            tint = if (post.priority == FeedPostPriority.important) Color(0xFFEAB308) else Color(
                                0xFF22D3EE
                            ),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.priority.name.uppercase(),
                            color = if (post.priority == FeedPostPriority.important) Color(
                                0xFFEAB308
                            ) else Color(0xFF22D3EE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = post.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.body,
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontFamily = QuickSandFontFamily
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReactionButton(
                        icon = Icons.Default.ThumbUp,
                        count = post.reactionCounts["like"] ?: 0,
                        isActive = userReaction == "like",
                        onClick = { onReactionClick(post.id, "like") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReactionButton(
                        painter = painterResource(id = R.drawable.ic_pray),
                        count = post.reactionCounts["pray"] ?: 0,
                        isActive = userReaction == "pray",
                        onClick = { onReactionClick(post.id, "pray") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ReactionButton(
                        icon = Icons.Default.LocalFireDepartment,
                        count = post.reactionCounts["fire"] ?: 0,
                        isActive = userReaction == "fire",
                        onClick = { onReactionClick(post.id, "fire") }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.commentCount.toString(),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.createdByName ?: "Unknown",
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )

                val formattedTime = SimpleDateFormat("HH:mm", LocalLocale.current.platformLocale)
                    .format(post.createdAt.toDate())
                Text(
                    text = formattedTime,
                    color = TextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}

@Composable
fun ReactionButton(
    icon: ImageVector? = null,
    painter: Painter? = null,
    count: Int,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() },
        color = if (isActive) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
        shape = CircleShape,
        border = BorderStroke(
            1.dp,
            if (isActive) Color.White else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isActive) Color(0xFFEAB308) else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                painter != null -> {
                    Icon(
                        painter = painter,
                        contentDescription = null,
                        tint = if (isActive) Color(0xFFEAB308) else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}
