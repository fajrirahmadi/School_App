package com.jhy.project.schoollibrary.component.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    title: String,
    date: String,
    time: String = "",
    showTime: Boolean = true,
    onDateClicked: () -> Unit,
    onTimeClicked: () -> Unit = {}
) {
    val dateText = date.ifEmpty { "Pilih tanggal" }
    val timeText = time.ifEmpty { "Pilih jam" }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WorkSandTextMedium(text = title)
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .border(
                        BorderStroke(1.dp, AppColor.neutral40),
                        RoundedCornerShape(10)
                    )
                    .background(AppColor.white)
                    .clickable {
                        onDateClicked()
                    },
                contentAlignment = Alignment.Center
            ) {
                WorkSandTextNormal(
                    text = dateText
                )
            }
            if (showTime) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(48.dp)
                        .border(
                            BorderStroke(1.dp, AppColor.neutral40),
                            RoundedCornerShape(10)
                        )
                        .background(AppColor.white)
                        .clickable {
                            onTimeClicked()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    WorkSandTextNormal(
                        text = timeText
                    )
                }
            }
        }
    }
}