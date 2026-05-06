package dev.bti.kdym.ui.screens.home

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.R
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.data.models.FeedPostPriority
import dev.bti.kdym.ui.components.CountdownCard
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.GlitchText
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel = viewModel()
) {
    val campDate = remember {
        Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 1, 0, 0)
        }.time
    }

    val posts by viewModel.liveUpdates.collectAsState()

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            HomeTopBar()
            Spacer(modifier = Modifier.height(24.dp))
            HeroCard(campDate)
            Spacer(modifier = Modifier.height(24.dp))
            VerseCard()
            Spacer(modifier = Modifier.height(24.dp))
            RegistrationCard()
            Spacer(modifier = Modifier.height(32.dp))
            LiveUpdatesHeader()
            Spacer(modifier = Modifier.height(16.dp))
            
            posts.forEach { post ->
                FeedPostCard(post)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.height(140.dp))
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.white_kdym_logo),
                contentDescription = "KDYM Logo",
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "KDYM",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "OUTPOUR",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(0.5f),
                    lineHeight = 10.sp
                )
            }
        }

        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(RedAccent, MaterialTheme.shapes.extraSmall)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "KDYM LIVE",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun HeroCard(campDate: java.util.Date) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        contentPadding = 24.dp
    ) {
        Column {
            Surface(
                color = RedAccent.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, RedAccent.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = RedAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2026 DISTRICT THEME",
                        fontFamily = RubikFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "HEARTLAND",
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color.White
            )

            GlitchText(text = "OUTPOUR")

            Text(
                text = "YOUTH CAMP",
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            CountdownCard(targetDate = campDate)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillStat(label = "DATES", value = "JUN 1-4", modifier = Modifier.weight(1f))
                PillStat(label = "PLACE", value = "TABOR", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PillStat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.4f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun VerseCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "verseAnim")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = floatAnim },
        backgroundColor = Color.Black.copy(alpha = 0.3f),
        borderColor = Color(0xFF22D3EE).copy(alpha = 0.1f * glowAnim)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JOEL 2:28",
                    color = Color(0xFF22D3EE).copy(alpha = glowAnim),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "PROMISE",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "\"I will pour out my spirit upon all flesh.\"",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 36.sp,
                fontFamily = RubikFontFamily
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "THESE ARE THE LAST DAYS",
                color = RedAccent.copy(alpha = glowAnim),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = RubikFontFamily
            )
        }
    }
}

@Composable
fun RegistrationCard() {
    val context = LocalContext.current
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_group_msg),
                        contentDescription = null,
                        tint = RedAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registration Open",
                        color = RedAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }
                Text(
                    text = "HEARTLAND",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ready for camp?",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Register for Heartland Youth Camp through the official Kansas UPCI form.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = RubikFontFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, "https://kansasupci.churchtrac.com/connect?ei=ZTZ5JAI".toUri()))
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "REGISTER FOR CAMP", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun FeedPostCard(post: FeedPost) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = (if (post.priority == FeedPostPriority.important) Color(0xFFEAB308) else Color(0xFF22D3EE)).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (post.priority == FeedPostPriority.important) Icons.Default.Info else Icons.Default.Campaign,
                            contentDescription = null,
                            tint = if (post.priority == FeedPostPriority.important) Color(0xFFEAB308) else Color(0xFF22D3EE),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = post.priority.name.uppercase(),
                            color = if (post.priority == FeedPostPriority.important) Color(0xFFEAB308) else Color(0xFF22D3EE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = RubikFontFamily
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
                fontFamily = RubikFontFamily,
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = post.body,
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontFamily = RubikFontFamily
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReactionButton(icon = Icons.Default.ThumbUp)
                    Spacer(modifier = Modifier.width(8.dp))
                    ReactionButton(icon = Icons.Default.Star)
                    Spacer(modifier = Modifier.width(8.dp))
                    ReactionButton(icon = Icons.Default.LocalFireDepartment)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = post.commentCount.toString(), color = TextSecondary, fontSize = 12.sp, fontFamily = RubikFontFamily)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = post.createdByName ?: "Unknown", color = TextSecondary.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)

                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(post.createdAt.toDate())
                Text(text = formattedTime, color = TextSecondary.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
            }
        }
    }
}

@Composable
fun ReactionButton(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(36.dp),
        color = Color.White.copy(alpha = 0.05f),
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun LiveUpdatesHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "FOR YOU",
                color = RedAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = RubikFontFamily
            )
            Text(
                text = "LIVE UPDATES",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
        }
        
        Surface(
            modifier = Modifier.size(48.dp),
            color = Color.White,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
