package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents a specific camp session or event year.
 *
 * @property id Unique identifier for the camp session.
 * @property name Official name of the camp.
 * @property theme Spiritual or decorative theme for the session.
 * @property year Calendar year the camp occurs.
 * @property startDate Beginning of the camp session.
 * @property endDate End of the camp session.
 * @property location Physical address or venue name.
 * @property isActive Whether this is the current or upcoming session.
 * @property createdAt Registration date in the system.
 */
data class Camp(
    val id: String = "",
    val name: String = "",
    val theme: String? = null,
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val location: String? = null,
    val isActive: Boolean = true,
    
    val verse: String? = null,
    val verseReference: String? = null,
    val verseTagline: String? = null,
    val description: String? = null,
    val subtitle: String? = null,
    val yearText: String? = null,
    val romanYear: String? = null,
    val accentColor: String = "#EF4444",
    val secondaryColor: String? = null,
    val historyPhotos: List<String> = emptyList(),

    val createdAt: Timestamp? = null
) {
    /**
     * Formatted string representing the date range of the camp.
     */
    val dateText: String
        get() {
            val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val start = startDate?.toDate()?.let { formatter.format(it) } ?: ""
            val end = endDate?.toDate()?.let { formatter.format(it) } ?: ""
            return if (end.isNotEmpty()) "$start - $end" else start
        }
}
