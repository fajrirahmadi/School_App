package com.jhy.project.schoollibrary.feature.school.fasilitas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.SchoolFacilityModel
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FasilitasViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var facilityState by mutableStateOf<SchoolFacilityModel?>(null)
        private set

    fun onCreate() {
        loadFacility()
    }

    private fun loadFacility(online: Boolean = false) {
        showLoading(facilityState == null)
        db.getFasilitas(online).addOnCompleteListener {
            if (it.isSuccessful) {
                facilityState = it.result.toObject(SchoolFacilityModel::class.java)
            }

            if (!online) {
                loadFacility(true)
            }
            postDelayed { showLoading(false) }

        }
    }
}