package com.jhy.project.schoollibrary.feature.activity

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.HorizontalSpace
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.SelectedButton
import com.jhy.project.schoollibrary.component.compose.UnSelectedButton
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandButtonMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.feature.library.user.UserListener
import com.jhy.project.schoollibrary.feature.library.user.UsersBottomSheet
import com.jhy.project.schoollibrary.model.Presence
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.guru
import com.jhy.project.schoollibrary.model.toKelasText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityFragment : BaseComposeFragment() {

    private val viewModel by viewModels<ActivityViewModel>()

    enum class SectionType {
        Kegiatan, Kurikulum, Pertemuan, Asesmen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme {
                when {
                    !viewModel.isLogin -> {
                        NonLoginComponent()
                    }

                    else -> {
                        ActivityPage()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initObservers()
    }

    @Composable
    private fun ActivityPage() {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (viewModel.isAdmin) {
                item {
                    UnSelectedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = viewModel.userName ?: "Pilih Guru"
                    ) {
                        showBottomSheet(UsersBottomSheet(guru, object : UserListener {
                            override fun pickUser(user: User) {
                                viewModel.updateUser(user.kode, user.name)
                            }
                        }))
                    }
                }
            }
            if (!viewModel.selectedUser.isNullOrEmpty()) {
                item {
                    ActivitySection()
                }
                item {
                    KelasSection()
                }
                item {
                    PresenceSection()
                }
            }
        }
    }

    private fun initObservers() {
        if (!viewModel.isLogin) return

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.onCreate()
    }

    @Composable
    fun ActivitySection() {
        val selectedDay = viewModel.selectedFilter[SectionType.Kegiatan] ?: "Senin"
        SectionComponent(backgroundColor = AppColor.blueSoft) {
            WorkSandTextMedium(
                text = "Kegiatan", size = 18.sp
            )
            VerticalSpace(height = 8.dp)
            FilterComponent(
                sectionType = SectionType.Kegiatan,
                items = viewModel.days,
                selectedItem = selectedDay,
                onChange = viewModel::updateSelectedFilter
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp)
            ) {
                viewModel.todayActivities.forEach { item ->
                    val backgroundColor =
                        if (item.guru.isEmpty()) AppColor.neutral40 else AppColor.white
                    val textColor =
                        if (item.guru.isEmpty()) AppColor.white else AppColor.neutral40
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
                            val kelas = item.kelas.uppercase().replace(".", " ")
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
                                text = kelas,
                                align = TextAlign.Start,
                                color = textColor
                            )
                        }
                    }
                }
                if (viewModel.todayActivities.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(AppColor.white), contentAlignment = Alignment.Center
                    ) {
                        WorkSandTextMedium(text = "Belum ada kegiatan di hari $selectedDay")
                    }
                }
            }
        }
    }

    @Composable
    fun KelasSection() {
        val kelasList =
            viewModel.mapelState?.kelas?.map { it.split("_").lastOrNull()?.toKelasText() ?: "" }
                ?: emptyList()
        val selectedKelas = viewModel.selectedFilter[SectionType.Kurikulum]
        SectionComponent(
            backgroundColor = AppColor.greenSoft
        ) {
            WorkSandTextMedium(
                text = "Kurikulum Merdeka", size = 18.sp
            )
            VerticalSpace(height = 8.dp)
            FilterComponent(
                sectionType = SectionType.Kurikulum,
                items = kelasList,
                selectedItem = selectedKelas ?: "",
                onChange = viewModel::updateSelectedFilter
            )
            VerticalSpace(height = 16.dp)
            selectedKelas?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkSandTextMedium(
                        modifier = Modifier.weight(1f),
                        text = "Capaian Pembelajaran (CP)",
                        align = TextAlign.Left
                    )
                    WorkSandButtonMedium(text = "Lihat") {

                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkSandTextMedium(
                        modifier = Modifier.weight(1f),
                        text = "Alur Tujuan Pembelajaran (ATP)",
                        align = TextAlign.Left
                    )
                    WorkSandButtonMedium(text = "Lihat") {

                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkSandTextMedium(
                        modifier = Modifier.weight(1f),
                        text = "Perangkat Ajar",
                        align = TextAlign.Left
                    )
                    WorkSandButtonMedium(text = "Lihat") {

                    }
                }
            }
        }
    }

    @Composable
    fun PresenceSection() {
        val kelasList = viewModel.mapelState?.kelas?.map { it.split("_").lastOrNull()?.toKelasText() ?: "" } ?: emptyList()
        val selectedKelas = viewModel.selectedFilter[SectionType.Pertemuan] ?: ""
        SectionComponent(
            backgroundColor = AppColor.pinkSoft
        ) {
            WorkSandTextMedium(
                text = "Pertemuan", size = 18.sp
            )
            VerticalSpace(height = 8.dp)
            FilterComponent(
                sectionType = SectionType.Pertemuan,
                items = kelasList,
                selectedItem = selectedKelas,
                onChange = viewModel::updateSelectedFilter
            )
            VerticalSpace(height = 16.dp)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.absenceList) { absence ->
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .clickable {
                                if (viewModel.isAdmin) return@clickable
                                viewModel.mapelState?.let {
                                    findNavController().navigate(
                                        ActivityFragmentDirections.actionToPresenceFragment(
                                            selectedKelas, it, absence.key
                                        )
                                    )
                                }
                            },
                        backgroundColor = AppColor.white
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            val hadir =
                                absence.absence.values.filter { it == Presence.Hadir }.size
                            val sakit =
                                absence.absence.values.filter { it == Presence.Sakit }.size
                            val izin =
                                absence.absence.values.filter { it == Presence.Izin }.size
                            val tanpaKeterangan =
                                absence.absence.values.filter { it == Presence.TanpaKeterangan }.size

                            WorkSandTextMedium(text = "Pertemuan ke-${absence.pertemuan}")
                            VerticalSpace(height = 8.dp)
                            WorkSandTextNormal(text = "Hadir: $hadir")
                            WorkSandTextNormal(text = "Sakit: $sakit")
                            WorkSandTextNormal(text = "Izin: $izin")
                            WorkSandTextNormal(text = "Tanpa Keterangan: $tanpaKeterangan")
                            VerticalSpace(height = 8.dp)
                            WorkSandTextNormal(text = "Jurnal: ${absence.jurnal}")
                        }

                    }
                }
            }
            if (!viewModel.isAdmin) {
                VerticalSpace(height = 16.dp)
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(), text = "Tambah Pertemuan"
                ) {
                    viewModel.mapelState?.let {
                        findNavController().navigate(
                            ActivityFragmentDirections.actionToPresenceFragment(
                                selectedKelas, it
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AsesmenSection() {
        val kelasList = viewModel.mapelState?.kelas?.map { it.split("_").lastOrNull()?.toKelasText() ?: "" } ?: emptyList()
        val selectedKelas = viewModel.selectedFilter[SectionType.Asesmen] ?: ""
        SectionComponent(
            backgroundColor = AppColor.orangeSoft
        ) {
            WorkSandTextMedium(
                text = "Asesmen",
                size = 18.sp
            )
            VerticalSpace(height = 8.dp)
            FilterComponent(
                sectionType = SectionType.Asesmen,
                items = kelasList,
                selectedItem = selectedKelas,
                onChange = viewModel::updateSelectedFilter
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.absenceList) { absence ->
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .clickable {

                            },
                        backgroundColor = AppColor.white
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            val hadir =
                                absence.absence.values.filter { it == Presence.Hadir }.size
                            val sakit =
                                absence.absence.values.filter { it == Presence.Sakit }.size
                            val izin =
                                absence.absence.values.filter { it == Presence.Izin }.size
                            val tanpaKeterangan =
                                absence.absence.values.filter { it == Presence.TanpaKeterangan }.size

                            WorkSandTextMedium(text = "Pertemuan ke-${absence.pertemuan}")
                            VerticalSpace(height = 8.dp)
                            WorkSandTextNormal(text = "Hadir: $hadir")
                            WorkSandTextNormal(text = "Sakit: $sakit")
                            WorkSandTextNormal(text = "Izin: $izin")
                            WorkSandTextNormal(text = "Tanpa Keterangan: $tanpaKeterangan")
                            VerticalSpace(height = 8.dp)
                            WorkSandTextNormal(text = "Jurnal: ${absence.jurnal}")
                        }
                    }
                }
            }
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(), text = "Perbaharui"
            ) {

            }
        }
    }

    @Composable
    fun SectionComponent(
        backgroundColor: Color,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            backgroundColor = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = content
            )
        }
    }

    @Composable
    fun FilterComponent(
        sectionType: SectionType,
        items: List<String>,
        selectedItem: String,
        onChange: (SectionType, String) -> Unit
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                if (selectedItem == item) {
                    SelectedButton(text = item)
                } else {
                    UnSelectedButton(text = item) {
                        onChange(sectionType, item)
                    }
                }
            }
        }
    }
}