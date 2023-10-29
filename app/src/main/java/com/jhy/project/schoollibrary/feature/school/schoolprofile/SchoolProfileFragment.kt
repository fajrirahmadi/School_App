package com.jhy.project.schoollibrary.feature.school.schoolprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentSchoolProfileBinding
import com.jhy.project.schoollibrary.extension.initHorizontalAdapter
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.model.SchoolProfile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SchoolProfileFragment : BaseViewBindingFragment<FragmentSchoolProfileBinding>() {

    private val viewModel by viewModels<SchoolProfileViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSchoolProfileBinding
        get() = FragmentSchoolProfileBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        bindObservers()
    }

    private fun bindViews() {
        binding.filterRv.initHorizontalAdapter(requireContext(), viewModel.adapter)
    }

    private fun bindObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        lifecycleScope.launch {
            viewModel.imageState.collectLatest {
                binding.coverIv.setImage(it)
            }
        }

        lifecycleScope.launch {
            viewModel.nameState.collectLatest {
                binding.titleTv.text = it
            }
        }

        lifecycleScope.launch {
            viewModel.contentState.collectLatest {
                it?.let {
                    updateContent(it)
                }
            }
        }

        viewModel.onCreate()
    }

    private fun updateContent(model: SchoolProfile) {
        binding.filterRv.scrollToPosition(viewModel.selectedPage)
        binding.contentTv.loadDataWithBaseURL(null, model.content, "text/html", "utf-8", null);
    }
}