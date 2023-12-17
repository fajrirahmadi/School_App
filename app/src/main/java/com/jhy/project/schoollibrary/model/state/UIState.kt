package com.jhy.project.schoollibrary.model.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class UIState {
    object Loading : UIState()
    data class Success(var data: Any): UIState()
    data class Error(var errorType: ErrorType): UIState()
}

enum class ErrorType(val icon: ImageVector, val message: String) {
    NetworkError(
        Icons.Outlined.Settings,
        "Pastikan Anda terhubung dengan koneksi Internet"
    ),
    EmptyData(
        Icons.Outlined.Info,
        "Data yang Anda cari tidak ditemukan"
    ),
    SystemError(
        Icons.Outlined.Build,
        "Terjadi kesalahan pada sistem, kami segera memperbaikinya"
    )
}