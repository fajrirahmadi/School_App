package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterBookBinding
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.model.Book
import com.mikepenz.fastadapter.binding.AbstractBindingItem

data class BookAdapter(
    val book: Book,
    val showRemove: Boolean = false
) :
    AbstractBindingItem<AdapterBookBinding>() {

    override val type: Int
        get() = R.id.book_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterBookBinding {
        return AdapterBookBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterBookBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            coverIv.setImage(book.image, R.drawable.placeholder_book)
            titleTv.text = book.judul
            removeBtn.isVisible = showRemove
        }
    }

    override fun unbindView(binding: AdapterBookBinding) {
        super.unbindView(binding)
        binding.coverIv.setImageResource(0)
    }

}