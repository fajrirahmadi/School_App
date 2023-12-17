package com.jhy.project.schoollibrary.feature.school.siswaalumni

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.ImageWithShimmer
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.features.InformationSection
import com.jhy.project.schoollibrary.extension.showBottomSheet
import com.jhy.project.schoollibrary.feature.library.user.KartuAnggotaSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SDMDetailFragment : BaseComposeFragment() {

    private val viewModel by viewModels<SDMDetailViewModel>()
    private val args by navArgs<SDMDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            val user = viewModel.user
            MaterialTheme {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        ImageWithShimmer(
                            modifier = Modifier
                                .width(75.dp)
                                .height(100.dp),
                            url = user?.url,
                            isLoading = user == null
                        )
                    }
                    item {
                        InformationSection(
                            title = "Informasi Umum",
                            userInformation = viewModel.userInformation
                        )
                    }
                    item {
                        user?.let {
                            if (viewModel.isAdmin) {
                                PrimaryButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    text = "Kartu Anggota"
                                ) {
                                    showBottomSheet(
                                        KartuAnggotaSheet(user)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        viewModel.onCreate(args.key)
    }
}