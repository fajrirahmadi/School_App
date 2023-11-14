package com.jhy.project.schoollibrary.extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toIDRFormat(): String {
    return "IDR " + this.toDotsFormat()
}

fun Long.toDotsFormat(): String {
    return String.format("%,.0f", this.toDouble()).replace(",", ".")
}

fun Long.toDateFormat(format: String): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(Date(this))
}

fun Long.roundUpToNearestThousand(): Long {
    return ((this + 999) / 1000) * 1000
}

fun Long.toChatDate(): String {
    return when {
        this.toDateFormat("ddMMyyyy") == currentDateMillis.toDateFormat("ddMMyyyy") -> "Today"
        else -> this.toDateFormat("dd/MM/yyyy")
    }
}

fun Long.toMoment(): String {
    val interval = (System.currentTimeMillis() - this) / 1000
    return when {
        interval > 7 * 86400 -> this.toDateFormat("dd/MM/yyyy")
        interval > 86400 -> (interval / 86400).toString() + "d ago"
        interval > 3600 -> ((interval % 86400) / 3600).toString() + "h ago"
        interval > 60 -> ((interval % 3600) / 60).toString() + "m ago"
        else -> "Just now"
    }
}

fun Long.isSameDate(other: Long, format: String = "ddMMyyyy"): Boolean {
    return this.toDateFormat(format) == other.toDateFormat(format)
}