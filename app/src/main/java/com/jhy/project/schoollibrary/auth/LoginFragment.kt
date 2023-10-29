package com.jhy.project.schoollibrary.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentAuthLoginBinding
import com.jhy.project.schoollibrary.extension.showToast
import com.jhy.project.schoollibrary.extension.string
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseViewBindingFragment<FragmentAuthLoginBinding>() {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            doLogin()
        }
        binding.btnForgetPassword.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionToForgetPasswordFragment(), getNavOptions()
            )
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionToRegistrationFragment(), getNavOptions()
            )
        }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            when (it.second) {
                LiveDataTag.login -> if (it.first) {
                    requireActivity().finish()
                } else showToast("Login failed")
            }
        }
    }

    private fun doLogin() {
        binding.apply {
            when {
                etPassword.string.length < 6 -> showToast("Minimal password 6 digit")
                else -> viewModel.doLoginByEmailAndPassword(
                    etEmail.string, etPassword.string
                )
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAuthLoginBinding
        get() = FragmentAuthLoginBinding::inflate
}