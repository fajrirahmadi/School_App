package com.jhy.project.schoollibrary.feature.school.extra

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.SchoolExtraModel
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtraViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var extraModel by mutableStateOf<SchoolExtraModel?>(null)
        private set

    fun onCreate() {
        loadExtraModel()
    }

    private var schoolJob: Job? = null
    private fun loadExtraModel() {
        showLoading(extraModel == null)
        schoolJob?.cancel()
        schoolJob = viewModelScope.launch {
            observeStatefulDoc<SchoolExtraModel>(
                db.getExtra()
            ).collect {
                if (it is FirestoreState.Success) {
                    extraModel = it.data
                }
                postDelayed { showLoading(false) }
            }
        }
    }
}