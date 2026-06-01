package dev.bti.kdym.ui.screens.events

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.bti.kdym.R
import dev.bti.kdym.data.models.EventCategory
import dev.bti.kdym.data.models.KDYMEvent
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.CyanAccent
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class EventScope(val title: String) {
    ALL_EVENTS("Events"),
    CAMP_SCHEDULE("Schedule")
}

@Composable
fun EventsScreen(
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit = {},
    mainViewModel: MainViewModel = hiltViewModel(),
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    var selectedScope by remember { mutableStateOf(EventScope.ALL_EVENTS) }

    val allEvents by mainViewModel.allEvents.collectAsState()
    val campSchedule by mainViewModel.campSchedule.collectAsState()

    var seedMessage by remember { mutableStateOf<String?>(null) }

    val visibleEvents = remember(allEvents, campSchedule, selectedScope) {
        when (selectedScope) {
            EventScope.ALL_EVENTS -> allEvents.filter { !it.isCampEvent }
            EventScope.CAMP_SCHEDULE -> campSchedule
        }
    }

    OutpourBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                HomeTopBar()
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    Text(
                        text = "KDYM",
                        color = RedAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "EVENTS",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    ScopeSwitcher(
                        selectedScope = selectedScope,
                        onScopeSelected = { selectedScope = it }
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                seedMessage?.let {
                    item {
                        InlineEventNotice(message = it)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (mainViewModel.uiState.value.isLoading) {
                    item {
                        EventsLoadingBlock()
                    }
                } else {
                    eventsListContent(
                        events = visibleEvents,
                        scope = selectedScope,
                        onSelect = { onNavigateToEventDetail(it.id) }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(120.dp)) }
            }
        }
    }
}

@Composable
fun EventsHeader(
    isAdmin: Boolean,
    onAddClick: () -> Unit,
    onSeedClick: () -> Unit,
    isSeeding: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HomeTopBar()
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = RedAccent, modifier = Modifier.size(16.dp))
                    Text(text = "KDYM EVENTS", color = RedAccent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.4.sp, fontFamily = QuickSandFontFamily)
                }
                Text(text = "EVENTS", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = (-2.4).sp, fontFamily = QuickSandFontFamily)
            }
            
            if (isAdmin) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onSeedClick,
                        enabled = !isSeeding,
                        modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                    ) {
                        if (isSeeding) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        else Icon(Icons.Default.AutoAwesome, contentDescription = "Seed", tint = Color.White)
                    }
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier.background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun ScopeSwitcher(
    selectedScope: EventScope,
    onScopeSelected: (EventScope) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EventScope.entries.forEach { scope ->
            val isSelected = selectedScope == scope
            Surface(
                onClick = { onScopeSelected(scope) },
                color = if (isSelected) Color.White else Color.White.copy(0.065f),
                shape = CircleShape,
                border = if (isSelected) null else BorderStroke(1.dp, Color.White.copy(0.11f))
            ) {
                Text(
                    text = scope.title.uppercase(),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = if (isSelected) Color.Black else Color.White.copy(0.64f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}

fun LazyListScope.eventsListContent(
    events: List<KDYMEvent>,
    scope: EventScope,
    onSelect: (KDYMEvent) -> Unit
) {
    if (events.isEmpty()) {
        item { EmptyEventsPlaceholder(scope = scope) }
        return
    }

    val sortedEvents = events.sortedBy { it.startDate }

    when (scope) {
        EventScope.ALL_EVENTS -> {
            val now = com.google.firebase.Timestamp.now()
            val nextEvent = sortedEvents.firstOrNull { (it.endDate ?: it.startDate) >= now }
            
            if (nextEvent != null) {
                item {
                    FeaturedUpcomingEventCard(event = nextEvent, onSelect = onSelect)
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "ALL EVENTS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.1.sp)
                    Text(text = "${sortedEvents.size}", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(sortedEvents) { event ->
                EventListCard(event = event, onClick = { onSelect(event) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        EventScope.CAMP_SCHEDULE -> {
            val series = groupEventsBySeries(sortedEvents)
            val selectedSeries = series.firstOrNull { it.isCurrent } ?: series.firstOrNull { it.isNext } ?: series.lastOrNull()

            if (selectedSeries != null) {
                item {
                    EventScheduleHero(series = selectedSeries)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                selectedSeries.dayGroups.forEachIndexed { index, group ->
                    item {
                        EventScheduleDayDisclosure(
                            group = group,
                            dayNumber = index + 1,
                            onSelect = onSelect
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedUpcomingEventCard(event: KDYMEvent, onSelect: (KDYMEvent) -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onSelect(event) },
        cornerRadius = 34.dp,
        backgroundColor = Color.White.copy(0.055f),
        contentPadding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Gradient Overlay
            Box(modifier = Modifier
                .matchParentSize()
                .background(Brush.radialGradient(
                    colors = listOf(CyanAccent.copy(0.3f), RedAccent.copy(0.18f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(1000f, 0f),
                    radius = 800f
                ))
            )
            
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        Text("Next Up", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
                    }
                    Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = event.title.uppercase(),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-2.0).sp,
                        lineHeight = 33.sp
                    )
                    Text(text = event.kdymDateRangeText, color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }

                event.subtitle?.let {
                    if (it.isNotEmpty()) {
                        Surface(color = Color.White, shape = CircleShape) {
                            Text(
                                text = it.uppercase(),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventListCard(event: KDYMEvent, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        cornerRadius = 28.dp,
        backgroundColor = Color.White.copy(0.045f),
        contentPadding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val gradientColor = if (event.isCampEvent) CyanAccent else if (event.category == EventCategory.camp) RedAccent else CyanAccent
            Box(modifier = Modifier
                .matchParentSize()
                .background(Brush.radialGradient(
                    colors = listOf(gradientColor.copy(0.18f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(1000f, 0f),
                    radius = 400f
                ))
            )

            Row(modifier = Modifier.padding(15.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                EventDateBadge(event = event)
                
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = if (event.isCampEvent) Icons.AutoMirrored.Filled.ListAlt else Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = if (event.isCampEvent) CyanAccent else RedAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = (if (event.isCampEvent) "Schedule" else event.category.name).uppercase(),
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.7.sp
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(0.35f), modifier = Modifier.size(14.dp))
                    }

                    Text(text = event.title.uppercase(), color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.0).sp, lineHeight = 20.sp)
                    
                    event.subtitle?.let {
                        if (it.isNotEmpty()) {
                            Text(text = it, color = if (event.category == EventCategory.camp) RedAccent else CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.AccessTimeFilled, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                            Text(text = event.kdymTimeText, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        event.location?.let {
                            if (it.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                                    Text(text = it, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Text(text = event.description, color = TextSecondary, fontSize = 12.sp, maxLines = 2, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
fun EventDateBadge(event: KDYMEvent) {
    Column(
        modifier = Modifier
            .width(62.dp)
            .background(Color.Black.copy(0.3f), RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(20.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = event.monthText, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
        Text(text = event.dayNumberText, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.7).sp)
        Text(text = event.dayText, color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
    }
}

@Composable
fun EventScheduleHero(series: EventScheduleSeries) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 30.dp,
        backgroundColor = Color.White.copy(0.055f),
        contentPadding = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .matchParentSize()
                .background(Brush.radialGradient(
                    colors = listOf(CyanAccent.copy(0.24f), RedAccent.copy(0.14f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(1000f, 0f),
                    radius = 600f
                ))
            )
            
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = if (series.isCurrent) Icons.Default.Radio else Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = (if (series.isCurrent) "Current Schedule" else "Next Schedule").uppercase(),
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.7.sp
                        )
                    }
                    Text(text = "${series.itemCount} ITEMS", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = series.title.uppercase(), color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.8).sp, lineHeight = 28.sp)
                    Text(text = series.dateRangeText, color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }

                Text(text = "Tap a day to open or collapse the schedule.", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EventScheduleDayDisclosure(
    group: EventScheduleDayGroup,
    dayNumber: Int,
    onSelect: (KDYMEvent) -> Unit
) {
    var isExpanded by remember { mutableStateOf(dayNumber == 1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(0.045f), RoundedCornerShape(28.dp))
            .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(28.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .size(58.dp)
                    .background(Color.Black.copy(0.3f), RoundedCornerShape(19.dp))
                    .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(19.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "DAY", color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                Text(text = "$dayNumber", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.4).sp)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = group.weekdayText.uppercase(), color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.8.sp)
                Text(text = group.dateText, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(text = "${group.items.size} schedule items", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White.copy(0.58f),
                modifier = Modifier.size(24.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(horizontal = 13.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                group.items.forEach { item ->
                    EventScheduleCompactRow(item = item, onClick = { onSelect(item) })
                }
            }
        }
    }
}

@Composable
fun EventScheduleCompactRow(item: KDYMEvent, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.Black.copy(0.2f), RoundedCornerShape(18.dp))
            .border(1.dp, Color.White.copy(0.065f), RoundedCornerShape(18.dp))
            .padding(13.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = item.kdymTimeText,
            modifier = Modifier.width(86.dp),
            color = CyanAccent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.0.sp
        )
        
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = item.title.uppercase(), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp)
            item.location?.let {
                if (it.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                        Text(text = it, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(0.28f), modifier = Modifier.size(14.dp))
    }
}

@Composable
fun EventsLoadingBlock() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CircularProgressIndicator(color = Color.White)
        Text(text = "Loading Events", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
    }
}

@Composable
fun EmptyEventsPlaceholder(scope: EventScope) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(
                imageVector = if (scope == EventScope.ALL_EVENTS) Icons.Default.CalendarToday else Icons.AutoMirrored.Filled.ListAlt,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = if (scope == EventScope.ALL_EVENTS) "No events yet" else "No schedule items yet",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (scope == EventScope.ALL_EVENTS) "Admins can create rallies, services, camp, and full-year KDYM events." 
                       else "Schedule items can live under an event detail instead of cluttering the main Events list.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun InlineEventNotice(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyanAccent.copy(0.11f), RoundedCornerShape(18.dp))
            .border(1.dp, CyanAccent.copy(0.26f), RoundedCornerShape(18.dp))
            .padding(15.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
        Text(text = message, color = Color.White.copy(0.86f), fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
    }
}

// Helpers

data class EventScheduleSeries(
    val key: String,
    val title: String,
    val items: List<KDYMEvent>
) {
    val itemCount = items.size
    val startDate = items.minOf { it.startDate }
    val endDate = items.maxOf { it.endDate ?: it.startDate }
    val isCurrent: Boolean
        get() {
            val now = com.google.firebase.Timestamp.now()
            return startDate <= now && endDate >= now
        }
    val isNext: Boolean
        get() = startDate > com.google.firebase.Timestamp.now()

    val dateRangeText: String
        get() {
            val sdf = SimpleDateFormat("MMM d", Locale.US)
            val start = sdf.format(startDate.toDate())
            val end = sdf.format(endDate.toDate())
            return if (start == end) start else "$start - $end, ${SimpleDateFormat("yyyy", Locale.US).format(endDate.toDate())}"
        }

    val dayGroups: List<EventScheduleDayGroup>
        get() {
            val calendar = Calendar.getInstance()
            return items.groupBy {
                calendar.time = it.startDate.toDate()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }.toSortedMap().map { (millis, items) ->
                EventScheduleDayGroup(Date(millis), items.sortedBy { it.startDate })
            }
        }
}

data class EventScheduleDayGroup(
    val date: Date,
    val items: List<KDYMEvent>
) {
    val weekdayText = SimpleDateFormat("EEEE", Locale.US).format(date)
    val dateText = SimpleDateFormat("MMMM d", Locale.US).format(date)
}

fun groupEventsBySeries(events: List<KDYMEvent>): List<EventScheduleSeries> {
    return events.groupBy { it.scheduleSeriesKey }
        .map { (key, items) ->
            val title = when (key) {
                "kyc-2026" -> "KYC 2026"
                "outpour-camp-2026" -> "Heartland Youth Camp"
                else -> items.firstOrNull()?.subtitle ?: "Event Schedule"
            }
            EventScheduleSeries(key, title, items)
        }
        .sortedBy { it.startDate }
}
