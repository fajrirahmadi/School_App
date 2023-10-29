package com.jhy.project.schoollibrary.feature.school.galeri.add

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseComposeFragment
import com.jhy.project.schoollibrary.component.compose.*
import com.jhy.project.schoollibrary.extension.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddGalleryFragment : BaseComposeFragment() {

    private val viewModel by viewModels<AddGalleryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            var selectedImageUris by remember {
                mutableStateOf<List<Uri>>(emptyList())
            }
            var albumName by remember { mutableStateOf("") }
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetMultipleContents()
            ) { uris ->
                val currentItems = mutableListOf<Uri>()
                currentItems.addAll(selectedImageUris)
                currentItems.addAll(uris)
                selectedImageUris = currentItems
            }
            val askPermission =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
                    if (it) imagePickerLauncher.launch("image/*")
                    else showToast("Please allow permission to pick image")
                }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                EditText(placeholder = "Input Nama Album",
                    keyboardType = KeyboardType.Text,
                    text = albumName,
                    onTextChange = {
                        albumName = it
                    })
                VerticalSpace(height = 16.dp)
                WorkSandButtonMedium(text = "Tambahkan Foto") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        askPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        askPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(
                    )
                ) {
                    items(selectedImageUris) { uri ->
                        loadImage(
                            context = requireContext(),
                            url = uri.toString(),
                            defaultImage = R.drawable.ic_logo_smp
                        ).value?.let {
                            Row {
                                Box(modifier = Modifier.size(100.dp)) {
                                    ImageComponent(frame = it, scale = ContentScale.Crop)
                                }
                                HorizontalSpace(width = 8.dp)
                            }
                        }
                    }
                }
                VerticalSpace(height = 24.dp)
                SelectedButton(modifier = Modifier.fillMaxWidth(), text = "Submit") {
                    viewModel.submitGallery(requireContext(), albumName, selectedImageUris)
                }
            }
        }

        initObservers()
    }

    private fun initObservers() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            showToast(it.second)
            if (it.first) {
                findNavController().popBackStack()
            }
        }
    }
}