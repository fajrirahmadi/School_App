package com.jhy.project.schoollibrary.base

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jhy.project.schoollibrary.component.LoadingDialog

abstract class BaseViewBindingBottomSheet<B : ViewBinding> : BaseBottomSheet() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> B

    private val baseHandler = Handler(Looper.getMainLooper())
    private lateinit var sheetDialog: BottomSheetDialog
    private val loadingDialog by lazy {
        LoadingDialog()
    }

    @Suppress("UNCHECKED_CAST")
    protected val binding: B
        get() = _binding as B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        sheetDialog.setOnShowListener {
            val d: BottomSheetDialog = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = getSheetState()
                if (isAlwaysFull()) {
                    val params = bottomSheet.layoutParams
                    params.height = WindowManager.LayoutParams.MATCH_PARENT
                    bottomSheet.layoutParams = params
                }
                behavior.isDraggable = isDraggable()
            }

        }
        return sheetDialog
    }

    open fun isDraggable(): Boolean {
        return true
    }

    open fun isAlwaysFull(): Boolean {
        return false
    }

    open fun getSheetState(): Int {
        return BottomSheetBehavior.STATE_EXPANDED
    }

    private fun disableClick() {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    protected fun blockButton() {
        disableClick()
        baseHandler.postDelayed(
            {
                enableClick()
            }, 1500
        )
    }

    private fun enableClick() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    open fun getSheetDialog(): BottomSheetDialog {
        return sheetDialog
    }

    fun showLoading() {
        loadingDialog.show(requireContext())
    }

    fun dismissLoading() {
        loadingDialog.dismiss()
    }
}