package com.jhy.project.schoollibrary.feature.school.galeri

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Gallery
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    private var galleryJob: Job? = null
    private fun loadGallery() {
        showLoading(galleryState.isEmpty())
        galleryJob?.cancel()
        galleryJob = viewModelScope.launch {
            observeStatefulCollection<Gallery>(
                db.loadGallery()
            ).collect {
                if (it is FirestoreState.Success) {
                    galleryState = it.data.asList()
                }
                postDelayed { showLoading(false) }
            }
        }
    }

}