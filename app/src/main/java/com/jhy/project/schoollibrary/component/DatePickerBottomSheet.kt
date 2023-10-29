package com.jhy.project.schoollibrary.component

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetPickerBinding
import com.jhy.project.schoollibrary.extension.getCurrentYear
import com.jhy.project.schoollibrary.extension.getNextYear
import java.util.*

const val dateBottomSheet = "date_bottom_sheet"

class DatePickerBottomSheet(
    private val startDate: Int = getCurrentYear(),
    private val endDate: Int = getNextYear(),
    private val date: Long,
    private val onDatePicked: (date: Long) -> Unit
) :
    BaseViewBindingBottomSheet<BottomsheetPickerBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            pickerTitle.text = "Pilih Tanggal"
            applyButton.apply {
                text = "Simpan"
                setOnClickListener {
                    val date = Calendar.getInstance()
                    date.set(Calendar.YEAR, binding.thirdPicker.value)
                    date.set(Calendar.MONTH, binding.secondPicker.value)
                    date.set(Calendar.DAY_OF_MONTH, binding.firstPicker.value)
                    onDatePicked.invoke(date.timeInMillis)
                    dismiss()
                }
            }
        }
        configureDatePicker()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        binding.secondPicker.value = calendar.get(Calendar.MONTH)
        binding.thirdPicker.value = calendar.get(Calendar.YEAR)
        updateFirstPicker()
        binding.firstPicker.value = calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun configureDatePicker() {
        binding.secondPicker.apply {
            maxValue = 11
            minValue = 0
            displayedValues = monthList
            setOnValueChangedListener { _, _, _ ->
                updateFirstPicker()
            }
        }
        binding.thirdPicker.apply {
            maxValue = endDate
            minValue = startDate
            wrapSelectorWheel = false
        }
    }

    private fun updateFirstPicker() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, binding.secondPicker.value)
        calendar.set(Calendar.YEAR, binding.thirdPicker.value)
        binding.firstPicker.apply {
            val max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            value = if (value > max) max else value
            minValue = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
            maxValue = max
        }
    }

    override fun getSheetState(): Int {
        return BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun isDraggable(): Boolean {
        return false
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetPickerBinding
        get() = BottomsheetPickerBinding::inflate

    private val monthList by lazy {
        arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )
    }
}