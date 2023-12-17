package com.jhy.project.schoollibrary.feature.school.fasilitas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.SchoolFacilityModel
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FasilitasViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var facilityState by mutableStateOf<SchoolFacilityModel?>(null)
        private set

    fun onCreate() {
        loadFacility()
    }

    private var schoolJob: Job? = null
    private fun loadFacility() {
        showLoading(facilityState == null)
        schoolJob?.cancel()
        schoolJob = viewModelScope.launch {
            observeStatefulDoc<SchoolFacilityModel>(
                db.getFasilitas()
            ).collect {
                if (it is FirestoreState.Success) {
                    facilityState = it.data
                }
                postDelayed { showLoading(false) }
            }
        }
    }
}