package com.jhy.project.schoollibrary.feature.school.extra

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExtraFragment : BaseComposeFragment() {

    private val viewModel by viewModels<ExtraViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            val items = viewModel.extraModel?.items ?: emptyList()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items.sortedBy { it.name }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        backgroundColor = AppColor.greenSoft
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            WorkSandTextMedium(
                                text = item.name, color = AppColor.neutral40
                            )
                            VerticalSpace(height = 8.dp)
                            if (item.target.isNotEmpty()) {
                                WorkSandTextNormal(
                                    text = "Target: ${item.target}", color = AppColor.neutral40
                                )
                                VerticalSpace(height = 8.dp)
                            }
                            if (item.tujuan.isNotEmpty()) {
                                WorkSandTextNormal(
                                    text = "Tujuan: ${item.tujuan}", color = AppColor.neutral40
                                )
                                VerticalSpace(height = 8.dp)
                            }
                            if (item.lingkup.isNotEmpty()) {
                                WorkSandTextNormal(
                                    text = "Lingkup:\n${
                                        item.lingkup.replace(
                                            "*", "\n"
                                        )
                                    }", color = AppColor.neutral40
                                )
                                VerticalSpace(height = 8.dp)
                            }
                            if (item.penanggungJawab.isNotEmpty()) {
                                WorkSandTextNormal(
                                    text = "Penanggung Jawab:\n${
                                        item.penanggungJawab.replace(
                                            "*", "\n"
                                        )
                                    }", color = AppColor.neutral40
                                )
                                VerticalSpace(height = 8.dp)
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