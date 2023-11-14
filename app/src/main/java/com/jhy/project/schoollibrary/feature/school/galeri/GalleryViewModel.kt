package com.jhy.project.schoollibrary.feature.school.galeri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Gallery
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var galleryState by mutableStateOf<List<Gallery>>(emptyList())
        private set
    var isAdmin by mutableStateOf(false)
        private set

    fun onCreate() {
        loadUserData {
            isAdmin = it.role == admin
        }
        loadGallery()
    }

    private fun loadGallery(online: Boolean = false) {
        showLoading(galleryState.isEmpty())
        db.loadGallery(online).addOnCompleteListener {
            if (it.isSuccessful) {
                galleryState = it.result.toObjects(Gallery::class.java)
            }
            if (!online) {
                loadGallery(true)
            }
            postDelayed { showLoading(false) }
        }
    }

}