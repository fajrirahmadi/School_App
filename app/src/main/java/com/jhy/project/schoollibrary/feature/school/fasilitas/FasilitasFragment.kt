package com.jhy.project.schoollibrary.feature.school.fasilitas

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.ZoomImageBottomSheet
import com.jhy.project.schoollibrary.component.compose.*
import com.jhy.project.schoollibrary.extension.showBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FasilitasFragment : BaseComposeFragment() {

    private val viewModel by viewModels<FasilitasViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            Column(modifier = Modifier.fillMaxWidth()) {
                val image = viewModel.facilityState?.image
                val facilities = viewModel.facilityState?.items

                image?.let { url ->
                    loadImage(
                        context = requireContext(), url = url, defaultImage = R.drawable.ic_logo_smp
                    ).value?.let {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showBottomSheet(
                                    ZoomImageBottomSheet(url)
                                )
                            }) {
                            ImageComponent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                frame = it,
                                scale = ContentScale.FillBounds
                            )
                        }
                    }
                }
                VerticalSpace(height = 16.dp)
                facilities?.let { items ->
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                VerticalSpace(height = 16.dp)
                                WorkSandTextMedium(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Fasilitas di UPT SMPN 1 Painan",
                                    color = AppColor.neutral40,
                                    size = 16.sp
                                )
                            }
                        }
                        itemsIndexed(items) { _, item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    WorkSandTextMedium(text = item.name, color = AppColor.neutral40)
                                    VerticalSpace(height = 8.dp)
                                    WorkSandTextNormal(
                                        text = "Jumlah: ${item.count}", color = AppColor.neutral40
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.onCreate()
    }
}