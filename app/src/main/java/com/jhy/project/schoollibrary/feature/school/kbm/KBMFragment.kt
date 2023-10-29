package com.jhy.project.schoollibrary.feature.school.kbm

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
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
import com.jhy.project.schoollibrary.component.ChooserBottomSheet
import com.jhy.project.schoollibrary.component.compose.*
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.Mapel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KBMFragment : BaseComposeFragment() {

    private val viewModel by viewModels<KBMViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            val kelasList = viewModel.kelasFilterState.collectAsState().value
            val kelasSelected = viewModel.kelasSelectedState.collectAsState().value
            val kelas = viewModel.kelasState.collectAsState().value
            val mapel = viewModel.mapelListState.collectAsState().value
            val daySelected = viewModel.daySelectedState.collectAsState().value
            val jadwal = viewModel.jadwalListState.collectAsState().value

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterKelas(kelasList, kelasSelected)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp)
                ) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            KelasInfoSection(kelas)
                            Divider(
                                modifier = Modifier.padding(16.dp, 0.dp),
                                color = AppColor.neutral40
                            )
                            MapelSection(mapel)
                            Divider(
                                modifier = Modifier.padding(16.dp, 0.dp),
                                color = AppColor.neutral40
                            )
                            DayFilterSection(daySelected)
                        }
                    }
                    itemsIndexed(jadwal.sortedBy { it.jam }) { index, item ->
                        JadwalItem(index = index, item = item)
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
    fun FilterKelas(kelas: List<String>, kelasSelected: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorkSandTextMedium(
                text = "Pilih kelas:",
                size = 16.sp,
                color = colorResource(id = R.color.black),
                align = TextAlign.Center
            )
            HorizontalSpace(width = 8.dp)
            UnSelectedButton(
                modifier = Modifier.fillMaxWidth(),
                text = kelasSelected,
                textColor = R.color.black,
                background = R.color.white
            ) {
                showBottomSheet(ChooserBottomSheet("Pilih kelas", kelas) {
                    viewModel.updateKelasSelected(it)
                })
            }
        }
    }

    @Composable
    fun KelasInfoSection(kelas: Kelas?) {
        kelas?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = AppColor.blueSoft
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    WorkSandTextMedium(
                        text = "Informasi Kelas",
                        color = AppColor.neutral40,
                        size = 16.sp
                    )
                    VerticalSpace(height = 16.dp)
                    WorkSandTextNormal(
                        text = "Jumlah Siswa: ${it.jumlahSiswa}",
                        color = AppColor.neutral40
                    )
                    VerticalSpace(height = 8.dp)
                    WorkSandTextNormal(
                        text = "Siswa Laki-Laki: ${it.male}",
                        color = AppColor.neutral40
                    )
                    VerticalSpace(height = 8.dp)
                    WorkSandTextNormal(
                        text = "Siswa Perempuan: ${it.female}",
                        color = AppColor.neutral40
                    )
                    VerticalSpace(height = 16.dp)
                    WorkSandTextMedium(text = "Wali Kelas", color = AppColor.neutral40)
                    VerticalSpace(height = 16.dp)
                    WorkSandTextNormal(
                        text = "${it.walas.trim()}\nNIP: ${it.nip}",
                        color = AppColor.neutral40,
                        textAlign = TextAlign.Start
                    )
                }
            }
        } ?: run {
            VerticalSpace(height = 1.dp)
        }
    }

    @Composable
    fun MapelSection(mapel: List<Mapel>) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = AppColor.neutral40
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                WorkSandTextMedium(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp),
                    text = "Informasi Mata Pelajaran",
                    size = 16.sp,
                    color = AppColor.white,
                    align = TextAlign.Start
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    items(mapel) {
                        Card(
                            modifier = Modifier.padding(8.dp),
                            backgroundColor = colorResource(id = R.color.white)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                WorkSandTextMedium(text = it.name, color = AppColor.black)
                                VerticalSpace(height = 8.dp)
                                WorkSandTextNormal(
                                    text = it.guru,
                                    textAlign = TextAlign.Left,
                                    color = AppColor.black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DayFilterSection(daySelected: Int) {
        Column(modifier = Modifier.fillMaxWidth()) {
            WorkSandTextMedium(
                modifier = Modifier.padding(16.dp),
                text = "Informasi Jadwal Pelajaran",
                size = 16.sp,
                color = AppColor.black
            )
            LazyRow(
                modifier = Modifier
                    .height(56.dp)
                    .padding(16.dp, 0.dp, 0.dp, 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(viewModel.days) { index, item ->
                    Row {
                        if (index == daySelected) {
                            SelectedButton(text = item)
                        } else {
                            UnSelectedButton(text = item) {
                                viewModel.updateDaySelected(index)
                            }
                        }
                        HorizontalSpace(width = 16.dp)
                    }
                }
            }
        }
    }

    @Composable
    fun JadwalItem(index: Int, item: Jadwal) {
        val backgroundColor = if (item.guru.isEmpty()) AppColor.neutral40 else AppColor.white
        val textColor = if (item.guru.isEmpty()) AppColor.white else AppColor.neutral40
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            ) {
                val mapel =
                    if (item.guru.isEmpty()) item.mapel.uppercase()
                    else "${item.mapel} - ${item.guru}"
                WorkSandTextMedium(
                    modifier = Modifier.width(100.dp),
                    text = item.actualJam,
                    color = textColor
                )
                HorizontalSpace(width = 16.dp)
                WorkSandTextMedium(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 16.dp, 0.dp),
                    text = mapel,
                    align = TextAlign.Start,
                    color = textColor
                )
            }
        }
    }
}