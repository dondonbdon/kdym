package dev.bti.kdym.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.components.StandardTopBar
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun CommentThreadScreen(
    postId: String,
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val updates by viewModel.liveUpdates.collectAsState()
    val post = updates.find { it.id == postId } ?: return
    val comments by viewModel.getComments(postId).collectAsState(initial = emptyList())
    
    var commentText by remember { mutableStateOf("") }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            StandardTopBar(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.ChatBubbleOutline,
                iconColor = Color(0xFFEF4444),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
            ) {
                item {
                    FeedPostCard(post = post) // Show the original post at the top
                }
                
                item {
                    Text(
                        text = "THREAD",
                        color = Color(0xFFEF4444),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = RubikFontFamily
                    )
                    Text(
                        text = "COMMENTS",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }
                
                items(comments) { comment ->
                    CommentItem(
                        author = comment.createdByName,
                        body = comment.body,
                        time = "13:16" // Placeholder time logic
                    )
                }
            }
            
            // Bottom Input Area
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                cornerRadius = 32.dp,
                backgroundColor = Color.Black.copy(alpha = 0.5f),
                contentPadding = 8.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CommandInputField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = "Ask or respond",
                            icon = Icons.Default.ChatBubbleOutline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(postId, commentText)
                                commentText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CommentItem(author: String, body: String, time: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.White.copy(alpha = 0.05f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = author.uppercase(),
                    color = Color(0xFF22D3EE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = time,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = RubikFontFamily
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = RubikFontFamily
            )
        }
    }
}
