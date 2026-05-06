package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale


data class KDYMEvent(
    val id: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val description: String = "",
    val location: String? = null,
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp? = null,
    val imageURL: String? = null,
    val registrationURL: String? = null,
    val category: EventCategory = EventCategory.other,
    val isCampEvent: Boolean = false,
    val campId: String? = null,
    val isPublished: Boolean = true,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    val dateRangeText: String
        get() {
            val formatter = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
            val start = startDate?.toDate()?.let { formatter.format(it) } ?: ""
            val end = endDate?.toDate()?.let { formatter.format(it) }
            return if (end != null) "$start - $end" else start
        }

    val dayText: String
        get() = startDate?.toDate()
            ?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it).uppercase() } ?: ""

    val monthText: String
        get() = startDate?.toDate()
            ?.let { SimpleDateFormat("MMM", Locale.getDefault()).format(it).uppercase() } ?: ""

    val dayNumberText: String
        get() = startDate?.toDate()?.let { SimpleDateFormat("d", Locale.getDefault()).format(it) }
            ?: ""

    val timeText: String
        get() {
            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            val start = startDate?.toDate()?.let { formatter.format(it) } ?: ""
            val end = endDate?.toDate()?.let { formatter.format(it) }
            return if (end != null) "$start - $end" else start
        }
}
