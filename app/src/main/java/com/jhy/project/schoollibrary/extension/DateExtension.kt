package com.jhy.project.schoollibrary.extension

import java.util.*

val currentDateMillis = System.currentTimeMillis()
const val sevenDay = 7 * 3600 * 1000

fun getCurrentYear(): Int {
    return Calendar.getInstance().timeInMillis.toDateFormat("yyyy").toInt()
}

fun getNextYear(): Int {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.YEAR, 1)
    return calendar.timeInMillis.toDateFormat("yyyy").toInt()
}

fun Date.isDateSameAs(other: Date, format: String): Boolean {
    return this.time.toDateFormat(format) == other.time.toDateFormat(format)
}

fun Date.inSameMonth(year: Int, month: Int): Boolean {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)
    calendar.add(Calendar.DAY_OF_MONTH, -1)

    val firstDate = calendar.time

    val secondCalendar = Calendar.getInstance()
    secondCalendar.time = firstDate
    secondCalendar.add(Calendar.MONTH, 1)
    secondCalendar.add(Calendar.DAY_OF_MONTH, -1)
    val lastDate = secondCalendar.time

    return !this.before(firstDate) && !this.after(lastDate)
}