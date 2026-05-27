package dev.bti.kdym.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import dev.bti.kdym.data.models.Camp
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import androidx.compose.ui.platform.LocalContext

@Composable
fun MemoriesCard(camp: Camp) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(camp.accentColor))
    } catch (e: Exception) {
        Color(0xFFFFD700)
    }
    
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(0.4f),
        cornerRadius = 32.dp,
        contentPadding = 24.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = camp.romanYear ?: "MMXXIV",
                        color = Color.White.copy(0.3f),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = (-2).sp
                    )
                    Text(
                        text = camp.yearText ?: "TWO THOUSAND TWENTY FOUR",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                }
                
                Surface(
                    color = Color.White.copy(0.06f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = camp.theme?.uppercase() ?: "LET'S GO",
                color = accentColor,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                lineHeight = 36.sp
            )
            Text(
                text = camp.subtitle ?: "Go therefore.",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bible Verse Card
            Surface(
                color = Color.Black.copy(0.5f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = camp.verseReference ?: "MATTHEW 28:19",
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${camp.verse ?: "Go ye therefore, and teach all nations."}\"",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = camp.verseTagline ?: "GO WHERE HE SENDS",
                        color = accentColor.copy(0.6f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Photo Stacks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((-16).dp)
            ) {
                repeat(camp.historyPhotos.size.coerceAtMost(3)) { i ->
                    MemoryStackItem(
                        index = i,
                        accentColor = accentColor,
                        romanYear = camp.romanYear ?: "MMXXIV",
                        imageName = camp.historyPhotos.getOrNull(i)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(accentColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "A YEAR IN THE STORY",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "SWIPE",
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun MemoryStackItem(index: Int, accentColor: Color, romanYear: String, imageName: String?) {
    val context = LocalContext.current
    val rotation = when(index) {
        0 -> -5f
        1 -> 0f
        else -> 5f
    }
    
    val resId = remember(imageName) {
        if (imageName != null) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
        } else 0
    }

    Surface(
        modifier = Modifier
            .size(width = 110.dp, height = 150.dp)
            .graphicsLayer { rotationZ = rotation },
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(0.2f),
                            accentColor.copy(0.05f)
                        )
                    )
                )
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(0.3f))
        ) {
            if (resId != 0) {
                AsyncImage(
                    model = resId,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = romanYear,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (index < 2 && resId == 0) {
                    Icon(
                        imageVector = if (index == 0) Icons.Default.AddPhotoAlternate else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White.copy(0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (index == 0) "ADD\nMEMORY" else "ADD\nMOMENT",
                        color = TextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily,
                        lineHeight = 10.sp
                    )
                } else if (index >= 2 || resId != 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TIME TRAVEL",
                            color = accentColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (resId != 0) "Relive the moment." else "The light turned green. The call was movement, purpose, and bold obedience.",
                        color = Color.White.copy(0.8f),
                        fontSize = 8.sp,
                        fontFamily = QuickSandFontFamily,
                        lineHeight = 10.sp,
                        maxLines = 4
                    )
                }
            }
        }
    }
}
