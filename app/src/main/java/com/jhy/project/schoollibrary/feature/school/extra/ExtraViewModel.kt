package com.jhy.project.schoollibrary.feature.school.extra

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.SchoolExtraModel
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExtraViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var extraModel by mutableStateOf<SchoolExtraModel?>(null)
        private set

    fun onCreate() {
        loadExtraModel()
    }

    private fun loadExtraModel(online: Boolean = false) {
        showLoading(extraModel == null)
        db.getExtra(online).addOnCompleteListener {
            if (it.isSuccessful) {
                extraModel = it.result.toObject(SchoolExtraModel::class.java)
            }
            if (!online) {
                loadExtraModel(true)
            }
            postDelayed { showLoading(false) }
        }
    }
}