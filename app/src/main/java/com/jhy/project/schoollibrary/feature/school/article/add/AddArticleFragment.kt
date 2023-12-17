package com.jhy.project.schoollibrary.feature.school.article.add

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.EditTextWithTitle
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.MultipleLineEditText
import com.jhy.project.schoollibrary.component.compose.PrimaryButton
import com.jhy.project.schoollibrary.component.compose.loadImage
import com.jhy.project.schoollibrary.extension.popBack
import com.jhy.project.schoollibrary.extension.showToast
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.constant.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddArticleFragment : BaseComposeFragment() {

    private val viewModel by viewModels<AddArticleViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MaterialTheme {

                var selectedImageUri by remember {
                    mutableStateOf<Uri?>(null)
                }
                val imagePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    selectedImageUri = uri
                }
                val askPermission =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                        if (it) imagePickerLauncher.launch("image/*")
                        else showToast("Please allow permission to pick image")
                    }

                var title by remember {
                    mutableStateOf("")
                }
                var description by remember {
                    mutableStateOf("")
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            selectedImageUri?.let {
                                loadImage(
                                    context = requireContext(),
                                    url = selectedImageUri.toString(),
                                    defaultImage = R.drawable.ic_logo_smp
                                ).value?.let {
                                    ImageComponent(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        frame = it,
                                        scale = ContentScale.Crop
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    AppColor.neutral40
                                                ),
                                                startY = 200f
                                            )
                                        ),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    PrimaryButton(text = "Ubah Cover") {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            askPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                        } else {
                                            askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                    }
                                }
                            }
                                ?: run {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(
                                                width = 2.dp,
                                                color = AppColor.greenSoft
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        PrimaryButton(text = "Tambah Cover") {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                askPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                            } else {
                                                askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                            }
                                        }
                                    }
                                }

                        }
                    }
                    item {
                        EditTextWithTitle(
                            title = "Judul Artikel/Berita",
                            text = title,
                            onTextChange = {
                                title = it
                            }
                        )
                    }
                    item {
                        MultipleLineEditText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            value = description,
                            onValueChange = { description = it },
                            hintText = "Masukkan isi artikel/berita"
                        )
                    }
                    item {
                        PrimaryButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            text = "Submit"
                        ) {
                            viewModel.submitArticle(
                                requireContext(),
                                selectedImageUri,
                                title,
                                description
                            )
                        }
                    }
                }
            }
        }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        lifecycleScope.launch {
            viewModel.resultState.collectLatest {
                when (it) {
                    is Result.Success -> showInfoDialog(it.message) {
                        findNavController().navigateUp()
                    }

                    is Result.Error -> showInfoDialog(it.message)

                    else -> {}
                }
            }
        }

        viewModel.onCreate()
    }
}