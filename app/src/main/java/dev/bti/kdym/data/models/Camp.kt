package dev.bti.kdym.data.models

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class Camp(
    val id: String = "",
    val name: String = "",
    val theme: String? = null,
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val location: String? = null,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null
) {
    val dateText: String
        get() {
            val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val start = startDate?.toDate()?.let { formatter.format(it) } ?: ""
            val end = endDate?.toDate()?.let { formatter.format(it) } ?: ""
            return if (end.isNotEmpty()) "$start - $end" else start
        }
}
