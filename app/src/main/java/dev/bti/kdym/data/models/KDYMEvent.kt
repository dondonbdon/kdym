package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import dev.bti.kdym.data.local.serializers.TimestampSerializer
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Represents a calendar event, such as a rally, service, or camp schedule item.
 *
 * @property id Unique identifier for the event.
 * @property title Name of the event.
 * @property subtitle Short descriptive line below the title.
 * @property description Detailed information about the event.
 * @property location Physical or virtual venue.
 * @property startDate When the event begins.
 * @property endDate Optional end time.
 * @property imageURL Optional promotional image URL.
 * @property registrationURL Optional link to external registration system.
 * @property category Classification of the event (e.g., service, rally).
 * @property isCampEvent If true, this event is part of a specific camp session.
 * @property campId Associated camp session ID.
 * @property isPublished Whether the event is visible to users.
 * @property createdBy UID of the creator.
 * @property createdAt Timestamp of creation.
 * @property updatedAt Last modification timestamp.
 */
@Serializable
@IgnoreExtraProperties
data class KDYMEvent(
    @DocumentId
    val id: String = "",
    val parentEventId: String? = null,
    val eventKind: String = "event", // "event" or "scheduleItem"
    val seedKey: String? = null,
    val title: String = "",
    val subtitle: String? = null,
    val description: String = "",
    val location: String? = null,
    @Serializable(with = TimestampSerializer::class)
    val startDate: Timestamp = Timestamp.now(),
    @Serializable(with = TimestampSerializer::class)
    val endDate: Timestamp? = null,
    val imageURL: String? = null,
    val registrationURL: String? = null,
    val category: EventCategory = EventCategory.other,
    val campId: String? = null,
    val createdBy: String? = null,
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp? = null,
    @Serializable(with = TimestampSerializer::class)
    val updatedAt: Timestamp? = null,
    @get:PropertyName("isCampEvent")
    @set:PropertyName("isCampEvent")
    var isCampEvent: Boolean = false,
    @get:PropertyName("isPublished")
    @set:PropertyName("isPublished")
    var isPublished: Boolean = false,
    var pushError: String? = null,
    @Serializable(with = TimestampSerializer::class)
    var pushSentAt: Timestamp? = null

) {
    /**
     * Formatted string representing the full date and time range.
     */
    val dateRangeText: String
        get() {
            val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
            val start = startDate.toDate().let { formatter.format(it) }
            val end = endDate?.toDate()?.let { formatter.format(it) }
            return if (end != null) "$start - $end" else start
        }

    /**
     * Short day name (e.g., MON, TUE).
     */
    val dayText: String
        get() = startDate.toDate()
            .let { SimpleDateFormat("EEE", Locale.getDefault()).format(it).uppercase() }

    /**
     * Short month name (e.g., JAN, FEB).
     */
    val monthText: String
        get() = startDate.toDate()
            .let { SimpleDateFormat("MMM", Locale.getDefault()).format(it).uppercase() }

    /**
     * Day of the month as a string.
     */
    val dayNumberText: String
        get() = startDate.toDate().let { SimpleDateFormat("d", Locale.getDefault()).format(it) }

    /**
     * Formatted string representing the time range (start - end).
     */
    val timeText: String
        get() {
            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            val start = startDate.toDate().let { formatter.format(it) }
            val end = endDate?.toDate()?.let { formatter.format(it) }
            return if (end != null) "$start - $end" else start
        }

    val kdymDateRangeText: String
        get() {
            val start = startDate.toDate()
            val end = endDate?.toDate() ?: start
            val calendar = Calendar.getInstance()
            
            val sdfFull = SimpleDateFormat("MMM d, yyyy", Locale.US)
            val sdfMonthDay = SimpleDateFormat("MMM d", Locale.US)

            calendar.time = start
            val startYear = calendar.get(Calendar.YEAR)
            val startDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            
            calendar.time = end
            val endYear = calendar.get(Calendar.YEAR)
            val endDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

            if (startYear == endYear && startDayOfYear == endDayOfYear) {
                return sdfFull.format(start)
            }

            return if (startYear == endYear) {
                "${sdfMonthDay.format(start)} - ${sdfMonthDay.format(end)}, $endYear"
            } else {
                "${sdfFull.format(start)} - ${sdfFull.format(end)}"
            }
        }

    val kdymTimeText: String
        get() {
            val formatter = SimpleDateFormat("h:mm a", Locale.US)
            val start = startDate.toDate()
            val end = endDate?.toDate() ?: start
            
            val calendar = Calendar.getInstance()
            calendar.time = start
            val startDay = calendar.get(Calendar.DAY_OF_YEAR)
            calendar.time = end
            val endDay = calendar.get(Calendar.DAY_OF_YEAR)

            return if (startDay == endDay) {
                "${formatter.format(start)} - ${formatter.format(end)}"
            } else {
                formatter.format(start)
            }
        }

    val scheduleSeriesKey: String
        get() {
            val joined = listOfNotNull(title, subtitle, description, location)
                .joinToString(" ")
                .lowercase()

            if (joined.contains("kyc") || joined.contains("kansas youth convention")) {
                return "kyc-2026"
            }

            if (joined.contains("outpour") || joined.contains("heartland") || joined.contains("camp")) {
                return "outpour-camp-2026"
            }

            return SimpleDateFormat("yyyy-MM", Locale.US).format(startDate.toDate())
        }
}
