package dev.bti.kdym.ui.screens.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import dev.bti.kdym.data.models.EventCategory
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.components.admin.PickerBottomSheet
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@Composable
fun CreateEventScreen(
    eventId: String? = null,
    parentEventId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    val context = LocalContext.current
    val events by viewModel.allEvents.collectAsState()
    val existingEvent = remember(eventId, events) { events.find { it.id == eventId } }
    
    var title by remember(existingEvent) { mutableStateOf<String>(existingEvent?.title ?: "") }
    var subtitle by remember(existingEvent) { mutableStateOf<String>(existingEvent?.subtitle ?: "") }
    var description by remember(existingEvent) { mutableStateOf<String>(existingEvent?.description ?: "") }
    var location by remember(existingEvent) { mutableStateOf<String>(existingEvent?.location ?: "") }
    var registrationURL by remember(existingEvent) { mutableStateOf<String>(existingEvent?.registrationURL ?: "") }
    var category by remember(existingEvent) { mutableStateOf<EventCategory>(existingEvent?.category ?: EventCategory.other) }
    
    var startDate by remember(existingEvent) { 
        mutableStateOf<Calendar>(
            existingEvent?.startDate?.let { 
                val cal = Calendar.getInstance()
                cal.time = it.toDate()
                cal
            } ?: Calendar.getInstance()
        )
    }
    var endDate by remember(existingEvent) { 
        mutableStateOf<Calendar?>(
            existingEvent?.endDate?.let { 
                val cal = Calendar.getInstance()
                cal.time = it.toDate()
                cal
            }
        )
    }
    
    var isCampEvent by remember(existingEvent) { mutableStateOf(existingEvent?.isCampEvent ?: false) }
    var isPublished by remember(existingEvent) { mutableStateOf(existingEvent?.isPublished ?: true) }

    var showCategoryPicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("MMM d, yyyy", LocalLocale.current.platformLocale)
    val timeFormatter = SimpleDateFormat("h:mm a", LocalLocale.current.platformLocale)

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "BACK", fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = if (eventId == null) "CREATE" else "EDIT",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = if (parentEventId != null) "SCHEDULE ITEM" else "EVENT",
                        color = TextSecondary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                CommandInputField(value = title, onValueChange = { title = it }, placeholder = "Title", icon = Icons.Default.Event)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = subtitle, onValueChange = { subtitle = it }, placeholder = "Subtitle", icon = Icons.Default.Edit)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = description, onValueChange = { description = it }, placeholder = "Description", icon = Icons.AutoMirrored.Filled.Notes)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = location, onValueChange = { location = it }, placeholder = "Location", icon = Icons.Default.Place)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = registrationURL, onValueChange = { registrationURL = it }, placeholder = "Registration URL (Optional)", icon = Icons.Default.Link)

                Spacer(modifier = Modifier.height(24.dp))

                // Category Picker
                GlassCard(
                    modifier = Modifier.fillMaxWidth().clickable { showCategoryPicker = true }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = Color(0xFF22D3EE))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "CATEGORY", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            Text(text = category.title.uppercase(), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = Color.White.copy(0.4f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Date/Time Pickers
                Text(text = "DATE & TIME", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                DateTimeRow(
                    label = "Starts",
                    calendar = startDate,
                    onDateClick = {
                        DatePickerDialog(context, { _, y, m, d ->
                            val newCal = startDate.clone() as Calendar
                            newCal.set(y, m, d)
                            startDate = newCal
                        }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    onTimeClick = {
                        TimePickerDialog(context, { _, h, m ->
                            val newCal = startDate.clone() as Calendar
                            newCal.set(Calendar.HOUR_OF_DAY, h)
                            newCal.set(Calendar.MINUTE, m)
                            startDate = newCal
                        }, startDate.get(Calendar.HOUR_OF_DAY), startDate.get(Calendar.MINUTE), false).show()
                    },
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter
                )

                Spacer(modifier = Modifier.height(8.dp))

                DateTimeRow(
                    label = "Ends",
                    calendar = endDate ?: startDate,
                    onDateClick = {
                        val current = endDate ?: startDate
                        DatePickerDialog(context, { _, y, m, d ->
                            val newCal = (endDate ?: startDate).clone() as Calendar
                            newCal.set(y, m, d)
                            endDate = newCal
                        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    onTimeClick = {
                        val current = endDate ?: startDate
                        TimePickerDialog(context, { _, h, m ->
                            val newCal = (endDate ?: startDate).clone() as Calendar
                            newCal.set(Calendar.HOUR_OF_DAY, h)
                            newCal.set(Calendar.MINUTE, m)
                            endDate = newCal
                        }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false).show()
                    },
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter,
                    isSet = endDate != null,
                    onClear = { endDate = null }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Toggles
                ToggleRow(title = "Camp Schedule Item", checked = isCampEvent, onCheckedChange = { isCampEvent = it })
                ToggleRow(title = "Published", checked = isPublished, onCheckedChange = { isPublished = it })

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (eventId == null) {
                            viewModel.createEvent(
                                title = title,
                                subtitle = subtitle.takeIf { it.isNotBlank() },
                                description = description,
                                location = location.takeIf { it.isNotBlank() },
                                registrationURL = registrationURL.takeIf { it.isNotBlank() },
                                category = category,
                                startDate = Timestamp(startDate.time),
                                endDate = endDate?.let { Timestamp(it.time) },
                                isCampEvent = isCampEvent,
                                isPublished = isPublished,
                                parentEventId = parentEventId,
                                eventKind = if (parentEventId != null) "scheduleItem" else "event"
                            )
                        } else {
                            existingEvent?.let {
                                viewModel.updateEvent(it.copy(
                                    title = title,
                                    subtitle = subtitle,
                                    description = description,
                                    location = location,
                                    registrationURL = registrationURL,
                                    category = category,
                                    startDate = Timestamp(startDate.time),
                                    endDate = endDate?.let { Timestamp(it.time) },
                                    isCampEvent = isCampEvent,
                                    isPublished = isPublished
                                ))
                            }
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(28.dp),
                    enabled = title.isNotBlank() && description.isNotBlank()
                ) {
                    Text(
                        text = if (eventId == null) {
                            if (parentEventId != null) "CREATE ITEM" else "CREATE EVENT"
                        } else "SAVE CHANGES",
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }

        if (showCategoryPicker) {
            PickerBottomSheet(
                title = "SELECT CATEGORY",
                options = EventCategory.entries.map { it.title },
                selectedOption = category.title,
                onOptionSelected = { title ->
                    category = EventCategory.entries.find { it.title == title } ?: EventCategory.other
                    showCategoryPicker = false
                },
                onDismiss = { showCategoryPicker = false }
            )
        }
    }
}

@Composable
fun DateTimeRow(
    label: String,
    calendar: Calendar,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat,
    isSet: Boolean = true,
    onClear: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(64.dp),
        color = Color.White.copy(0.05f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
            
            if (isSet) {
                Row(
                    modifier = Modifier
                        .clickable { onDateClick() }
                        .background(Color.White.copy(0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = dateFormatter.format(calendar.time), color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .clickable { onTimeClick() }
                        .background(Color.White.copy(0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = timeFormatter.format(calendar.time), color = Color.White, fontSize = 12.sp)
                }
                if (onClear != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White.copy(0.4f), modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                Text(
                    text = "SET",
                    color = Color(0xFF22D3EE),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onDateClick() }
                )
            }
        }
    }
}

