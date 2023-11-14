package com.jhy.project.schoollibrary.feature.school.organisasi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.SchoolOrganisasiModel
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrganisasiViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var organisasiState by mutableStateOf<SchoolOrganisasiModel?>(null)
        private set

    fun onCreate() {
        loadOrganisasi()
    }

    private fun loadOrganisasi(online: Boolean = false) {
        showLoading(organisasiState == null)
        db.getOrganisasi(online).addOnCompleteListener {
            if (it.isSuccessful) {
                organisasiState = it.result.toObject(SchoolOrganisasiModel::class.java)
            }

            if (!online) {
                loadOrganisasi(true)
            }

            postDelayed { showLoading(false) }

        }
    }
}