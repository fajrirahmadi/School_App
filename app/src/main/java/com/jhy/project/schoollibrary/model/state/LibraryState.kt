package com.jhy.project.schoollibrary.model.state

import com.jhy.project.schoollibrary.model.BookCategory
import kotlinx.coroutines.flow.MutableStateFlow

data class LibraryHomeUIState(
    var menuState: UIState = UIState.Loading,
    var keywordState: String = "",
    var filterState: BookCategory = BookCategory.All,
    var counterState: Int = 0,
    var bookState: UIState = UIState.Loading
)

data class BookPackageUIState(
    var filterState: String = "VII",
    var booksState: UIState = UIState.Loading
)