package com.jhy.project.schoollibrary.feature.library.home

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.BookCategory
import com.jhy.project.schoollibrary.model.BookCounter
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import com.jhy.project.schoollibrary.model.adapter.FilterAdapter
import com.jhy.project.schoollibrary.model.adapter.LibraryMenu
import com.jhy.project.schoollibrary.model.adapter.LibraryMenuAdapter
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var selectedFilter = BookCategory.All
    var keyword = ""

    private val _libraryMenuAdapter = ItemAdapter<LibraryMenuAdapter>()
    val homeMenuAdapter by lazy {
        FastAdapter.with(_libraryMenuAdapter)
    }

    private val _filterAdapter = ItemAdapter<FilterAdapter>()
    val filterAdapter by lazy {
        FastAdapter.with(_filterAdapter)
    }

    private val bookList = mutableListOf<Book>()
    private val _bookAdapter = ItemAdapter<BookAdapter>()
    val bookAdapter by lazy {
        FastAdapter.with(_bookAdapter)
    }

    private val _bookCount = MutableStateFlow(0)
    val bookCountState = _bookCount.asStateFlow()

    fun onCreate(online: Boolean = false) {
        initFilterAdapter()
        loadUserData()
        loadCounter()
        loadBookList(online)
    }

    private fun loadCounter(online: Boolean = false) {
        db.loadBookCounter(online).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.toObject(BookCounter::class.java)?.let { bookCount ->
                    _bookCount.value = bookCount.count
                }
                if (!online) loadCounter(true)
            }
        }
    }

    fun initHomeMenuAdapter(admin: Boolean) {
        if (_libraryMenuAdapter.adapterItemCount > 0) return
        for (data in LibraryMenu.values().filter { admin || it != LibraryMenu.DaftarPengguna }) {
            _libraryMenuAdapter.add(LibraryMenuAdapter(data))
        }
        homeMenuAdapter.notifyAdapterDataSetChanged()
    }

    private fun initFilterAdapter() {
        _filterAdapter.clear()
        val filters = listOf(
            BookCategory.All,
            BookCategory.Kelas7,
            BookCategory.Kelas8,
            BookCategory.Kelas9,
            BookCategory.Umum,
            BookCategory.Sejarah,
            BookCategory.Agama,
            BookCategory.Novel
        )
        for (filter in filters) {
            _filterAdapter.add(FilterAdapter(filter, filter == selectedFilter))
        }
        filterAdapter.notifyAdapterDataSetChanged()

        filterAdapter.onClickListener = { _, _, data, position ->
            if (!data.choose) {
                for ((index, item) in _filterAdapter.adapterItems.withIndex()) {
                    if (item.choose) {
                        _filterAdapter.getAdapterItem(index).choose = false
                        break
                    }
                }
                selectedFilter = data.filter
                _filterAdapter.getAdapterItem(position).choose = true
                filterAdapter.notifyAdapterDataSetChanged()
                resetPage()
            }
            true
        }
    }

    private fun resetPage() {
        _bookAdapter.clear()
        bookAdapter.notifyAdapterDataSetChanged()
        loadBookList(true)
    }

    private fun loadBookList(online: Boolean = false) {
        showLoading(_bookAdapter.adapterItemCount == 0)
        db.loadBookList(getFilterBySelected(), keyword = keyword, online = online, limit = 50)
            .addOnCompleteListener {
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

    private fun getFilterBySelected(): List<String> {
        return when (selectedFilter) {
            BookCategory.Kelas7 -> listOf(
                BookCategory.Kelas7.toString(),
                BookCategory.Kelas7_1.toString(),
                BookCategory.Kelas7_2.toString()
            )
            BookCategory.Kelas8 -> listOf(
                BookCategory.Kelas8.toString(),
                BookCategory.Kelas8_1.toString(),
                BookCategory.Kelas8_2.toString()
            )
            BookCategory.Kelas9 -> listOf(
                BookCategory.Kelas9.toString(),
                BookCategory.Kelas9_1.toString(),
                BookCategory.Kelas9_2.toString()
            )
            BookCategory.Novel -> listOf(
                BookCategory.Novel.toString(),
                BookCategory.NovelFiksi.toString()
            )
            BookCategory.Umum,
            BookCategory.Agama,
            BookCategory.Sejarah -> listOf(
                selectedFilter.toString()
            )
            else -> emptyList()
        }
    }

    fun showBookAdapter(firstPage: Boolean = true) {
        if (firstPage) _bookAdapter.clear()
        val startIndex = _bookAdapter.adapterItemCount
        for ((index, data) in bookList.filterIndexed { index, _ -> index >= startIndex }
            .withIndex()) {
            _bookAdapter.add(BookAdapter(data))
            if (index == 19) break
        }
        bookAdapter.notifyAdapterDataSetChanged()
        postDelayed { dismissLoading() }
    }

    fun doSearch(keyword: String = "") {
        if (this.keyword == keyword) return
        this.keyword = keyword
        resetPage()
    }

    fun filterIndex(): Int {
        return selectedFilter.ordinal
    }
}