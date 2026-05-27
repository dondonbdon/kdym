package dev.bti.kdym.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

@Composable
fun PollBubble(
    groupId: String,
    pollId: String,
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val pollState by viewModel.getPoll(groupId, pollId).collectAsState(initial = null)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Optimistic UI State
    var optimisticVoteId by remember(pollState) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    pollState?.let { poll ->
        GlassCard(
            modifier = modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            backgroundColor = Color.White.copy(0.05f),
            borderColor = Color(0xFFEAB308).copy(0.3f) // Subtle yellow border match iOS
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // iOS Style Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Poll,
                            contentDescription = null,
                            tint = Color(0xFFEAB308), // Yellow accent
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "POLL",
                            color = Color(0xFFEAB308),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                    Text(
                        text = "${poll.totalVotes + if(optimisticVoteId != null && !poll.options.any { it.voterIds.contains(currentUserId) }) 1 else 0} VOTERS",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily
                    )
                }

                Text(
                    text = if (poll.allowMultipleVotes) "Multiple choices" else "Single choice",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = poll.question,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                poll.options.forEach { option ->
                    val serverSelected = option.voterIds.contains(currentUserId)
                    val isSelected = optimisticVoteId == option.id || (serverSelected && optimisticVoteId == null)

                    val virtualTotalVotes = poll.totalVotes + if(optimisticVoteId != null && !poll.options.any { it.voterIds.contains(currentUserId) }) 1 else 0
                    val virtualOptionVotes = option.voteCount + if(optimisticVoteId == option.id && !serverSelected) 1 else 0

                    val percentage = if (virtualTotalVotes > 0) {
                        virtualOptionVotes.toFloat() / virtualTotalVotes
                    } else 0f

                    PollOptionItem(
                        text = option.title,
                        percentage = percentage,
                        isSelected = isSelected,
                        onClick = {
                            // Optimistic Update Trigger
                            val previousVoteId = optimisticVoteId
                            optimisticVoteId = option.id
                            scope.launch {
                                try {
                                    viewModel.voteInPoll(groupId, pollId, option.id)
                                } catch (e: Exception) {
                                    optimisticVoteId = previousVoteId
                                    // Feedback would ideally happen via a Snackbar in MainViewModel
                                } finally {
                                    // Reset after server sync is handled by pollState update
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun PollOptionItem(
    text: String,
    percentage: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedPercentage by animateFloatAsState(targetValue = percentage, label = "poll_percentage")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(0.05f))
            .border(1.dp, if (isSelected) Color(0xFFEAB308).copy(0.5f) else Color.Transparent, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedPercentage)
                .fillMaxHeight()
                .background(if (isSelected) Color(0xFFEAB308).copy(0.15f) else Color.White.copy(0.05f))
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFFEAB308) else Color.White.copy(0.3f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = QuickSandFontFamily,
                    maxLines = 1
                )
            }

            Text(
                text = "${(percentage * 100).toInt()}%",
                color = Color.White.copy(0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSandFontFamily
            )
        }
    }
}