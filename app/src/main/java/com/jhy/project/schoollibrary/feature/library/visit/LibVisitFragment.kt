package com.jhy.project.schoollibrary.feature.library.visit

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.HorizontalSpace
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.feature.library.home.CaptureAct
import com.jhy.project.schoollibrary.feature.library.user.UserListener
import com.jhy.project.schoollibrary.feature.library.user.UsersBottomSheet
import com.jhy.project.schoollibrary.model.User
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
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        WorkSandTextMedium(text = "Masuk dengan: ")
                        HorizontalSpace(width = 16.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PrimaryButton(
                                modifier = Modifier.weight(1f),
                                text = "Scan Barcode"
                            ) {
                                launchBar()
                            }
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
                    }
                    WorkSandTextMedium(text = "Daftar Kunjungan Pustaka")
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(visitors) { visitor ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        WorkSandTextMedium(
                                            modifier = Modifier.weight(1f),
                                            text = visitor.name
                                        )
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
    }

    private fun initObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            showInfoDialog(it.second)
        }
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