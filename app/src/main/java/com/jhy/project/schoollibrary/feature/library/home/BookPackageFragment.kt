package com.jhy.project.schoollibrary.feature.library.home

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.features.BookShimmer
import com.jhy.project.schoollibrary.component.compose.ErrorComponent
import com.jhy.project.schoollibrary.component.compose.FilterButton
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.features.BookSection
import com.jhy.project.schoollibrary.constanta.Navigation
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.state.UIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookPackageFragment : BaseComposeFragment() {

    private val viewModel by viewModels<BookPackageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {

            val filter = viewModel.bookPackageState.filterState

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("VII", "VIII", "IX").forEach {
                        FilterButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(48.dp),
                            text = it,
                            selected = it == filter
                        ) {
                            viewModel.updateFilter(it)
                        }
                    }
                }

                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    text = "Pinjam Buku Paket"
                ) {
                    navigate(Navigation.pinjamBuku + "?paket=true")
                }

                BookListSection(viewModel.bookPackageState.booksState)
            }
        }

        viewModel.loadBookPackage()
    }

    @Composable
    fun BookListSection(bookState: UIState) {
        when (bookState) {
            is UIState.Error -> {
                ErrorComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    errorType = bookState.errorType
                )
            }

            UIState.Loading -> {
                BookShimmer()
            }

            is UIState.Success -> {
                BookSection(
                    books = bookState.data.asList()
                ) {
                    navigate(it)
                }
            }
        }
    }
}