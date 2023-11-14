package com.jhy.project.schoollibrary.component.compose.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.HorizontalSpace
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.extension.inSameMonth
import com.jhy.project.schoollibrary.extension.toDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    state: CalendarState,
    onDateSelected: (Date) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val selectedDateText = state.selectedDate?.time?.toDateFormat("yyyy-MM-dd")
    val monthText = state.currentMonth.toMonthText("MM")
    val monthYear = "${state.currentMonth.toMonthText()} ${state.currentYear}"
    Card(
        modifier = modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WorkSandTextMedium(
                    modifier = Modifier.weight(1f),
                    text = monthYear,
                    size = 20.sp,
                    align = TextAlign.Left
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onPrevious()
                    }
                )
                HorizontalSpace(width = 8.dp)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        onNext()
                    }
                )
            }
            VerticalSpace(height = 16.dp)
            LazyVerticalStaggeredGrid(
                StaggeredGridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                items(state.days) { day ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        WorkSandTextMedium(text = day)
                    }
                }
                items(state.dates) { item ->
                    val day = item.time.toDateFormat("yyyy-MM-dd")
                    val isSelectedDay = day == selectedDateText
                    val backgroundColor =
                        if (isSelectedDay) AppColor.blueSoft
                        else AppColor.white
                    val textColor =
                        if (day.split("-")[1] == monthText) AppColor.black
                        else AppColor.grey
                    val hasEvent = state.events.contains(day)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDateSelected(item) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(
                                    backgroundColor,
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            WorkSandTextMedium(
                                text = day.split("-").lastOrNull() ?: "",
                                color = textColor
                            )
                            VerticalSpace(height = 4.dp)
                            if (hasEvent) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            AppColor.red, CircleShape
                                        )
                                        .alpha(0f)
                                )
                            } else {
                                VerticalSpace(height = 4.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getDates(year: Int, month: Int, maxWeeks: Int): MutableList<Date> {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.SUNDAY // Set Sunday as the first day of the week
    calendar.set(year, month - 1, 1) // Month is 0-based, so subtract 1
    val daysInMonth = mutableListOf<Date>()

    // find start sunday
    while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        calendar.add(Calendar.DAY_OF_MONTH, -1)
    }

    var currentWeek = 1
    while (calendar.get(Calendar.MONTH) == month - 1 && currentWeek <= maxWeeks) {
        daysInMonth.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            currentWeek++
        }
    }

    // If the last week is not complete, add days from the next month
    while (currentWeek <= maxWeeks) {
        daysInMonth.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            currentWeek++
        }
    }

    return daysInMonth
}

fun getDays(): List<String> {
    return listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
}

fun Int.toMonthText(format: String = "MMMM"): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, this - 1)
    return calendar.timeInMillis.toDateFormat(format)
}

data class CalendarState(
    var selectedDate: Date? = null,
    var days: List<String> = getDays(),
    var dates: List<Date> = emptyList(),
    var events: List<String> = emptyList(),
    var currentMonth: Int = 1,
    var currentYear: Int = 2023
)

//
//@Preview
//@Composable
//fun CalendarAppPreview() {
//    MaterialTheme {
//        val year = 2023
//        val month = 11
//        CalendarView(
//            modifier = Modifier
//                .padding(16.dp),
//            state = CalendarState(
//                days = getDays(),
//                dates = emptyList(),
//                events = listOf("22112023"),
//                currentMonth = month,
//                currentYear = year
//            ),
//            onDateSelected = {},
//            onNext = {},
//            onPrevious = {}
//        )
//    }
//}