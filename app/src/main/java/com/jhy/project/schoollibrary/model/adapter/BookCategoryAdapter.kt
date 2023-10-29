package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterSingleBinding
import com.jhy.project.schoollibrary.model.BookCategory
import com.mikepenz.fastadapter.binding.AbstractBindingItem


data class BookCategoryAdapter(
    val data: BookCategory
) :
    AbstractBindingItem<AdapterSingleBinding>() {

    override val type: Int
        get() = R.id.book_category_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterSingleBinding {
        return AdapterSingleBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterSingleBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            menuTv.text = data.title
        }
    }

}