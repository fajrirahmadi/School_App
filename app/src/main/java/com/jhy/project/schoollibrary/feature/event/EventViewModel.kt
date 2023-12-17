package com.jhy.project.schoollibrary.feature.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.component.compose.calendar.CalendarState
import com.jhy.project.schoollibrary.component.compose.calendar.getDates
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.extension.generateDateBetween
import com.jhy.project.schoollibrary.extension.isDateSameAs
import com.jhy.project.schoollibrary.extension.isInRange
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.Event
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var isAdmin by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var calendarState by mutableStateOf(CalendarState())
        private set

    var eventListState by mutableStateOf<List<Event>>(emptyList())
        private set

    private var events = mutableListOf<Event>()

    fun onCreate() {
        loadUserData {
            isAdmin = it.role == admin
        }
        onDateSelected(calendarState.selectedDate ?: Date())
    }

    private fun checkAvailableEvent() {
        val selectedDate = calendarState.selectedDate?.time ?: 0L
        eventListState = events.filter {
            selectedDate.isInRange(it.startDate, it.endDate)
        }
    }

    fun onDateSelected(date: Date) {
        if (!date.isDateSameAs(
                calendarState.selectedDate ?: Date(),
                "MMyyyy"
            ) || calendarState.selectedDate == null
        ) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date.time
            updateCalendar(calendar)
        }
        calendarState = calendarState.copy(
            selectedDate = date
        )
        checkAvailableEvent()
    }

    fun onPreviousMonthClicked() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, calendarState.currentYear)
        calendar.set(Calendar.MONTH, calendarState.currentMonth - 1)
        calendar.add(Calendar.MONTH, -1)
        updateCalendar(calendar)
    }

    fun onNextMonthClicked() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, calendarState.currentYear)
        calendar.set(Calendar.MONTH, calendarState.currentMonth - 1)
        calendar.add(Calendar.MONTH, 1)
        updateCalendar(calendar)
    }

    private fun updateCalendar(calendar: Calendar) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        calendarState = calendarState.copy(
            currentYear = year,
            currentMonth = month
        )
        viewModelScope.launch {
            val dates = withContext(Dispatchers.IO) {
                getDates(year, month, 5)
            }
            calendarState = calendarState.copy(
                dates = dates
            )
        }
        loadEventByMonth(calendar.timeInMillis.toDateFormat("yyyy-MM"))
    }

    var eventJob: Job? = null
    private fun loadEventByMonth(filter: String) {
        isLoading = true
        eventJob?.cancel()
        eventJob = viewModelScope.launch {
            observeStatefulCollection<Event>(
                db.loadEventMyMonth(filter)
            ).collect {
                if (it is FirestoreState.Success) {
                    events = it.data.asList<Event>().toMutableList()
                    calendarState = calendarState.copy(
                        events = events.joinToString(",") { event ->
                            event.startDate.generateDateBetween(event.endDate)
                        }
                    )
                    checkAvailableEvent()
                }
                isLoading = false
                postDelayed { showLoading(false) }
            }
        }
    }
}