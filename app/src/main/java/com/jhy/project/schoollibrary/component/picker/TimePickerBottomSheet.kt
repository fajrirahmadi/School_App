package com.jhy.project.schoollibrary.component.picker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetPickerBinding

class TimePickerBottomSheet(
    private val hour: String,
    private val minute: String,
    private val onTimePicked: (String, String) -> Unit
) :
    BaseViewBindingBottomSheet<BottomsheetPickerBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureDatePicker()
        binding.pickerTitle.text = "Select Time"
        binding.applyButton.text = "Set Time"

        binding.applyButton.setOnClickListener {
            onTimePicked(
                binding.firstPicker.value.toString(),
                binding.secondPicker.value.toString()
            )
            dialog?.dismiss()
        }
        binding.firstPicker.value = hour.toInt()
        binding.secondPicker.value = minute.toInt()
    }

    private fun configureDatePicker() {
        binding.firstPicker.apply {
            isVisible = true
            maxValue = 23
            minValue = 0
        }
        binding.secondPicker.apply {
            maxValue = 59
            minValue = 0
        }
        binding.thirdPicker.apply {
            isVisible = false
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetPickerBinding
        get() = BottomsheetPickerBinding::inflate

    override fun getSheetState(): Int {
        return BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun isDraggable(): Boolean {
        return false
    }
}