package com.jhy.project.schoollibrary.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.auth.AuthActivity
import com.jhy.project.schoollibrary.component.InfoDialog
import com.jhy.project.schoollibrary.component.LoadingDialog
import com.jhy.project.schoollibrary.component.WebViewActivity
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandButtonMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.model.constant.Constant

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

    fun navigateToLoginPage() {
        val intent = Intent(
            requireActivity(),
            AuthActivity::class.java
        )
        startActivity(intent)
    }

    fun showWeb(url: String) {
        val intent = Intent(requireActivity(), WebViewActivity::class.java)
        intent.putExtra(Constant.URL, url)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    @Composable
    fun NonLoginComponent(modifier: Modifier = Modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WorkSandTextMedium(
                text = "Untuk mengakses halaman ini, Anda harus terdaftar sebagai guru/pegawai SMPN 1 Painan"
            )
            VerticalSpace(height = 16.dp)
            WorkSandButtonMedium(text = "Masuk") {
                navigateToLoginPage()
            }
        }
    }
}