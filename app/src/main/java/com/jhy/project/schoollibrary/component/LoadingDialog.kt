package com.jhy.project.schoollibrary.component

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.DialogLoadingBinding


class LoadingDialog {

    private lateinit var dialog: Dialog
    var isShowing = false

    fun show(context: Context): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DialogLoadingBinding.inflate(inflater)
        binding.progressBar.isVisible = true
        dialog = Dialog(context, R.style.FullDialog)
        dialog.apply {
            setContentView(binding.root)
            setCancelable(false)
            setOnCancelListener(null)
            show()
            window?.setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT
            )
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor =
                ResourcesCompat.getColor(context.resources, R.color.primary_color, null)
        }
        isShowing = true
        return dialog
    }

    fun dismiss() {
        isShowing = false
        dialog.dismiss()
    }
}