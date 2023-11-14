package com.jhy.project.schoollibrary.feature.main

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentMenuBinding
import com.jhy.project.schoollibrary.extension.*
import com.jhy.project.schoollibrary.model.HomeMenu
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.typeDeeplink
import com.jhy.project.schoollibrary.model.typeWeb
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuFragment : BaseViewBindingFragment<FragmentMenuBinding>() {

    private val viewModel by viewModels<MainMenuViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMenuBinding
        get() = FragmentMenuBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews()
        bindObservers()

    }

    @SuppressLint("SetTextI18n")
    private fun bindObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.userState.observe(viewLifecycleOwner) {
            binding.greetingTv.text = "Hi, ${it?.name?.firstWordCapitalize()}"
            binding.profileIv.setImage(it?.url, R.drawable.dummy_profile)
            binding.addArticleBtn.isVisible = it?.role == admin
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.onCreate()
    }

    private fun bindViews() {
        binding.profileIv.setOnClickListener {
            if (!viewModel.isLogin) {
                navigateToLoginPage()
                return@setOnClickListener
            }
            findNavController().navigate(
                MainMenuFragmentDirections.actionToProfileFragment(), getNavOptions()
            )
        }
        binding.bannerRv.initHorizontalAdapter(requireContext(), viewModel.bannerAdapter)
        viewModel.bannerAdapter.onClickListener = { _, _, item, _ ->
            navigate(item.menu)
            true
        }
        binding.menuRv.initGridAdapter(requireContext(), viewModel.homeMenuAdapter, 3)
        viewModel.homeMenuAdapter.onClickListener = { _, _, item, _ ->
            navigate(item.menu)
            true
        }
        binding.newsRv.initVerticalAdapter(requireContext(), viewModel.articleAdapter)
        viewModel.articleAdapter.onClickListener = { _, _, item, _ ->
            findNavController().navigate(
                MainMenuFragmentDirections.actionToArticleFragment(
                    item.article
                )
            )
            true
        }
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            viewModel.onCreate(true)
        }
        binding.addArticleBtn.setOnClickListener {
        }
    }

    private fun navigate(menu: HomeMenu) {
        when (menu.type) {
            typeWeb -> {
                showWeb(menu.direction)
            }
            typeDeeplink -> {
                try {
                    findNavController().navigate(Uri.parse(menu.direction), getNavOptions())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}