package com.jhy.project.schoollibrary.feature.school.siswaalumni

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.ChooserBottomSheet
import com.jhy.project.schoollibrary.component.compose.*
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.model.alumni
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.model.wanita
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SAFragment : BaseComposeFragment() {

    private val viewModel by viewModels<SAViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            val page = viewModel.pageState.collectAsState().value
            val tahun = viewModel.alumniTahunState.collectAsState().value
            val kelasList = viewModel.kelasListState.collectAsState().value
            val kelas = viewModel.kelasState.collectAsState().value
            val users = viewModel.userListState.collectAsState().value
            var searchText by remember { mutableStateOf("") }

            Column(modifier = Modifier.fillMaxWidth()) {
                SearchTextField(modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(16.dp, 0.dp),
                    placeholder = cariSiswaOrAlumni,
                    text = searchText,
                    onTextChange = {
                        searchText = it
                        if (it.isEmpty()) viewModel.doSearch("")
                    },
                    onSearch = {
                        viewModel.doSearch(searchText)
                    })
                VerticalSpace(height = 16.dp)
                FilterUserSection(page = page)

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        if (page == alumni) {
                            FilterItemSection("Pilih Tahun", tahun, generateYearList()) {
                                viewModel.updateAlumniTahun(it)
                            }
                        } else {
                            FilterItemSection("Pilih Kelas", kelas, kelasList) {
                                viewModel.updateKelas(it)
                            }
                        }
                    }
                    itemsIndexed(users) { _, item ->
                        val background = if (wanita.equals(
                                item.gender,
                                true
                            )
                        ) AppColor.pinkSoft else AppColor.blueSoft
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 8.dp),
                            backgroundColor = background
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                WorkSandTextMedium(
                                    text = "Nama: ${item.name ?: ""}", color = AppColor.neutral40
                                )
                                VerticalSpace(height = 8.dp)
                                WorkSandTextNormal(
                                    text = item.kelas?.replace(".", " ")?.replace("_", " ")
                                        ?.capitalizeWord() ?: "",
                                    color = AppColor.neutral40,
                                    textAlign = TextAlign.Start
                                )
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

    @Composable
    fun FilterUserSection(page: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(16.dp, 0.dp)
        ) {
            FilterButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = "Alumni",
                selected = page == alumni
            ) {
                viewModel.updatePage(alumni)
            }
            HorizontalSpace(width = 16.dp)
            FilterButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = "Siswa",
                selected = page == siswa
            ) {
                viewModel.updatePage(siswa)
            }
        }
    }

    @Composable
    fun FilterItemSection(
        title: String, value: String, list: List<String>, action: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorkSandTextMedium(
                text = "$title:",
                size = 16.sp,
                color = colorResource(id = R.color.black),
                align = TextAlign.Center
            )
            HorizontalSpace(width = 8.dp)
            UnSelectedButton(
                modifier = Modifier.fillMaxWidth(),
                text = value,
                textColor = R.color.black,
                background = R.color.white
            ) {
                showBottomSheet(ChooserBottomSheet(title, list) {
                    action.invoke(it)
                })
            }
        }
    }

    private fun generateYearList(): List<String> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = ArrayList<String>()

        for (year in currentYear downTo currentYear - 100) {
            years.add(year.toString())
        }

        return years
    }
}