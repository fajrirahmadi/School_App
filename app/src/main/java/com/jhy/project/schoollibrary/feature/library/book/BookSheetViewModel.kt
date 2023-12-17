package com.jhy.project.schoollibrary.feature.library.book

import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.model.toAvailableISBN
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.loadBookList
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSheetViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    private var keyword = ""
    var filter: String? = null
    var code: String? = null

    private val bookList = mutableListOf<Book>()
    private val _bookAdapter = ItemAdapter<BookAdapter>()
    val bookAdapter by lazy {
        FastAdapter.with(_bookAdapter)
    }

    private var booksJob: Job? = null
    fun loadBookList() {
        val keywords = keyword.toAvailableISBN().firstOrNull() ?: ""
        code = keyword.toAvailableISBN().getOrNull(1)

        booksJob?.cancel()
        booksJob = viewModelScope.launch {
            observeStatefulCollection<Book>(
                db.loadBookList(filter?.split("_")?.firstOrNull(), keyword = keywords)
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> {
                        showLoading(false)
                    }
                    is FirestoreState.Loading -> {
                        showLoading()
                    }
                    is FirestoreState.Success -> {
                        bookList.apply {
                            clear()
                            addAll(it.data.asList())
                        }
                        showBookAdapter()
                        postDelayed { showLoading(false) }
                    }
                }
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
    }

    fun doSearch(keyword: String) {
        this.keyword = keyword
        loadBookList()
    }
}