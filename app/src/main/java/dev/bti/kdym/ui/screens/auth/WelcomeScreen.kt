package dev.bti.kdym.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
    onNavigateToSignUp: () -> Unit,
    onNavigateToPlayPreview: () -> Unit
) {
    val pages = remember {
        listOf(
            WelcomeOnboardingPage(
                eyebrow = "2026 DISTRICT THEME",
                title = "HEARTLAND",
                glitchTitle = "OUTPOUR",
                subtitle = "The official digital home for Kansas District Youth Ministries — built for rallies, camp, events, Play media, announcements, and the move of God across this generation.",
                icon = Icons.Default.LocalFireDepartment,
                accent = RedAccent,
                features = listOf(
                    WelcomeFeature(Icons.Default.AutoAwesome, "Theme-driven", "Heartland Outpour at the center"),
                    WelcomeFeature(Icons.Default.PlayCircleFilled, "Play media", "Messages, recaps, clips, and audio"),
                    WelcomeFeature(Icons.Default.Campaign, "Announcements", "Stay locked in with KDYM updates")
                )
            ),
            WelcomeOnboardingPage(
                eyebrow = "YEAR-ROUND KDYM",
                title = "ONE APP.",
                glitchTitle = "CONNECTED.",
                subtitle = "Know what is happening, where to be, what to watch, and how to stay connected with the district beyond a single service or event.",
                icon = Icons.Default.WifiTethering,
                accent = CyanAccent,
                features = listOf(
                    WelcomeFeature(Icons.Default.CalendarMonth, "Events", "Rallies, conventions, services, and camp"),
                    WelcomeFeature(Icons.Default.Forum, "Groups", "Camp groups, updates, and conversations"),
                    WelcomeFeature(Icons.Default.Diversity3, "Community", "People, leaders, teams, and connection")
                )
            ),
            WelcomeOnboardingPage(
                eyebrow = "CAMP MODE READY",
                title = "WHEN CAMP",
                glitchTitle = "GOES LIVE",
                subtitle = "KDYM becomes a live camp operations app: schedules, groups, tribe wars, updates, reminders, and leadership tools when they matter most.",
                icon = Icons.Default.Bolt,
                accent = Gold,
                features = listOf(
                    WelcomeFeature(Icons.Default.ListAlt, "Live schedule", "Know the next session, activity, and move"),
                    WelcomeFeature(Icons.Default.Shield, "Access control", "Camp features unlock through approval"),
                    WelcomeFeature(Icons.Default.EmojiEvents, "Tribe Wars", "Scores, events, points, and momentum")
                )
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
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo"
    )

    OutpourBackground {
        Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                WelcomeTopBar(onNavigateToLogin = onNavigateToLogin)

                // Carousel
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) { pageIndex ->
                    WelcomeCarouselPageContent(
                        page = pages[pageIndex],
                        floatOffset = floatAnim,
                        haloAlpha = haloAlpha,
                        isSelected = pagerState.currentPage == pageIndex
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
                    onSignIn = onNavigateToLogin,
                    onOpenPreview = onNavigateToPlayPreview
                )
            }
        }
    }
}

@Composable
fun WelcomeTopBar(onNavigateToLogin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.white_kdym_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(44.dp)
        )

        Surface(
            onClick = onNavigateToLogin,
            color = Color.White.copy(0.06f),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.13f))
        ) {
            Text(
                text = "SIGN IN",
                modifier = Modifier.padding(horizontal = 17.dp, vertical = 10.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily,
                letterSpacing = 2.sp,
                color = Color.White.copy(0.78f)
            )
        }
    }
}

@Composable
fun WelcomeCarouselPageContent(
    page: WelcomeOnboardingPage,
    floatOffset: Float,
    haloAlpha: Float,
    isSelected: Boolean
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

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
                    fontFamily = RubikFontFamily,
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
                    fontFamily = RubikFontFamily,
                    color = Color.White.copy(0.94f),
                    letterSpacing = (-2.2).sp,
                    lineHeight = 33.sp
                )
                Box(modifier = Modifier.offset(x = (-4).dp)) {
                    val fontSize = if (page.glitchTitle.length > 8) 58.sp else 66.sp
                    GlitchText(text = page.glitchTitle, fontSize = fontSize)
                }
            }
        }

        // Subtitle
        Text(
            text = page.subtitle,
            color = TextSecondary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = RubikFontFamily,
            lineHeight = 24.sp
        )

        // Features
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            page.features.forEachIndexed { index, feature ->
                val featureAnim by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.55f,
                    animationSpec = spring(dampingRatio = 0.78f, stiffness = 300f),
                    label = "featureAlpha"
                )
                val featureOffsetX by animateDpAsState(
                    targetValue = if (isSelected) 0.dp else 18.dp,
                    animationSpec = spring(dampingRatio = 0.78f, stiffness = 300f),
                    label = "featureOffsetX"
                )
                
                WelcomeFeatureRow(
                    feature = feature,
                    accent = page.accent,
                    modifier = Modifier
                        .alpha(featureAnim)
                        .offset(x = featureOffsetX)
                )
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
                            fontFamily = RubikFontFamily,
                            letterSpacing = 2.5.sp
                        )
                        Text(
                            text = if (page.title.contains("HEARTLAND")) "A digital home for the movement." else if (page.title.contains("ONE APP")) "Everything KDYM in one place." else "When camp starts, the app wakes up.",
                            color = Color.White,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = RubikFontFamily,
                            letterSpacing = (-1).sp,
                            lineHeight = 21.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .background(page.accent, CircleShape)
                            .blur(if (haloAlpha > 0.15f) 12.dp else 4.dp)
                            .alpha(haloAlpha * 3)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    val labels = if (page.title.contains("HEARTLAND")) listOf("PLAY", "MEDIA", "UPDATES")
                                else if (page.title.contains("ONE APP")) listOf("EVENTS", "GROUPS", "KDYM")
                                else listOf("SCHEDULE", "TRIBES", "LIVE")
                    
                    labels.forEach { label ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color.White.copy(0.052f),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.08f))
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(vertical = 12.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = RubikFontFamily,
                                letterSpacing = 1.8.sp,
                                color = Color.White.copy(0.78f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(140.dp))
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
                    fontFamily = RubikFontFamily,
                    letterSpacing = 1.6.sp
                )
                Text(
                    text = feature.detail,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = RubikFontFamily
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
    onSignIn: () -> Unit,
    onOpenPreview: () -> Unit
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

            // Buttons
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
                            shape = CircleShape
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                                Text(text = "CREATE ACCOUNT", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily, letterSpacing = 1.sp)
                            }
                        }

                        Button(
                            onClick = onSignIn,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.045f), contentColor = Color.White),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.11f))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.ArrowCircleRight, contentDescription = null)
                                Text(text = "SIGN IN", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily, letterSpacing = 1.sp)
                            }
                        }

                        Surface(
                            onClick = onOpenPreview,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White.copy(0.045f),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.11f))
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 15.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White.copy(0.76f))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "OPEN PLAY PREVIEW",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = RubikFontFamily,
                                    letterSpacing = 2.sp,
                                    color = Color.White.copy(0.76f)
                                )
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
                                fontFamily = RubikFontFamily,
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
                                Text(text = "NEXT", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily, letterSpacing = 2.1.sp)
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}
