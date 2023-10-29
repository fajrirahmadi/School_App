package com.jhy.project.schoollibrary.feature.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.auth.AuthActivity
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.component.ConfirmationBottomSheet
import com.jhy.project.schoollibrary.component.WebViewActivity
import com.jhy.project.schoollibrary.component.confirmationBottomSheet
import com.jhy.project.schoollibrary.databinding.FragmentProfileBinding
import com.jhy.project.schoollibrary.extension.initVerticalAdapter
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.model.ProfileMenu
import com.jhy.project.schoollibrary.model.constant.Constant
import com.jhy.project.schoollibrary.model.siswa
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseViewBindingFragment<FragmentProfileBinding>() {

    private val viewModel by viewModels<ProfileViewModel>()
    private var uri: String? = null

    private val askPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) imagePictureLauncher.launch("image/*") else
                showInfoDialog(
                    requireContext(),
                    "Berikan akses kamera & penyimpanan untuk menambahkan image"
                )
        }

    private val imagePictureLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                this.uri = uri.toString()
                viewModel.updateImage(requireContext(), uri.toString())
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userState.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    profileIv.apply {
                        setImage(
                            it.url,
                            placeholderResId = R.drawable.dummy_profile
                        )
                        clipToOutline = true
                    }
                    addPhotoBtn.setOnClickListener {
                        askPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    nameTv.text = it.name
                    idTv.text = "${
                        when (it.role) {
                            siswa -> "NIS: "
                            else -> "NIP: "
                        }
                    } ${it.no_id}"
                    listRv.initVerticalAdapter(requireContext(), viewModel.adapter)
                }
            }
        }

        viewModel.adapter.onClickListener = { _, _, data, _ ->
            when (data.data) {
                ProfileMenu.PrivacyPolicy -> showWeb("https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/url%2Fprivacy_policy.html?alt=media&token=215d30e9-5a37-420e-8f65-0defb69ca80b")
                ProfileMenu.TermOfCondition -> showWeb("https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/url%2Fterm_and_condition.html?alt=media&token=4193d3a8-9479-49d9-b2c6-fe7bf60711e1")
                ProfileMenu.About -> showWeb("https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/url%2Fabout_us.html?alt=media&token=e8abf39d-8f6f-416d-a712-3207579f5ada")
                ProfileMenu.Logout -> showLogoutSheet()
            }

            true
        }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.onCreate()
    }

    private fun showLogoutSheet() {
        showBottomSheet(ConfirmationBottomSheet(
            "Konfirmasi",
            "Yakin ingin keluar dari aplikasi?",
            actionYes = {
                viewModel.logout()
                postDelayed({
                    val intent = Intent(
                        requireActivity(),
                        AuthActivity::class.java
                    )
                    intent.flags =
                        (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    requireActivity().finish()
                }, 2_00L)
            }
        ), confirmationBottomSheet)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate
}