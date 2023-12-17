package com.jhy.project.schoollibrary.component.compose.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.loadImage
import com.jhy.project.schoollibrary.component.compose.shimmerEffect
import com.jhy.project.schoollibrary.constanta.Navigation
import com.jhy.project.schoollibrary.model.Book

@Composable
fun BookShimmer(modifier: Modifier = Modifier) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        items(4) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
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
                            .background(AppColor.blueSoft)
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
    }
}

@Composable
fun BookSection(
    modifier: Modifier = Modifier, books: List<Book> = emptyList(), navigate: (String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier
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
                    navigate(Navigation.bookDetail + book.key + "&paket=true")
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
                                context = LocalContext.current,
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