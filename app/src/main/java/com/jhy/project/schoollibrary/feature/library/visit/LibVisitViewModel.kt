package com.jhy.project.schoollibrary.feature.library.visit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Kunjungan
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibVisitViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var visitors by mutableStateOf(emptyList<Kunjungan>())
        private set

    fun onCreate() {
        loadVisitors()
    }

    private fun loadVisitors(online: Boolean = false) {
        showLoading(visitors.isEmpty())
        db.loadVisitors(online).addOnCompleteListener {
            if (it.isSuccessful) {
                visitors = it.result.toObjects(Kunjungan::class.java)
            }
            if (!online) loadVisitors(true)
            postDelayed { showLoading(false) }
        }
    }

    fun submitVisitor(user: User) {
        showLoading()
        val visitor = Kunjungan(
            "",
            user.name ?: "",
            user.no_id ?: ""
        )
        db.submitVisitor(visitor).addOnCompleteListener {
            if (it.isSuccessful) {
                requestState.value = Pair(true, "Berhasil membuat kunjungan baru")
                loadVisitors(true)
            } else {
                requestState.value = Pair(false, "Gagal membuat kunjugan baru")
            }
            postDelayed { showLoading(false) }
        }
    }

    fun searchUserByNis(nis: String) {
        showLoading()
        db.searchUserByNis(nis).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toObjects(User::class.java).firstOrNull()?.let { user ->
                    submitVisitor(user)
                } ?: run {
                    searchNotFound()
                }
            } else {
                searchNotFound()
            }
        }
    }

    private fun searchNotFound() {
        requestState.value = Pair(false, "Data tidak ditemukan")
        dismissLoading()
    }

}