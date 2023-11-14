package com.jhy.project.schoollibrary.feature.library.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.BookCategory
import com.jhy.project.schoollibrary.model.BookCounter
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import com.jhy.project.schoollibrary.model.adapter.FilterAdapter
import com.jhy.project.schoollibrary.model.adapter.LibraryMenu
import com.jhy.project.schoollibrary.model.adapter.LibraryMenuAdapter
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var isAdmin by mutableStateOf(false)
        private set

    var libMenuItems by mutableStateOf<List<LibraryMenu>>(emptyList())
        private set

    var keyword by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf(BookCategory.All)
        private set

    var filters by mutableStateOf(
        listOf(
            BookCategory.All,
            BookCategory.Kelas7,
            BookCategory.Kelas8,
            BookCategory.Kelas9,
            BookCategory.Umum,
            BookCategory.Sejarah,
            BookCategory.Agama,
            BookCategory.Novel
        )
    )

    var bookCount by mutableIntStateOf(0)
        private set

    var bookList by mutableStateOf<List<Book>>(emptyList())
        private set

    fun onCreate(online: Boolean = false) {
        loadUserData {
            isAdmin = it.role == admin
            libMenuItems = LibraryMenu.values()
                .filter { menu ->
                    isAdmin or (menu != LibraryMenu.DaftarPengguna)
                }
        }
        loadCounter(online)
        loadBookList(online)
    }

    private fun loadCounter(online: Boolean = false) {
        db.loadBookCounter(online).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.toObject(BookCounter::class.java)?.let { bookCount ->
                    this.bookCount = bookCount.count
                }
            }
            if (!online) loadCounter(true)
        }
    }

    private fun resetPage() {
        loadBookList(true)
    }

    private fun loadBookList(online: Boolean = false) {
        showLoading(bookList.isEmpty())
        db.loadBookList(getFilterBySelected(), keyword = keyword, online = online, limit = 50)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    bookList = it.result.toObjects(Book::class.java)
                    if (bookList.isEmpty() && !online) {
                        loadBookList(true)
                        return@addOnCompleteListener
                    }
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

    fun doSearch(keyword: String = "") {
        if (keyword.isNotEmpty() && keyword.length < 3) return
        this.keyword = keyword
        resetPage()
    }

    fun updateSelectedFilter(item: BookCategory) {
        selectedFilter = item
        resetPage()
    }
}