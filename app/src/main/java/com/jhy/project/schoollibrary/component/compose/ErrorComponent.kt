package com.jhy.project.schoollibrary.component.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jhy.project.schoollibrary.model.state.ErrorType

@Composable
fun ErrorComponent(
    modifier: Modifier = Modifier, errorType: ErrorType
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = errorType.icon, contentDescription = "error")
            WorkSandTextMedium(text = errorType.message)
        }
    }
}