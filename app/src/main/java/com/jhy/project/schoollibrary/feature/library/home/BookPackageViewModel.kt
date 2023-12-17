package com.jhy.project.schoollibrary.feature.library.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.state.BookPackageUIState
import com.jhy.project.schoollibrary.model.state.ErrorType
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.model.state.UIState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.loadBookList
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookPackageViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var bookPackageState by mutableStateOf(BookPackageUIState())
        private set

    fun updateFilter(filter: String) {
        bookPackageState = bookPackageState.copy(
            filterState = filter
        )
        loadBookPackage()
    }

    private var booksJob: Job? = null
    fun loadBookPackage() {
        booksJob?.cancel()
        booksJob = viewModelScope.launch {
            observeStatefulCollection<Book>(
                db.loadBookList(bookPackageState.filterState)
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> {
                        bookPackageState = bookPackageState.copy(
                            booksState = UIState.Error(ErrorType.NetworkError)
                        )
                    }
                    is FirestoreState.Loading -> {
                        bookPackageState = bookPackageState.copy(
                            booksState = UIState.Loading
                        )
                    }
                    is FirestoreState.Success -> {
                        bookPackageState = bookPackageState.copy(
                            booksState = UIState.Success(it.data)
                        )
                    }
                }
            }
        }
    }
}