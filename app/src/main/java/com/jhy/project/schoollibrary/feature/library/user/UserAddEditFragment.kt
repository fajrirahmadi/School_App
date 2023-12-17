package com.jhy.project.schoollibrary.feature.library.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentUserAddEditBinding
import com.jhy.project.schoollibrary.extension.trim
import com.jhy.project.schoollibrary.model.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAddEditFragment : BaseViewBindingFragment<FragmentUserAddEditBinding>() {

    private val args by navArgs<UserAddEditFragmentArgs>()
    private val viewModel by viewModels<UsersViewModel>()

    private var gender = pria

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.loadUserData()
    }

    private fun bindView() {
        binding.apply {
            args.user?.let { user ->
                idEt.apply {
                    isEnabled = false
                    setText(user.no_id)
                }
                nameEt.setText(user.name)
                pinjamBtn.apply {
                    isVisible = true
                    setOnClickListener {
                        findNavController().navigate(
                            UserAddEditFragmentDirections.actionToPinjamFragment(
                                args.user?.key ?: ""
                            ),
                            getNavOptions()
                        )
                    }
                }
                cetakBtn.setOnClickListener {
                    if (user.pinjaman > 0) {
                        showInfoDialog(
                            requireContext(),
                            "${user.name} masih ada pinjaman buku, silakan kembalikan terlebih dahulu"
                        )
                    } else {
                        viewModel.userState.value?.let { profile ->
                            val superAdmin = viewModel.kepalaPustaka
                            showBottomSheet(
                                CetakBottomSheet(
                                    user,
                                    if ("admin".equals(user.role, true)) profile.name ?: superAdmin else superAdmin
                                ),
                                cetakBottomSheet
                            )
                        }
                    }
                }

                kelasEt.apply {
                    isVisible = args.type == siswa
                    setText(user.kelas?.toKelasText())
                    setOnClickListener {
                        showBottomSheet(
                            ClassListBottomSheet {
                                setText(it.value.toKelasText())
                            }, classListBottomSheet
                        )
                    }
                }
            }

            kelasLabel.isVisible = args.type == siswa

            btnSubmit.setOnClickListener {
                val user = args.user?.copy() ?: User()
                user.apply {
                    name = nameEt.trim
                    gender = this@UserAddEditFragment.gender
                    no_id = idEt.trim
                    email = "${idEt.trim}@mailinator.com"
                    kelas = kelasEt.trim.toKelasRequest()
                }
                viewModel.submitUser(user)
            }
            priaBtn.setOnClickListener {
                changeGender(pria)
            }
            wanitaBtn.setOnClickListener {
                changeGender(wanita)
            }
            gender = args.user?.gender ?: pria
            changeGender(gender)
        }
    }

    private fun changeGender(gender: String) {
        this.gender = gender
        val isMale = gender == pria

        binding.apply {
            priaBtn.isEnabled = !isMale
            wanitaBtn.isEnabled = isMale
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserAddEditBinding
        get() = FragmentUserAddEditBinding::inflate

}