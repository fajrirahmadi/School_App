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