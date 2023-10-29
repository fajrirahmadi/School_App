package com.jhy.project.schoollibrary.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentAuthRegistrasiBinding
import com.jhy.project.schoollibrary.extension.*
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationFragment : BaseViewBindingFragment<FragmentAuthRegistrasiBinding>() {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubmit.setOnClickListener {
            blockButton()
            verifyInput()
        }
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        viewModel.requestState.observe(viewLifecycleOwner) {
            when (it.second) {
                LiveDataTag.register ->
                    if (it.first) {
                        showToast("Registrasi berhasil, selamat datang di Sajak Senja")
                        popBack()
                    } else showToast("Registrasi gagal")
            }
        }
    }

    private fun verifyInput() {
        binding.apply {
            when {
                !etEmail.trim.isValidEmail() -> showToast("Email tidak valid")
                etNama.trim.isEmpty() -> showToast("Nama tidak boleh kosong")
                etPassword.trim.length < 8 -> showToast("Minimal kata sandi 8 digit")
                !etPassword.sameAs(etPasswordConfirm) -> showToast("Kata sandi konfirmasi tidak sesuai")
                else -> viewModel.createNewUser(etEmail.trim, etNama.trim, etPassword.string)
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAuthRegistrasiBinding
        get() = FragmentAuthRegistrasiBinding::inflate
}