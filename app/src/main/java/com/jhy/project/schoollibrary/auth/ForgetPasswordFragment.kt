package com.jhy.project.schoollibrary.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentAuthForgetPasswordBinding
import com.jhy.project.schoollibrary.extension.isValidEmail
import com.jhy.project.schoollibrary.extension.popBack
import com.jhy.project.schoollibrary.extension.showToast
import com.jhy.project.schoollibrary.extension.trim
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordFragment : BaseViewBindingFragment<FragmentAuthForgetPasswordBinding>() {

    private val viewModel by viewModels<AuthViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAuthForgetPasswordBinding
        get() = FragmentAuthForgetPasswordBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener {
            when {
                !binding.etEmail.trim.isValidEmail() -> showToast("Email tidak valid")
                else -> viewModel.sendEmailReset(requireContext(), binding.etEmail.trim)
            }
        }
        viewModel.requestState.observe(viewLifecycleOwner) {
            when (it.second) {
                LiveDataTag.forgetPassword ->
                    if (it.first) {
                        popBack()
                    }
            }
        }
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }
}