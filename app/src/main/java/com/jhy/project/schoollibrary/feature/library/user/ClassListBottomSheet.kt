package com.jhy.project.schoollibrary.feature.library.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetListBinding
import com.jhy.project.schoollibrary.extension.initVerticalAdapter
import com.jhy.project.schoollibrary.model.KelasCategory
import com.jhy.project.schoollibrary.model.adapter.KelasCategoryAdapter
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

const val classListBottomSheet = "class_list_bottom_sheet"

class ClassListBottomSheet(val pickedClass: (data: KelasCategory) -> Unit) :
    BaseViewBindingBottomSheet<BottomsheetListBinding>() {

    private val _kelasAdapter = ItemAdapter<KelasCategoryAdapter>()
    val kelasAdapter by lazy {
        FastAdapter.with(_kelasAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            searchTextInput.isVisible = false
            listRv.initVerticalAdapter(requireContext(), kelasAdapter)
        }

        kelasAdapter.onClickListener = { _, _, data, _ ->
            pickedClass.invoke(data.data)
            dismiss()
            true
        }

        initAdapter()
    }

    private fun initAdapter() {
        for (data in KelasCategory.values().filter { it != KelasCategory.ALL }) {
            _kelasAdapter.add(KelasCategoryAdapter(data))
        }
        kelasAdapter.notifyAdapterDataSetChanged()
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetListBinding
        get() = BottomsheetListBinding::inflate
}