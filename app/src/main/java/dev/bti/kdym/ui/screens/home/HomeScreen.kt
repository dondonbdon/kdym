package dev.bti.kdym.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.CyanAccent
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.RubikGlitchFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun HomeScreen() {
    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeTopBar()
            Spacer(modifier = Modifier.height(32.dp))
            HeroCard()
            Spacer(modifier = Modifier.height(24.dp))
            OperationCard()
            Spacer(modifier = Modifier.height(24.dp))
            LiveCampFeed()
            Spacer(modifier = Modifier.height(24.dp))
            JoelCard()
            Spacer(modifier = Modifier.height(100.dp))
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
                painter = painterResource(id = R.mipmap.kdym),
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
                Text(
                    text = "OUTPOUR",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = TextSecondary,
                    lineHeight = 10.sp
                )
            }
        }
        
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.extraLarge,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
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
fun HeroCard() {
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF061117).copy(alpha = 0.9f), // Deep Cyan
            Color(0xFF120102).copy(alpha = 0.9f)  // Deep Red
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 1000f),
        end = androidx.compose.ui.geometry.Offset(1000f, 0f)
    )

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Transparent,
        contentPadding = 0.dp
    ) {
        Box(modifier = Modifier.background(brush)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // District Theme Pill
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
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

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "HEARTLAND",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = Color.White
                )
                Text(
                    text = "OUTPOUR",
                    fontFamily = RubikGlitchFontFamily,
                    fontSize = 64.sp,
                    color = Color.White,
                    lineHeight = 64.sp
                )
                Text(
                    text = "YOUTH CAMP",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "A digital home for camp, tribe wars, announcements, events, Play media, and what God is pouring out across this generation.",
                    color = TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillStat(label = "DATES", value = "JUN 1-4", modifier = Modifier.weight(1f))
                    PillStat(label = "PLACE", value = "TABOR", modifier = Modifier.weight(1f))
                }
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextSecondary, letterSpacing = 1.sp)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun OperationCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "KDYM Hub",
                    fontFamily = RubikFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = CyanAccent
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "When camp is off, this stays useful for events, media, announcements, registration links, merch, and district updates.",
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun LiveCampFeed() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "LIVE CAMP FEED",
            fontFamily = RubikFontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = TextSecondary,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.height(120.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "Camp feed items will appear here", color = TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun JoelCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "joelPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "JOEL 2:28",
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = CyanAccent,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"I will pour out my spirit upon all flesh.\"",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "THESE ARE THE LAST DAYS",
                fontFamily = RubikFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = RedAccent,
                letterSpacing = 1.sp
            )
        }
    }
}
