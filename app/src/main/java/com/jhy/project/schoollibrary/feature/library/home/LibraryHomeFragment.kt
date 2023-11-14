package com.jhy.project.schoollibrary.feature.library.home

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.HorizontalSpace
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.SearchTextField
import com.jhy.project.schoollibrary.component.compose.SelectedButton
import com.jhy.project.schoollibrary.component.compose.UnSelectedButton
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.cariBukuDisini
import com.jhy.project.schoollibrary.component.compose.loadImage
import com.jhy.project.schoollibrary.constanta.Navigation
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.BookCategory
import com.jhy.project.schoollibrary.model.adapter.LibraryMenu
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryHomeFragment : BaseComposeFragment() {

    private val viewModel by viewModels<HomeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        composeView.setContent {
            val alpha = if (viewModel.isAdmin) 1f else 0f
            val libMenus = viewModel.libMenuItems
            val filters = viewModel.filters
            val selectedFilter = viewModel.selectedFilter
            val bookCount = viewModel.bookCount
            val books = viewModel.bookList
            var searchText by remember { mutableStateOf("") }
            MaterialTheme {
                Scaffold(floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier.alpha(alpha), onClick = {
                            navigate(Navigation.addEditBook + "Tambah Buku")
                        }, backgroundColor = AppColor.blueSoft
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        LibMenuSection(libMenus)
                        VerticalSpace(height = 16.dp)
                        SearchSection(
                            text = searchText
                        ) { newText ->
                            searchText = newText
                        }
                        FilterSection(filters = filters, selectedFilter)
                        WorkSandTextMedium(
                            modifier = Modifier.padding(16.dp, 0.dp),
                            text = "Total Buku: ($bookCount)"
                        )
                        BookListSection(books)
                    }
                }

            }
        }
    }

    private fun initObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.onCreate()
    }

    @Composable
    fun LibMenuSection(libMenus: List<LibraryMenu>) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            items(libMenus) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigate(item.uri)
                        }, horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(id = item.icon),
                        contentDescription = null
                    )
                    VerticalSpace(height = 8.dp)
                    WorkSandTextMedium(
                        modifier = Modifier.fillMaxWidth(), text = item.title
                    )
                }
            }
        }
    }

    @Composable
    fun SearchSection(text: String, onChange: (String) -> Unit) {
        SearchTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(16.dp, 0.dp),
            placeholder = cariBukuDisini,
            text = text,
            onTextChange = {
                onChange(it)
            },
            onSearch = viewModel::doSearch
        )
    }

    @Composable
    fun FilterSection(filters: List<BookCategory>, selectedFilter: BookCategory) {
        LazyRow(
            modifier = Modifier.padding(16.dp, 16.dp, 0.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(filters) { item ->
                Row {
                    if (item == selectedFilter) {
                        SelectedButton(text = item.title)
                    } else {
                        UnSelectedButton(text = item.title) {
                            viewModel.updateSelectedFilter(item)
                        }
                    }
                    HorizontalSpace(width = 8.dp)
                }
            }
        }
    }

    @Composable
    fun BookListSection(books: List<Book>) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            items(books) { book ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable {
                        navigate(Navigation.bookDetail + book.key)
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(150.dp)
                                .background(AppColor.blueSoft), contentAlignment = Alignment.Center
                        ) {
                            book.image?.let { url ->
                                loadImage(
                                    context = requireContext(),
                                    url = url,
                                    defaultImage = R.drawable.ic_logo_smp
                                ).value?.let {
                                    ImageComponent(
                                        frame = it, modifier = Modifier
                                            .width(100.dp)
                                            .height(150.dp)
                                    )
                                }
                            } ?: run {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_logo_smp),
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                        }
                        VerticalSpace(height = 8.dp)
                        WorkSandTextNormal(
                            text = book.judul ?: "Unknown",
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
        }
    }
}