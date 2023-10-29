package com.jhy.project.schoollibrary.base

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.component.InfoDialog
import com.jhy.project.schoollibrary.component.LoadingDialog

open class BaseComposeFragment : Fragment() {

    private var _baseHandler: Handler? = null
    lateinit var composeView: ComposeView
    private val loadingDialog by lazy {
        LoadingDialog()
    }
    private val infoDialog by lazy {
        InfoDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _baseHandler = Handler(Looper.getMainLooper())
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _baseHandler?.removeCallbacksAndMessages(null)
        _baseHandler = null
    }

    fun showLoading(show: Boolean = true) {
        if (show && !loadingDialog.isShowing) {
            loadingDialog.show(requireContext())
        } else if (!show && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    fun postDelayed(runnable: Runnable, delay: Long) {
        _baseHandler?.postDelayed(runnable, delay)
    }

    fun showInfoDialog(
        description: String, action: (() -> Unit)? = null
    ) {
        infoDialog.show(requireContext(), "Info", description, action)
    }

    protected open fun getNavOptions(): NavOptions? {
        return NavOptions.Builder().setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_to_left).setPopEnterAnim(R.anim.exit_to_right)
            .setPopExitAnim(R.anim.enter_from_left).build()
    }

    fun navigate(uri: String) {
        try {
            findNavController().navigate(
                Uri.parse(uri),
                getNavOptions()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}