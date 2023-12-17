package com.jhy.project.schoollibrary.feature.event.addevent

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.DatePickerBottomSheet
import com.jhy.project.schoollibrary.component.compose.DatePicker
import com.jhy.project.schoollibrary.component.compose.EditTextWithTitle
import com.jhy.project.schoollibrary.component.compose.MultipleLineEditText
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.picker.TimePickerBottomSheet
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.extension.toMillis
import com.jhy.project.schoollibrary.model.constant.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEventFragment : BaseComposeFragment() {

    private val viewModel by viewModels<AddEventViewModel>()
    private val args by navArgs<AddEventFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            MaterialTheme {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            EditTextWithTitle(
                                title = "Nama Acara",
                                placeholder = "Masukkan nama acara",
                                text = viewModel.eventNameState,
                                onTextChange = viewModel::updateEventNameState
                            )
                            EditTextWithTitle(
                                title = "Lokasi Acara",
                                placeholder = "Masukkan lokasi acara",
                                text = viewModel.eventPlaceState,
                                onTextChange = viewModel::updateEventPlaceState
                            )
                            EditTextWithTitle(
                                title = "Agenda",
                                placeholder = "Masukkan agenda acara",
                                text = viewModel.eventAgendaState,
                                onTextChange = viewModel::updateEventAgendaState
                            )
                            WorkSandTextMedium(text = "Peserta acara")
                            MultipleLineEditText(
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                value = viewModel.pesertaState,
                                onValueChange = viewModel::updatePesertaState,
                                hintText = "Masukkan peserta acara"
                            )
                            DatePicker(
                                title = "Tanggal Mulai",
                                date = viewModel.startDateState,
                                onDateClicked = {
                                    showBottomSheet(
                                        DatePickerBottomSheet(
                                            date = viewModel.startDateState.toMillis("dd MMMM yyyy"),
                                            onDatePicked = viewModel::updateStartDateState
                                        )
                                    )
                                },
                                time = viewModel.startTimeState,
                                onTimeClicked = {
                                    showBottomSheet(
                                        TimePickerBottomSheet(
                                            viewModel.startTimeState.split(":")[0],
                                            viewModel.startTimeState.split(":")[1],
                                            onTimePicked = viewModel::updateStartTimeState
                                        )
                                    )
                                })
                            DatePicker(
                                title = "Tanggal Berakhir",
                                date = viewModel.endDateState,
                                onDateClicked = {
                                    showBottomSheet(
                                        DatePickerBottomSheet(
                                            date = viewModel.endDateState.toMillis("dd MMMM yyyy"),
                                            onDatePicked = viewModel::updateEndDateState
                                        )
                                    )
                                },
                                time = viewModel.endTimeState,
                                onTimeClicked = {
                                    showBottomSheet(
                                        TimePickerBottomSheet(
                                            viewModel.endTimeState.split(":")[0],
                                            viewModel.endTimeState.split(":")[1],
                                            onTimePicked = viewModel::updateEndTimeState
                                        )
                                    )
                                })
                            PrimaryButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Submit Acara",
                                onClick = viewModel::submitEvent
                            )
                        }
                    }
                }
            }
        }

        initObservers()

    }

    private fun initObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        lifecycleScope.launch {
            viewModel.resultState.collectLatest {
                when (it) {
                    is Result.Success -> showInfoDialog(it.message) {
                        findNavController().navigateUp()
                    }

                    is Result.Error -> showInfoDialog(it.message)

                    else -> {}
                }
            }
        }

        args.event?.let {
            viewModel.initialEvent(it)
        }
    }
}