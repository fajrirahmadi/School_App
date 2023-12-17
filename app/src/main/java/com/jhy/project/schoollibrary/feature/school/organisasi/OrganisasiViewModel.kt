package com.jhy.project.schoollibrary.feature.school.organisasi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.SchoolOrganisasiModel
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrganisasiViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var organisasiState by mutableStateOf<SchoolOrganisasiModel?>(null)
        private set

    fun onCreate() {
        loadOrganisasi()
    }

    private var schoolJob: Job? = null
    private fun loadOrganisasi() {
        showLoading(organisasiState == null)
        schoolJob?.cancel()
        schoolJob = viewModelScope.launch {
            observeStatefulDoc<SchoolOrganisasiModel>(
                db.getOrganisasi()
            ).collect {
                if (it is FirestoreState.Success) {
                    organisasiState = it.data
                }
                postDelayed { showLoading(false) }
            }
        }
    }
}