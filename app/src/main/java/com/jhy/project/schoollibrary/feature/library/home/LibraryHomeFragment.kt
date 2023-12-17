package com.jhy.project.schoollibrary.feature.library.home

import android.os.Bundle
import android.view.View
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.ErrorComponent
import com.jhy.project.schoollibrary.component.compose.HorizontalSpace
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.SearchTextField
import com.jhy.project.schoollibrary.component.compose.SelectedButton
import com.jhy.project.schoollibrary.component.compose.UnSelectedButton
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.cariBukuDisini
import com.jhy.project.schoollibrary.component.compose.features.BookSection
import com.jhy.project.schoollibrary.component.compose.features.BookShimmer
import com.jhy.project.schoollibrary.component.compose.shimmerEffect
import com.jhy.project.schoollibrary.constanta.Navigation
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.BookCategory
import com.jhy.project.schoollibrary.model.adapter.LibraryMenu
import com.jhy.project.schoollibrary.model.state.UIState
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryHomeFragment : BaseComposeFragment() {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            val alpha = if (viewModel.isAdmin) 1f else 0f
            val libMenus = viewModel.homeLibState.menuState
            val filters = viewModel.filters
            val selectedFilter = viewModel.homeLibState.filterState
            val bookCount = viewModel.homeLibState.counterState
            val bookState = viewModel.homeLibState.bookState
            var searchText by remember { mutableStateOf(viewModel.homeLibState.keywordState) }
            val barLauncher = rememberLauncherForActivityResult(
                contract = ScanContract()
            ) { result ->
                result.contents?.let {
                    searchText = it
                    viewModel.doSearch(searchText)
                }
            }

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
                            launcher = barLauncher,
                            text = searchText
                        ) { newText ->
                            searchText = newText
                            if (newText.isEmpty()) viewModel.doSearch()
                        }
                        FilterSection(filters = filters, selectedFilter)
                        PrimaryButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 0.dp),
                            text = "Buku Paket"
                        ) {
                            navigate(Navigation.bookPackagePage)
                        }
                        WorkSandTextMedium(
                            modifier = Modifier.padding(16.dp, 0.dp),
                            text = "Total Buku: ($bookCount)"
                        )
                        BookListSection(bookState)
                    }
                }

            }
        }

        viewModel.onCreate()

    }

    @Composable
    fun LibMenuSection(state: UIState) {
        if (state is UIState.Error) {
            return
        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            when (state) {
                is UIState.Loading -> {
                    items(4) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shimmerEffect()
                            )
                            VerticalSpace(height = 8.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(20.dp)
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                else -> {
                    val items: List<LibraryMenu> =
                        if (state is UIState.Success) state.data.asList()
                        else emptyList()
                    items(items) { item ->
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
        }
    }

    @Composable
    fun SearchSection(
        launcher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>,
        text: String,
        onChange: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchTextField(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = cariBukuDisini,
                text = text,
                onTextChange = {
                    onChange(it)
                },
                onSearch = viewModel::doSearch
            )
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clickable {
                        val options = ScanOptions()
                        options.apply {
                            setPrompt("Volume up to flash on")
                            setBeepEnabled(true)
                            setOrientationLocked(true)
                            captureActivity = CaptureAct::class.java
                        }
                        launcher.launch(options)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(id = R.drawable.ic_scan),
                    contentDescription = "scan"
                )
            }
        }
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