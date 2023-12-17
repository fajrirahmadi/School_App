package com.jhy.project.schoollibrary.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.auth.AuthActivity
import com.jhy.project.schoollibrary.component.InfoDialog
import com.jhy.project.schoollibrary.component.LoadingDialog
import com.jhy.project.schoollibrary.component.WebViewActivity
import com.jhy.project.schoollibrary.model.constant.Constant


abstract class BaseViewBindingFragment<B : ViewBinding> : Fragment() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> B

    private var _baseHandler: Handler? = null
    private val loadingDialog by lazy {
        LoadingDialog()
    }
    private val infoDialog by lazy {
        InfoDialog()
    }

    @Suppress("UNCHECKED_CAST")
    protected val binding: B
        get() = _binding as B

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _baseHandler = Handler(Looper.getMainLooper())
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onResume() {
        super.onResume()
        enableClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showLoading(false)
        _baseHandler?.removeCallbacksAndMessages(null)
        _binding = null
    }

    fun <T : Any?, L : LiveData<T>> Fragment.observe(
        liveData: L, body: (T?) -> Unit
    ) {
        liveData.observe(viewLifecycleOwner, Observer(body))
    }

    inline fun <T : Any, L : LiveData<T>> Fragment.observeNonNull(
        liveData: L, crossinline body: (T) -> Unit
    ) {
        liveData.observe(viewLifecycleOwner, { it?.let(body) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    protected fun blockButton() {
        disableClick()
        _baseHandler?.postDelayed(
            {
                enableClick()
            }, 500
        )
    }

    private fun disableClick() {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun enableClick() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    protected open fun getNavOptions(): NavOptions? {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_to_left)
            .setPopEnterAnim(R.anim.exit_to_right)
            .setPopExitAnim(R.anim.enter_from_left).build()
    }

    fun postDelayed(runnable: Runnable, delay: Long) {
        _baseHandler?.postDelayed(runnable, delay)
    }

    fun showBottomSheet(
        bottomSheet: BaseBottomSheet, tag: String, dismissAction: (() -> Unit)? = null
    ) {
        blockButton()
        bottomSheet.showAllowingStateLoss(
            childFragmentManager, tag
        )
        postDelayed({
            bottomSheet.dialog?.setOnDismissListener {
                dismissAction?.invoke()
                bottomSheet.dismissAllowingStateLoss()
            }
        }, 1000)
    }

    @Suppress("DEPRECATION")
    fun finishActivity() {
        requireActivity().finish()
        requireActivity().overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left)
    }

    fun showLoading(show: Boolean = true) {
        if (show && !loadingDialog.isShowing) {
            loadingDialog.show(requireContext())
        } else if (!show && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    fun showInfoDialog(context: Context = requireContext(), description: String, action: (() -> Unit)? = null) {
        infoDialog.show(context, "Info", description, action)
    }

    fun dismissInfoDialog() {
        infoDialog.dismiss()
    }

    fun openBrowser(url: String) {
        val packageName = "com.android.chrome";
        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
        params.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.primary_color))
        builder.setDefaultColorSchemeParams(params.build())
        builder.setShowTitle(false)
        builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
        builder.setInstantAppsEnabled(true)
        val customBuilder = builder.build()
        if (requireContext().isPackageInstalled(packageName)) {
            customBuilder.intent.setPackage(packageName)
            customBuilder.launchUrl(requireContext(), Uri.parse(url))
        } else {
            try {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: Exception) {
                showInfoDialog(description = "Failed to open the url")
            }
        }
    }

    private fun Context.isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun showWeb(url: String) {
        val intent = Intent(requireActivity(), WebViewActivity::class.java)
        intent.putExtra(Constant.URL, url)
        requireActivity().startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    fun navigateToSimpleWeb(image: String? = null, title: String? = null, content: String? = null) {
        try {
            findNavController().navigate(Uri.parse("sekolahdigital://simple-web?image=$image&title=$title&content=$content"))
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

    fun fadeInView() {
    }
}