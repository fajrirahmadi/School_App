package com.jhy.project.schoollibrary.feature.event

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.calendar.CalendarView
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.extension.toDateFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventFragment : BaseComposeFragment() {

    private val viewModel by viewModels<EventViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            MaterialTheme {
                val events = viewModel.eventListState
                val alpha = if (viewModel.isAdmin) 1f else 0f
                val isLoading = viewModel.isLoading
                Scaffold(floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier.alpha(alpha),
                        onClick = {
                            findNavController().navigate(
                                EventFragmentDirections.actionToAddEventFragment(),
                                getNavOptions()
                            )
                        },
                        backgroundColor = AppColor.blueSoft
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        CalendarView(
                            modifier = Modifier.fillMaxWidth(),
                            state = viewModel.calendarState,
                            onDateSelected = viewModel::onDateSelected,
                            onPrevious = viewModel::onPreviousMonthClicked,
                            onNext = viewModel::onNextMonthClicked
                        )
                        VerticalSpace(height = 16.dp)
                        WorkSandTextMedium(
                            modifier = Modifier.padding(16.dp, 0.dp),
                            text = "Total Agenda (${events.size})"
                        )
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = AppColor.neutral40
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {

                                }
                                items(events) { event ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (viewModel.isAdmin) {
                                                    findNavController().navigate(
                                                        EventFragmentDirections.actionToAddEventFragment(event),
                                                        getNavOptions()
                                                    )
                                                }
                                            },
                                        backgroundColor = AppColor.greenSoft
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            WorkSandTextMedium(
                                                text = event.name,
                                                size = 16.sp
                                            )
                                            WorkSandTextNormal(
                                                text = "Waktu: ${
                                                    event.startDate.toDateFormat("HH:mm")
                                                } - selesai"
                                            )
                                            WorkSandTextNormal(text = "Lokasi: ${event.place.capitalizeWord()}")
                                            WorkSandTextNormal(text = "Peserta: ${event.user}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onCreate()
    }

}