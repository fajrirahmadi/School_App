package com.jhy.project.schoollibrary.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetConfirmationBinding

const val confirmationBottomSheet = "confirmation_bottom_sheet"

class ConfirmationBottomSheet(
    private val title: String,
    private val description: String,
    private val actionYes: (() -> Unit)? = null,
    private val actionNo: (() -> Unit)? = null
) :
    BaseViewBindingBottomSheet<BottomsheetConfirmationBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleTv.text = title
        binding.descriptionTv.text = description
        binding.yesButton.setOnClickListener {
            actionYes?.invoke()
            dismiss()
        }
        binding.noButton.setOnClickListener {
            actionNo?.invoke()
            dismiss()
        }

    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetConfirmationBinding
        get() = BottomsheetConfirmationBinding::inflate

}