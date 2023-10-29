package com.jhy.project.schoollibrary.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.UnSelectedButton
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.databinding.BottomsheetComposeBinding

class ChooserBottomSheet(
    private val title: String = "",
    private val list: List<String>,
    private val action: (String) -> Unit
) : BaseViewBindingBottomSheet<BottomsheetComposeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetComposeBinding
        get() = BottomsheetComposeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                WorkSandTextMedium(text = title, color = AppColor.black, size = 18.sp)
                VerticalSpace(height = 16.dp)
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(list) {
                        UnSelectedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp), text = it
                        ) {
                            action.invoke(it)
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun isDraggable(): Boolean {
        return false
    }
}