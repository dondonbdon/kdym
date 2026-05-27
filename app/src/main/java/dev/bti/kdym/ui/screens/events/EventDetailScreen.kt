package dev.bti.kdym.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.R
import dev.bti.kdym.data.models.KDYMEvent
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToCreateScheduleItem: (String) -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: MainViewModel = viewModel(),
    adminViewModel: dev.bti.kdym.viewmodels.AdminViewModel = viewModel()
) {
    val events by viewModel.allEvents.collectAsState()
    val campSchedule by viewModel.campSchedule.collectAsState()
    
    // Combine both pools of events to find the current one
    val allPossibleEvents = remember(events, campSchedule) { events + campSchedule }
    val event = allPossibleEvents.find { it.id == eventId } ?: return

    val scheduleItems by viewModel.getScheduleItems(eventId).collectAsState(initial = emptyList())
    val user by viewModel.user.collectAsState()
    val isAdmin = user?.isAdmin == true

    // For paging siblings if this is a scheduleItem
    val siblings = remember(event, allPossibleEvents) {
        if (event.eventKind == "scheduleItem" && event.parentEventId != null) {
            allPossibleEvents.filter { it.parentEventId == event.parentEventId }.sortedBy { it.startDate }
        } else if (event.eventKind == "scheduleItem" && event.campId != null) {
            allPossibleEvents.filter { it.campId == event.campId && it.isCampEvent }.sortedBy { it.startDate }
        } else {
            emptyList()
        }
    }

    val rsvps by viewModel.getRSVPs(eventId).collectAsState(initial = emptyList())
    val myRSVP = rsvps.find { it.userId == user?.uid }?.status

    val accentColor = if (event.isCampEvent) Color(0xFF22D3EE) else RedAccent

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }

                if (isAdmin) {
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1A1A1A))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Event", color = Color.White) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                },
                                onClick = { showMenu = false; onNavigateToEdit(event.id) }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Schedule Item", color = Color.White) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                },
                                onClick = {
                                    showMenu = false; onNavigateToCreateScheduleItem(event.id)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Event", color = Color.White) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    adminViewModel.deleteEvent(event.id)
                                    onNavigateBack()
                                }
                            )
                        }
                    }
                }
            }

            if (siblings.isNotEmpty()) {
                val pagerState = androidx.compose.foundation.pager.rememberPagerState(
                    initialPage = siblings.indexOfFirst { it.id == event.id }.coerceAtLeast(0),
                    pageCount = { siblings.size }
                )

                LaunchedEffect(pagerState.currentPage) {
                    val targetEvent = siblings[pagerState.currentPage]
                    if (targetEvent.id != eventId) {
                        onNavigateToDetail(targetEvent.id)
                    }
                }

                // Progress indicator for paging
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(siblings.size) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) Color.White else Color.White.copy(
                                0.2f
                            )
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(6.dp)
                        )
                    }
                }

                androidx.compose.foundation.pager.HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val pageEvent = siblings[page]

                    // FIX: Added missing vertical scroll and vertical padding for paged items
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        EventDetailContent(
                            event = pageEvent,
                            isAdmin = isAdmin,
                            onNavigateToEdit = onNavigateToEdit,
                            onNavigateToCreateScheduleItem = onNavigateToCreateScheduleItem,
                            onNavigateToDetail = onNavigateToDetail,
                            viewModel = viewModel,
                            adminViewModel = adminViewModel,
                            rsvps = if (pageEvent.id == event.id) rsvps else emptyList(),
                            scheduleItems = if (pageEvent.id == event.id) scheduleItems else emptyList()
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ) // FIX: Added vertical padding
                ) {
                    EventDetailContent(
                        event = event,
                        isAdmin = isAdmin,
                        onNavigateToEdit = onNavigateToEdit,
                        onNavigateToCreateScheduleItem = onNavigateToCreateScheduleItem,
                        onNavigateToDetail = onNavigateToDetail,
                        viewModel = viewModel,
                        adminViewModel = adminViewModel,
                        rsvps = rsvps,
                        scheduleItems = scheduleItems
                    )
                }
            }
        }
    }
}

