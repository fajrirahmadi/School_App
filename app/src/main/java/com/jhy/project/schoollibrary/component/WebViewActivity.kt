package com.jhy.project.schoollibrary.component

import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingActivity
import com.jhy.project.schoollibrary.databinding.ActivityWebViewBinding
import com.jhy.project.schoollibrary.model.constant.Constant

class WebViewActivity : BaseViewBindingActivity<ActivityWebViewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                blockButton()
                onBackPressed()
            }
        }
        binding.webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    view?.loadUrl(it)
                }
                return true
            }
        }
        binding.webView.loadUrl(intent.getStringExtra(Constant.URL) ?: "")
    }

    override val bindingInflater: (LayoutInflater) -> ActivityWebViewBinding
        get() = ActivityWebViewBinding::inflate

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.exit_to_right, R.anim.enter_from_left)
    }
}