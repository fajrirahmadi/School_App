package com.jhy.project.schoollibrary.base

import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet : BottomSheetDialogFragment() {
    private var isShown: Boolean = false

    fun showAllowingStateLoss(manager: FragmentManager, tag: String) {
        if (!isShown) {
            isShown = true
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        }
    }

    override fun dismissAllowingStateLoss() {
        if (isShown) {
            isShown = false
            super.dismissAllowingStateLoss()
        }
    }
}