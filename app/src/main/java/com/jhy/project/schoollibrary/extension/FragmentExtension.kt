package com.jhy.project.schoollibrary.extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.base.BaseBottomSheet
import com.jhy.project.schoollibrary.feature.library.home.CaptureAct
import com.journeyapps.barcodescanner.ScanOptions

fun Fragment.popBack() {
    val navController = this.findNavController()
    navController.popBackStack()
}

fun Fragment.popBackStackWithBundle(key: String, value: Any) {
    val navController = this.findNavController()
    navController.previousBackStackEntry?.savedStateHandle?.set(key, value)
    navController.popBackStack()
}

fun Fragment.showToast(text: String) {
    Toast.makeText(this.requireContext(), text, Toast.LENGTH_LONG).show()
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Fragment.showBottomSheet(
    bottomSheet: BaseBottomSheet,
    tag: String = "bottom_sheet",
    dismissAction: (() -> Unit)? = null
) {
    val handler = Handler(Looper.getMainLooper())
    bottomSheet.showAllowingStateLoss(
        childFragmentManager,
        tag
    )
    handler.postDelayed({
        bottomSheet.dialog?.setOnDismissListener {
            dismissAction?.invoke()
            bottomSheet.dismissAllowingStateLoss()
        }
    }, 2_00L)
}