@Composable
fun EventDetailContent(
    event: KDYMEvent,
    isAdmin: Boolean,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToCreateScheduleItem: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: MainViewModel,
    adminViewModel: dev.bti.kdym.viewmodels.AdminViewModel,
    rsvps: List<dev.bti.kdym.viewmodels.EventRSVP>,
    scheduleItems: List<KDYMEvent>
) {
    val myRSVP = rsvps.find { it.userId == (viewModel.user.collectAsState().value?.uid) }?.status
    val accentColor = if (event.isCampEvent) Color(0xFF22D3EE) else RedAccent

    Column(modifier = Modifier.fillMaxSize()) {
        // Event Banner Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 32.dp,
            backgroundColor = Color.Black.copy(0.4f),
            borderColor = accentColor.copy(0.2f)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(if (event.eventKind == "scheduleItem") R.drawable.ic_calendar else R.drawable.ic_tent),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (event.eventKind == "scheduleItem") "Schedule Item" else event.category.name.uppercase(),
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                    Text(
                        text = SimpleDateFormat(
                            "MMM d, yyyy",
                            Locale.US
                        ).format(event.startDate.toDate()),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = event.title.uppercase(),
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    lineHeight = 44.sp
                )

                event.subtitle?.let {
                    Text(
                        text = it,
                        color = Color(0xFF22D3EE),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.timeText,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location ?: "TBD",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RSVP Section
        RSVPSection(
            myRSVP = myRSVP,
            rsvps = rsvps,
            onRSVP = { status -> viewModel.updateRSVP(event.id, status) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Details Section
        Text(
            text = "DETAILS",
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            backgroundColor = Color.White.copy(0.05f)
        ) {
            Text(
                text = event.description,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontFamily = QuickSandFontFamily,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (scheduleItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "SCHEDULE",
                        color = Color(0xFF22D3EE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Inside this event",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
                Text(
                    text = scheduleItems.size.toString(),
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                scheduleItems.sortedBy { it.startDate }.forEach { item ->
                    ScheduleItemMiniRow(item, onClick = { onNavigateToDetail(item.id) })
                }
            }
        }

        Spacer(modifier = Modifier.height(140.dp))
    }
}


@Composable
fun RSVPSection(
    myRSVP: String?,
    rsvps: List<dev.bti.kdym.viewmodels.EventRSVP>,
    onRSVP: (String) -> Unit
) {
    val goingCount = rsvps.count { it.status == "going" }
    val maybeCount = rsvps.count { it.status == "maybe" }
    val notGoingCount = rsvps.count { it.status == "no" }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.White.copy(0.05f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RSVP",
                        color = Color(0xFF22D3EE),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Will you be there?",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = goingCount.toString(),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "GOING",
                        color = TextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RSVPButton(
                    label = "GOING",
                    icon = Icons.Default.CheckCircle,
                    isSelected = myRSVP == "going",
                    modifier = Modifier.weight(1f)
                ) { onRSVP("going") }
                RSVPButton(
                    label = "MAYBE",
                    icon = Icons.AutoMirrored.Filled.Help,
                    isSelected = myRSVP == "maybe",
                    modifier = Modifier.weight(1f)
                ) { onRSVP("maybe") }
                RSVPButton(
                    label = "NO",
                    icon = Icons.Default.Close,
                    isSelected = myRSVP == "no",
                    modifier = Modifier.weight(1f)
                ) { onRSVP("no") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RSVPCountBox(label = "MAYBE", count = maybeCount, color = Color(0xFFEAB308), modifier = Modifier.weight(1f))
                RSVPCountBox(label = "NOT GOING", count = notGoingCount, color = Color(0xFFEF4444), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun RSVPCountBox(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(48.dp),
        color = color.copy(0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text(text = count.toString(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun RSVPButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(64.dp)
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.White.copy(0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun ScheduleItemMiniRow(item: KDYMEvent, onClick: () -> Unit) {
    val dayName = remember(item.startDate) {
        SimpleDateFormat(
            "EEEE",
            Locale.US
        ).format(item.startDate.toDate()).uppercase()
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        backgroundColor = Color.Black.copy(0.3f),
        cornerRadius = 20.dp,
        contentPadding = 12.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.width(60.dp)) {
                Text(
                    text = item.timeText.replace(" - ", " -\n"),
                    color = Color(0xFF22D3EE),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 12.sp
                )
                Text(
                    text = dayName,
                    color = TextSecondary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
