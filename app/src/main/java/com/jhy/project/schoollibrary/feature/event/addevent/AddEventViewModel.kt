package com.jhy.project.schoollibrary.feature.event.addevent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.extension.toMillis
import com.jhy.project.schoollibrary.model.Event
import com.jhy.project.schoollibrary.model.constant.Result
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var eventNameState by mutableStateOf("")
        private set

    fun updateEventNameState(text: String) {
        eventNameState = text
    }

    var eventPlaceState by mutableStateOf("")
        private set

    fun updateEventPlaceState(text: String) {
        eventPlaceState = text
    }

    var pesertaState by mutableStateOf("")
        private set

    fun updatePesertaState(text: String) {
        pesertaState = text
    }

    var eventAgendaState by mutableStateOf("")
        private set

    fun updateEventAgendaState(text: String) {
        eventAgendaState = text
    }

    var startDateState by mutableStateOf(System.currentTimeMillis().toDateFormat("dd MMMM yyyy"))
        private set

    fun updateStartDateState(date: Long) {
        startDateState = date.toDateFormat("dd MMMM yyyy")
    }

    var startTimeState by mutableStateOf(System.currentTimeMillis().toDateFormat("HH:mm"))
        private set

    fun updateStartTimeState(hour: String, minute: String) {
        val newMinute = String.format(Locale.getDefault(), "%02d", minute.toInt())
        startTimeState = "$hour:$newMinute"
    }

    var endDateState by mutableStateOf(System.currentTimeMillis().toDateFormat("dd MMMM yyyy"))
        private set

    fun updateEndDateState(date: Long) {
        endDateState = date.toDateFormat("dd MMMM yyyy")
    }

    var endTimeState by mutableStateOf(System.currentTimeMillis().toDateFormat("HH:mm"))
        private set

    fun updateEndTimeState(hour: String, minute: String) {
        val newMinute = String.format(Locale.getDefault(), "%02d", minute.toInt())
        endTimeState = "$hour:$newMinute"
    }

    fun submitEvent() {
        val startDate = "$startDateState $startTimeState".toMillis("dd MMMM yyyy HH:mm")
        val endDate = "$endDateState $endTimeState".toMillis("dd MMMM yyyy HH:mm")
        val event = Event(
            "",
            eventNameState,
            eventAgendaState,
            eventPlaceState,
            startDate,
            endDate,
            pesertaState,
            "",
            emptyList(),
            listOf(
                startDate.toDateFormat("yyyy-MM-dd"),
                endDate.toDateFormat("yyyy-MM-dd"),
                startDate.toDateFormat("yyyy-MM"),
                endDate.toDateFormat("yyyy-MM")
            )
        )
        when {
            eventNameState.isEmpty() -> {
                updateResult(Result.Error("Nama acara tidak boleh kosong"))
            }

            eventAgendaState.isEmpty() -> {
                updateResult(Result.Error("Agenda acara tidak boleh kosong"))
            }

            pesertaState.isEmpty() -> {
                updateResult(Result.Error("Peserta tidak boleh kosong"))
            }

            eventPlaceState.isEmpty() -> {
                updateResult(Result.Error("Lokasi acara tidak boleh kosong"))
            }

            startDate >= endDate -> {
                updateResult(Result.Error("Tanggal awal tidak boleh melebihi tanggal akhir"))
            }

            else -> {
                showLoading()
                db.submitEvent(event).addOnCompleteListener {
                    if (it.isSuccessful) {
                        updateResult(Result.Success("Berhasil menambahkan event"))
                    } else {
                        updateResult(Result.Error("Gagal menambahkan event"))
                    }
                    dismissLoading()
                }
            }
        }
    }
}