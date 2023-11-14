package com.jhy.project.schoollibrary.feature.library.visit

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.ConfirmationBottomSheet
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.HorizontalSpace
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.feature.library.home.CaptureAct
import com.jhy.project.schoollibrary.feature.library.user.UserListener
import com.jhy.project.schoollibrary.feature.library.user.UsersBottomSheet
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.pria
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibVisitFragment : BaseComposeFragment() {

    private val viewModel by viewModels<LibVisitViewModel>()

    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let {
            viewModel.searchUserByNis(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        initObservers()
    }

    private fun setupViews() {
        composeView.setContent {
            MaterialTheme {
                val visitors = viewModel.visitors
                val dailyVisitorCount = viewModel.dailyVisitors
                val isAdmin = viewModel.isAdmin
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PrimaryButton(
                            modifier = Modifier.weight(1f),
                            text = "Scan Barcode"
                        ) {
                            launchBar()
                        }
                        HorizontalSpace(width = 8.dp)
                        PrimaryButton(
                            modifier = Modifier.weight(1f),
                            text = "Cari Daftar"
                        ) {
                            showBottomSheet(UsersBottomSheet(object : UserListener {
                                override fun pickUser(user: User) {
                                    viewModel.submitVisitor(user)
                                }
                            }))
                        }
                    }
                    VerticalSpace(height = 24.dp)
                    WorkSandTextMedium(text = "Daftar Kunjungan Hari Ini (${dailyVisitorCount})")
                    VerticalSpace(height = 8.dp)
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(visitors) { visitor ->
                            val bgColor =
                                if (visitor.gender == pria) AppColor.blueSoft else AppColor.pinkSoft
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 8.dp)
                                    .clickable {
                                        if (isAdmin) {
                                            showBottomSheet(
                                                ConfirmationBottomSheet(
                                                    "Hapus Kunjungan",
                                                    "Apakah Anda yakin ingin menghapus kunjungan ${visitor.name} ini?",
                                                    {
                                                        viewModel.removeVisitRecord(visitor)
                                                    }
                                                )
                                            )
                                        }
                                    },
                                backgroundColor = bgColor
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        WorkSandTextMedium(
                                            text = visitor.name,
                                            align = TextAlign.Left
                                        )
                                        visitor.kelas?.let {
                                            VerticalSpace(height = 8.dp)
                                            WorkSandTextNormal(
                                                text = "Kelas: ${
                                                    it.replace(
                                                        "_",
                                                        " "
                                                    )
                                                }"
                                            )
                                        }
                                    }
                                    HorizontalSpace(width = 16.dp)
                                    WorkSandTextNormal(text = visitor.date.toDateFormat("HH:mm"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            showInfoDialog(it.second)
        }

        viewModel.onCreate()
    }

    private fun launchBar() {
        val options = ScanOptions()
        options.apply {
            setPrompt("Volume up to flash on")
            setBeepEnabled(true)
            setOrientationLocked(true)
            captureActivity = CaptureAct::class.java
        }
        barLauncher.launch(options)
    }
}