package com.jhy.project.schoollibrary.feature.library.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetListBinding
import com.jhy.project.schoollibrary.extension.initVerticalAdapter
import com.jhy.project.schoollibrary.model.BookCategory
import com.jhy.project.schoollibrary.model.adapter.BookCategoryAdapter
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

const val bookCategoryBottomSheet = "book_category_bottom_sheet"

class BookCategoryBottomSheet(private val pickCategory: ((data: BookCategory) -> Unit)) :
    BaseViewBindingBottomSheet<BottomsheetListBinding>() {

    private val _adapter = ItemAdapter<BookCategoryAdapter>()
    private val adapter by lazy {
        FastAdapter.with(_adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()
        bindAdapter()

    }

    private fun bindAdapter() {
        for (data in BookCategory.values().filter { it != BookCategory.All }) {
            _adapter.add(BookCategoryAdapter(data))
        }
        adapter.notifyAdapterDataSetChanged()
        adapter.onClickListener = { _, _, data, _ ->
            pickCategory.invoke(data.data)
            dismiss()
            true
        }
    }

    private fun bindView() {
        binding.apply {
            titleTv.text = "Kategori Buku"
            listRv.initVerticalAdapter(requireContext(), adapter)
            searchTextInput.isVisible = false
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetListBinding
        get() = BottomsheetListBinding::inflate
}