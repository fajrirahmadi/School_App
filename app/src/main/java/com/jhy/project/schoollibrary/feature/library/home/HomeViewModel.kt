package com.jhy.project.schoollibrary.feature.library.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.BookCategory
import com.jhy.project.schoollibrary.model.BookCounter
import com.jhy.project.schoollibrary.model.adapter.LibraryMenu
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.state.ErrorType
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.model.state.LibraryHomeUIState
import com.jhy.project.schoollibrary.model.state.UIState
import com.jhy.project.schoollibrary.model.toAvailableISBN
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.loadBooks
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var isAdmin by mutableStateOf(false)
        private set

    var homeLibState by mutableStateOf(LibraryHomeUIState())
        private set

    var code: String? = null

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

    fun onCreate() {
        if (isLogin) {
            loadUserData {
                isAdmin = it.role == admin
                homeLibState = homeLibState.copy(
                    menuState = UIState.Success(
                        LibraryMenu.values()
                            .filter { menu ->
                                isAdmin or (menu != LibraryMenu.DaftarPengguna)
                            })
                )
            }
        } else {
            homeLibState.menuState = UIState.Error(ErrorType.EmptyData)
        }

        loadCounter()
        loadBookList()
    }

    private var counterJob: Job? = null
    private fun loadCounter() {
        counterJob?.cancel()
        counterJob = viewModelScope.launch {
            observeStatefulDoc<BookCounter>(
                db.loadBookCounter()
            ).collect {
                if (it is FirestoreState.Success) {
                    homeLibState = homeLibState.copy(
                        counterState = it.data?.count ?: 0
                    )
                }
            }
        }
    }

    private var booksJobs: Job? = null
    private fun loadBookList() {
        booksJobs?.cancel()
        booksJobs = viewModelScope.launch {

            val keywords = homeLibState.keywordState.toAvailableISBN().firstOrNull() ?: ""
            code = homeLibState.keywordState.toAvailableISBN().getOrNull(1)

            observeStatefulCollection<Book>(
                db.loadBooks(getFilterBySelected(), keywords)
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> {
                        homeLibState = homeLibState.copy(
                            bookState = UIState.Error(ErrorType.NetworkError)
                        )
                    }

                    is FirestoreState.Loading -> {
                        homeLibState = homeLibState.copy(
                            bookState = UIState.Loading
                        )
                    }

                    is FirestoreState.Success -> {
                        homeLibState = homeLibState.copy(
                            bookState = UIState.Success(it.data)
                        )
                    }
                }
            }
        }
    }

    private fun getFilterBySelected(): List<String> {
        return when (homeLibState.filterState) {
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
                homeLibState.filterState.toString()
            )
            else -> emptyList()
        }
    }

    fun doSearch(keyword: String = "") {
        if (keyword.isNotEmpty() && keyword.length < 3) return
        homeLibState = homeLibState.copy(
            keywordState = keyword
        )
        loadBookList()
    }

    fun updateSelectedFilter(filter: BookCategory) {
        homeLibState = homeLibState.copy(
            filterState = filter
        )
        loadBookList()
    }
}