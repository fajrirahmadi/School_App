package com.jhy.project.schoollibrary.component

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentSimpleWebBinding
import com.jhy.project.schoollibrary.extension.setImage

class SimpleWebFragment: BaseViewBindingFragment<FragmentSimpleWebBinding>() {

    private val args by navArgs<SimpleWebFragmentArgs>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSimpleWebBinding
        get() = FragmentSimpleWebBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews()
    }

    private fun bindViews() {
        binding.apply {
            args.image?.let {
                coverIv.setImage(it)
            } ?: run {
                coverIv.isVisible = false
            }

            args.title?.let {
                titleTv.text = it
            } ?: run {
                titleTv.isVisible = false
            }

            args.content?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contentTv.text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    @Suppress("DEPRECATION")
                    contentTv.text = Html.fromHtml(it)
                }
            }
        }
    }
}