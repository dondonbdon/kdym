package dev.bti.kdym.ui.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    fun formatRelativeTime(timestamp: Timestamp?): String {
        if (timestamp == null) return ""
        
        val now = Calendar.getInstance()
        val time = Calendar.getInstance().apply { time = timestamp.toDate() }
        
        return when {
            isSameDay(now, time) -> {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate())
            }
            isYesterday(now, time) -> {
                "YESTERDAY"
            }
            isSameYear(now, time) -> {
                SimpleDateFormat("MMM d", Locale.getDefault()).format(timestamp.toDate()).uppercase()
            }
            else -> {
                SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(timestamp.toDate()).uppercase()
            }
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(now: Calendar, then: Calendar): Boolean {
        val yesterday = now.clone() as Calendar
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        return isSameDay(yesterday, then)
    }

    private fun isSameYear(now: Calendar, then: Calendar): Boolean {
        return now.get(Calendar.YEAR) == then.get(Calendar.YEAR)
    }
}
