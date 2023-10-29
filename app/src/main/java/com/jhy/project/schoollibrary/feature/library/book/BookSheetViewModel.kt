package com.jhy.project.schoollibrary.feature.library.book

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookSheetViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var keyword = ""

    private val bookList = mutableListOf<Book>()
    private val _bookAdapter = ItemAdapter<BookAdapter>()
    val bookAdapter by lazy {
        FastAdapter.with(_bookAdapter)
    }

    fun loadBookList(online: Boolean = false) {
        showLoading()
        db.loadBookList(online = online, keyword = keyword).addOnCompleteListener {
            if (it.isSuccessful) {
                bookList.clear()
                bookList.addAll(it.result.toObjects(Book::class.java))
                if (bookList.isEmpty() && !online) {
                    loadBookList(true)
                    return@addOnCompleteListener
                }
                showBookAdapter()
            } else {
                dismissLoading()
            }
        }
    }

    fun showBookAdapter(firstPage: Boolean = true) {
        if (firstPage) _bookAdapter.clear()
        val list = bookList.filter {
            (it.judul?.contains(keyword, true) == true ||
                    it.isbn?.contains(keyword, true) == true)
        }

        val startIndex = _bookAdapter.adapterItemCount
        for ((index, data) in list.filterIndexed { index, _ -> index >= startIndex }
            .withIndex()) {
            _bookAdapter.add(BookAdapter(data))
            if (index == 19) break
        }
        bookAdapter.notifyAdapterDataSetChanged()
        postDelayed { dismissLoading() }
    }

    fun doSearch(keyword: String) {
        this.keyword = keyword
        loadBookList(true)
    }
}