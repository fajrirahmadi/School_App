package com.jhy.project.schoollibrary.feature.school.prestasi

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchoolPrestasiFragment : BaseComposeFragment() {

    private val viewModel by viewModels<SchoolPrestasiViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            val prestasies = viewModel.prestasiState.collectAsState().value
            val years = viewModel.yearState.collectAsState().value
            val selectedIndex = viewModel.selectedFilter.collectAsState().value
            val image = viewModel.imageState.collectAsState().value

            Column(modifier = Modifier.fillMaxWidth()) {
                loadImage(
                    context = requireContext(),
                    url = image,
                    defaultImage = R.drawable.ic_logo_smp
                ).value?.let {
                    ImageComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp), frame = it
                    )
                }
                VerticalSpace(height = 16.dp)
                WorkSandTextSemiBold(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    text = "Prestasi SMPN 1 Painan",
                    color = colorResource(
                        id = R.color.black
                    ),
                    size = 16.sp,
                    align = TextAlign.Center
                )
                LazyRow(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(years) { index, item ->
                        Row {
                            if (index == selectedIndex) {
                                SelectedButton(text = item)
                            } else {
                                UnSelectedButton(text = item) {
                                    viewModel.setSelectedFilter(index)
                                }
                            }
                            HorizontalSpace(width = 8.dp)
                        }
                    }
                }
                VerticalSpace(height = 16.dp)
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(prestasies) { _, item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp),
                            backgroundColor = colorResource(id = R.color.neutral_40)
                        ) {
                            WorkSandTextMedium(
                                modifier = Modifier.padding(8.dp),
                                text = item,
                                align = TextAlign.Left
                            )
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