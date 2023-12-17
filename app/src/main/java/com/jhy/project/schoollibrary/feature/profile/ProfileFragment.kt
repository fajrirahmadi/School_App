package com.jhy.project.schoollibrary.feature.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.BuildConfig
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.ConfirmationBottomSheet
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.WorkSandTextSemiBold
import com.jhy.project.schoollibrary.component.compose.features.InformationSection
import com.jhy.project.schoollibrary.component.compose.loadImage
import com.jhy.project.schoollibrary.component.confirmationBottomSheet
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.feature.MainActivity
import com.jhy.project.schoollibrary.model.ProfileMenu
import com.jhy.project.schoollibrary.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseComposeFragment() {

    private val viewModel by viewModels<ProfileViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme {
                val profile = viewModel.user
                val userInformation = viewModel.userInformation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            AppColor.blueSoft
                        ), verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HeaderSection(profile)
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            profile?.let {
                                InformationSection(
                                    title = "Information",
                                    userInformation = userInformation
                                )
                            }
                        }
                        item {
                            MenuSection(profile)
                        }
                        item {
                            WorkSandTextMedium(
                                text = "Version ${BuildConfig.VERSION_NAME}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onCreate()
    }

    @Composable
    private fun HeaderSection(profile: User?) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp),
            backgroundColor = AppColor.white
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                loadImage(
                    context = requireContext(),
                    url = profile?.url,
                    defaultImage = R.drawable.dummy_profile
                ).value?.let {
                    ImageComponent(
                        modifier = Modifier.size(56.dp), frame = it
                    )
                }
                VerticalSpace(height = 8.dp)
                WorkSandTextSemiBold(
                    text = profile?.name ?: "Hi, Guest", size = 16.sp
                )
                profile?.email?.let {
                    WorkSandTextNormal(
                        text = it, size = 14.sp
                    )
                } ?: run {
                    WorkSandTextNormal(text = "Masuk dengan akun Anda")
                }

                if (profile == null) {
                    PrimaryButton(text = "Masuk") {
                        navigateToLoginPage()
                    }
                } else {

                }
            }
        }
    }

    @Composable
    private fun MenuSection(profile: User?) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = AppColor.white,
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WorkSandTextSemiBold(
                    text = "Pengaturan", size = 16.sp
                )
                ProfileMenu.values()
                    .filter { if (profile == null) it != ProfileMenu.Logout else true }.forEach {
                        WorkSandTextNormal(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (it) {
                                        ProfileMenu.PrivacyPolicy -> showWeb("https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/url%2Fprivacy_policy.html?alt=media&token=215d30e9-5a37-420e-8f65-0defb69ca80b")
                                        ProfileMenu.TermOfCondition -> showWeb("https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/url%2Fterm_and_condition.html?alt=media&token=4193d3a8-9479-49d9-b2c6-fe7bf60711e1")
                                        ProfileMenu.About -> showWeb("https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/url%2Fabout_us.html?alt=media&token=e8abf39d-8f6f-416d-a712-3207579f5ada")
                                        ProfileMenu.Logout -> showLogoutSheet()
                                    }
                                }, text = it.key, textAlign = TextAlign.Left
                        )
                        Divider(
                            color = AppColor.neutral40
                        )
                    }
            }
        }
    }

    private fun showLogoutSheet() {
        showBottomSheet(
            ConfirmationBottomSheet("Konfirmasi", "Yakin ingin keluar dari aplikasi?", actionYes = {
                viewModel.logout()
                postDelayed({
                    val intent = Intent(
                        requireActivity(), MainActivity::class.java
                    )
                    intent.flags =
                        (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    requireActivity().finish()
                }, 2_00L)
            }), confirmationBottomSheet
        )
    }
}