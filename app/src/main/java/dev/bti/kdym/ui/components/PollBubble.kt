package dev.bti.kdym.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import dev.bti.kdym.data.models.Poll
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun PollBubble(
    pollId: String,
    viewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    val pollState by viewModel.getPoll(pollId).collectAsState(initial = null)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    pollState?.let { poll ->
        GlassCard(
            modifier = modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            backgroundColor = Color.White.copy(0.05f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = poll.question,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                poll.options.forEach { option ->
                    val isSelected = option.voterIds.contains(currentUserId)
                    val percentage = if (poll.totalVotes > 0) {
                        option.voteCount.toFloat() / poll.totalVotes
                    } else 0f
                    
                    PollOptionItem(
                        text = option.text,
                        percentage = percentage,
                        isSelected = isSelected,
                        onClick = { viewModel.voteInPoll(pollId, option.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${poll.totalVotes} votes",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = RubikFontFamily
                )
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(0.05f))
            .clickable { onClick() }
    ) {
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedPercentage)
                .fillMaxHeight()
                .background(if (isSelected) Color(0xFF22D3EE).copy(0.3f) else Color.White.copy(0.1f))
        )
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF22D3EE),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = RubikFontFamily
                )
            }
            
            Text(
                text = "${(percentage * 100).toInt()}%",
                color = Color.White.copy(0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = RubikFontFamily
            )
        }
    }
}
