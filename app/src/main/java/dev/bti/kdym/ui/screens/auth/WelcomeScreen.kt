package dev.bti.kdym.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.GlitchText
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.*
import kotlinx.coroutines.launch

data class WelcomeFeature(
    val icon: ImageVector,
    val title: String,
    val detail: String
)

data class WelcomeOnboardingPage(
    val eyebrow: String,
    val title: String,
    val glitchTitle: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color,
    val features: List<WelcomeFeature>
)

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val pages = remember {
        listOf(
            WelcomeOnboardingPage(
                eyebrow = "KDYM OUTPOUR",
                title = "HEARTLAND",
                glitchTitle = "OUTPOUR",
                subtitle = "The official digital home for Kansas District Youth Ministries.",
                icon = Icons.Default.LocalFireDepartment,
                accent = RedAccent,
                features = emptyList()
            ),
            WelcomeOnboardingPage(
                eyebrow = "YEAR-ROUND",
                title = "ONE APP.",
                glitchTitle = "CONNECTED.",
                subtitle = "Rallies, services, media, announcements, and community in one place.",
                icon = Icons.Default.WifiTethering,
                accent = CyanAccent,
                features = emptyList()
            ),
            WelcomeOnboardingPage(
                eyebrow = "CAMP MODE",
                title = "WHEN CAMP",
                glitchTitle = "GOES LIVE",
                subtitle = "Live schedule, tribe wars, group updates, reminders, and camp news.",
                icon = Icons.Default.Bolt,
                accent = Gold,
                features = emptyList()
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isFinalPage = pagerState.currentPage == pages.size - 1

    // Floating animations
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    OutpourBackground {
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                WelcomeTopBar()

                // Carousel
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top,
                    userScrollEnabled = true
                ) { pageIndex ->

                    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction

                    WelcomeCarouselPageContent(
                        page = pages[pageIndex],
                        floatOffset = floatAnim,
                        isSelected = pagerState.currentPage == pageIndex,
                        pageOffset = pageOffset
                    )
                }

                // Bottom Controls
                WelcomeBottomControls(
                    pagerState = pagerState,
                    pages = pages,
                    isFinalPage = isFinalPage,
                    onSkip = {
                        scope.launch { pagerState.animateScrollToPage(pages.size - 1) }
                    },
                    onNext = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    onSignUp = onNavigateToSignUp,
                    onSignIn = onNavigateToLogin
                )
            }
        }
    }
}

@Composable
fun WelcomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.white_kdym_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(44.dp)
        )
    }
}

