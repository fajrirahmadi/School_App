package com.jhy.project.schoollibrary.feature.activity.presence

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.EditText
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.SelectedButton
import com.jhy.project.schoollibrary.component.compose.UnSelectedButton
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.extension.isNumeric
import com.jhy.project.schoollibrary.model.Presence
import com.jhy.project.schoollibrary.model.constant.Result
import com.jhy.project.schoollibrary.model.toKelasText
import com.jhy.project.schoollibrary.model.wanita
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@AndroidEntryPoint
class PresenceFragment : BaseComposeFragment() {

    private val viewModel by viewModels<PresenceViewModel>()
    private val args by navArgs<PresenceFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme {
                PresencePage()
            }
        }
        initObservers()
    }

    private fun initObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        lifecycleScope.launch {
            viewModel.validationState.collectLatest {
                when (it) {
                    is Result.Success -> showInfoDialog(it.message)
                    is Result.Error -> showInfoDialog(it.message)
                    else -> {}
                }
            }
        }

        viewModel.onCreate(args.key, args.mapel, args.kelas)
    }

    @Composable
    fun PresencePage() {
        val kelas = viewModel.kelas.split("_").lastOrNull() ?: ""
        val mapel = viewModel.mapelState
        val siswa = viewModel.siswaState
        val controller = LocalSoftwareKeyboardController.current
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
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
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkSandTextNormal(
                            text = "Kelas",
                            textAlign = TextAlign.Left,
                            modifier = Modifier.width(120.dp)
                        )
                        WorkSandTextMedium(text = ": ${kelas.toKelasText()}")
                    }
                    VerticalSpace(height = 8.dp)
                    mapel?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            WorkSandTextNormal(
                                text = "Mapel",
                                textAlign = TextAlign.Left,
                                modifier = Modifier.width(120.dp)
                            )
                            WorkSandTextMedium(text = ": ${it.name}")
                        }
                        VerticalSpace(height = 8.dp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkSandTextNormal(
                            text = "Pertemuan ke-",
                            textAlign = TextAlign.Left,
                            modifier = Modifier.width(120.dp)
                        )
                        EditText(placeholder = "Input Pertemuan",
                            keyboardType = KeyboardType.Number,
                            text = viewModel.pertemuanText,
                            onTextChange = {
                                if (it.length <= 3 && it.isNumeric()) viewModel.pertemuanText = it
                            }) {
                            controller?.hide()
                        }
                    }
                    VerticalSpace(height = 8.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkSandTextNormal(
                            text = "Jurnal Kelas",
                            textAlign = TextAlign.Left,
                            modifier = Modifier.width(120.dp)
                        )
                        EditText(placeholder = "Input Jurnal Harian",
                            keyboardType = KeyboardType.Text,
                            text = viewModel.jurnalText,
                            onTextChange = {
                                viewModel.jurnalText = it
                            }) {
                            controller?.hide()
                        }
                    }
                }
            }
            VerticalSpace(height = 16.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(siswa) { index, item ->
                    val background = if (wanita.equals(
                            item.gender, true
                        )
                    ) AppColor.pinkSoft else AppColor.blueSoft
                    val presenceState = viewModel.presenceState[item.no_id]
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
                                text = "${index + 1}. ${item.name ?: ""}",
                                color = AppColor.neutral40
                            )
                            VerticalSpace(height = 8.dp)
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(Presence.values()) {
                                    if (presenceState == it) {
                                        SelectedButton(text = it.toString())
                                    } else {
                                        UnSelectedButton(text = it.toString()) {
                                            viewModel.updatePresence(item.no_id, it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(AppColor.white),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    text = "Update Data"
                ) {
                    viewModel.updateData()
                }
            }
        }
    }
}