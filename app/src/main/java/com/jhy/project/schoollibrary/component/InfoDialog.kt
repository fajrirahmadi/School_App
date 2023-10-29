package com.jhy.project.schoollibrary.component

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.DialogInfoBinding

class InfoDialog {

    private lateinit var dialog: Dialog

    fun show(
        context: Context,
        title: String = "Info",
        description: String = "",
        action: (() -> Unit)? = null
    ): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = DialogInfoBinding.inflate(inflater)
        binding.titleTv.text = title
        binding.descriptionTv.text = description
        binding.actionButton.setOnClickListener {
            action?.let {
                it.invoke()
                dialog.dismiss()
            } ?: dialog.dismiss()
        }
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
        return dialog
    }

    fun dismiss() {
        dialog.dismiss()
    }

}