@Composable
fun WelcomeCarouselPageContent(
    page: WelcomeOnboardingPage,
    floatOffset: Float,
    isSelected: Boolean,
    pageOffset: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .graphicsLayer {
                // The "Windy" Effect
                val absoluteOffset = kotlin.math.abs(pageOffset)

                // Slight fade as it blows away
                alpha = 1f - (absoluteOffset * 0.3f)

                // Scale it down slightly
                scaleX = 1f - (absoluteOffset * 0.15f)
                scaleY = 1f - (absoluteOffset * 0.15f)

                // Tilt it like the wind is catching it (rotates based on swipe direction)
                rotationZ = pageOffset * 12f

                // Add extra horizontal push so it sweeps out faster
                translationX = pageOffset * 150f
            },
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Hero Badge
        Surface(
            color = page.accent.copy(alpha = 0.13f),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, page.accent.copy(alpha = 0.26f)),
            modifier = Modifier.padding(top = 14.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(page.accent.copy(0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = page.accent,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Text(
                    text = page.eyebrow,
                    fontFamily = QuickSandFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(0.78f)
                )
            }
        }

        // Titles
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            val titleAnim by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.94f,
                animationSpec = spring(dampingRatio = 0.74f, stiffness = 300f),
                label = "titleScale"
            )
            val titleOffset by animateDpAsState(
                targetValue = if (isSelected) 0.dp else 18.dp,
                animationSpec = spring(dampingRatio = 0.74f, stiffness = 300f),
                label = "titleOffset"
            )

            Column(
                modifier = Modifier
                    .scale(titleAnim)
                    .offset(y = titleOffset)
            ) {
                Text(
                    text = page.title,
                    fontSize = if (page.title.length > 9) 34.sp else 40.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    color = Color.White.copy(0.94f),
                    letterSpacing = (-2.2).sp,
                    lineHeight = 33.sp
                )
                BoxWithConstraints(modifier = Modifier.fillMaxWidth().offset(x = (-4).dp)) {
                    val maxFontSize = 66f
                    val characterWidthRatio = 0.65f

                    val calculatedFontSize = maxWidth.value / (page.glitchTitle.length * characterWidthRatio)

                    val finalFontSize = calculatedFontSize.coerceAtMost(maxFontSize)

                    GlitchText(
                        text = page.glitchTitle,
                        fontSize = finalFontSize.sp
                    )
                }
            }
        }

        // Subtitle
        Text(
            text = page.subtitle,
            color = TextSecondary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = QuickSandFontFamily,
            lineHeight = 24.sp,
            maxLines = 3
        )

        // Features - Simplified for no scroll
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val featureLabels = if (page.title.contains("HEARTLAND")) listOf("EVENTS", "PLAY", "UPDATES")
            else if (page.title.contains("ONE APP")) listOf("SCHEDULE", "GROUPS", "MEDIA")
            else listOf("LIVE", "TRIBES", "ACCESS")

            featureLabels.forEach { label ->
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(0.045f),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.08f))
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        letterSpacing = 1.5.sp,
                        color = Color.White.copy(0.78f)
                    )
                }
            }
        }

        // Live Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = floatOffset.dp),
            cornerRadius = 24.dp,
            backgroundColor = Color.White.copy(0.045f)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (page.title.contains("HEARTLAND")) "KDYM LIVE" else if (page.title.contains("ONE APP")) "BUILT FOR NOW" else "CAMP READY",
                            color = page.accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily,
                            letterSpacing = 2.5.sp
                        )
                        Text(
                            text = if (page.title.contains("HEARTLAND")) "A digital home for the movement." else if (page.title.contains("ONE APP")) "Know what is happening next." else "The app wakes up when camp does.",
                            color = Color.White,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily,
                            letterSpacing = (-1).sp,
                            lineHeight = 21.sp
                        )
                        Text(
                            text = if (page.title.contains("HEARTLAND")) "One place for districts moments, media and updates" 
                                   else if (page.title.contains("ONE APP")) "Stay connected beyond one service or event" 
                                   else "Schedules, groups, scores, alerts and leadership tools",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(page.accent.copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = null,
                            tint = page.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Progress segments
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { i ->
                        val active = (page.title.contains("HEARTLAND") && i == 0) ||
                                (page.title.contains("ONE APP") && i == 1) ||
                                (page.title.contains("WHEN CAMP") && i == 2)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(if (active) page.accent else Color.White.copy(0.15f))
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WelcomeFeatureRow(feature: WelcomeFeature, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(0.045f),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.08f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accent.copy(0.13f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = feature.title.uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    letterSpacing = 1.6.sp
                )
                Text(
                    text = feature.detail,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}

@Composable
fun WelcomeBottomControls(
    pagerState: androidx.compose.foundation.pager.PagerState,
    pages: List<WelcomeOnboardingPage>,
    isFinalPage: Boolean,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onSignUp: () -> Unit,
    onSignIn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Background.copy(0.38f),
                        Background.copy(0.78f)
                    )
                )
            )
            .padding(horizontal = 22.dp, vertical = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Page Indicator
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.forEachIndexed { index, page ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 28.dp else 8.dp,
                        animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (isSelected) page.accent else Color.White.copy(0.18f))
                    )
                }
            }

            AnimatedContent(
                targetState = isFinalPage,
                transitionSpec = {
                    (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                },
                label = "buttons"
            ) { final ->
                if (final) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = onSignUp,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text(text = "CREATE ACCOUNT", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily, letterSpacing = 1.sp)
                            }
                        }

                        Button(
                            onClick = onSignIn,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                            shape = RoundedCornerShape(28.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.2f))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.ArrowCircleRight, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text(text = "SIGN IN", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily, letterSpacing = 1.sp)
                            }
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(
                            onClick = onSkip,
                            modifier = Modifier.width(92.dp),
                            color = Color.White.copy(0.045f),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.09f))
                        ) {
                            Text(
                                text = "SKIP",
                                modifier = Modifier.padding(vertical = 16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily,
                                letterSpacing = 2.1.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onNext,
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                            shape = CircleShape
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(text = "NEXT", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily, letterSpacing = 2.1.sp)
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
