package com.jhy.project.schoollibrary.feature.school.galeri

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.ZoomImageBottomSheet
import com.jhy.project.schoollibrary.component.compose.*
import com.jhy.project.schoollibrary.extension.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : BaseComposeFragment() {

    private val viewModel by viewModels<GalleryViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            val gallery = viewModel.galleryState
            val alpha = if (viewModel.isAdmin) 1f else 0f

            Scaffold(floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.alpha(alpha),
                    onClick = {
                        addGallery()
                    },
                    backgroundColor = AppColor.blueSoft
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    items(gallery) { data ->
                        val pagerState = rememberPagerState(pageCount = { data.items.size })
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        ) {
                            WorkSandTextMedium(text = data.name)
                            WorkSandTextNormal(text = data.year)
                            VerticalSpace(height = 8.dp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.TopStart
                            ) {
                                HorizontalPager(
                                    state = pagerState
                                ) { index ->
                                    loadImage(
                                        context = requireContext(),
                                        url = data.items[index],
                                        defaultImage = R.drawable.ic_logo_smp
                                    ).value?.let { image ->
                                        ImageComponent(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                                .clickable {
                                                    showBottomSheet(
                                                        ZoomImageBottomSheet(data.items[index])
                                                    )
                                                },
                                            frame = image,
                                            scale = ContentScale.Crop
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier.padding(16.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    backgroundColor = AppColor.greenSoft
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .height(24.dp)
                                            .padding(8.dp, 0.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        WorkSandTextNormal(text = "${pagerState.currentPage + 1} / ${data.items.size}")
                                    }
                                }
                            }
                            VerticalSpace(height = 16.dp)
                            Divider(
                                color = AppColor.neutral40
                            )
                        }
                    }
                }
            }
        }

        initObserver()
    }

    private fun initObserver() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.onCreate()
    }

    private fun addGallery() {
        findNavController().navigate(
            GalleryFragmentDirections.actionToAddGalleryFragment(), getNavOptions()
        )
    }
}