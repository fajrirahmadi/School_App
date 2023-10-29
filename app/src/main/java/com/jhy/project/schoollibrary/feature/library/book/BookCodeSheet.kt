package com.jhy.project.schoollibrary.feature.library.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.component.compose.EditText
import com.jhy.project.schoollibrary.databinding.BottomsheetComposeBinding
import com.jhy.project.schoollibrary.extension.isNumeric

class BookCodeSheet(private val action: (String) -> Unit) :
    BaseViewBindingBottomSheet<BottomsheetComposeBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetComposeBinding
        get() = BottomsheetComposeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            var searchText by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                EditText(
                    placeholder = "Input Kode Buku",
                    keyboardType = KeyboardType.Number,
                    text = searchText, onTextChange = {
                        if (it.length <= 3 && it.isNumeric()) searchText =
                            it
                    }) {
                    action.invoke(searchText)
                    dismiss()
                }
            }
        }
    }
}