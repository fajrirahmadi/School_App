package com.jhy.project.schoollibrary.feature.library.visit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.VisitCount
import com.jhy.project.schoollibrary.model.Visitor
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibVisitViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var isAdmin by mutableStateOf(false)
        private set
    var visitors by mutableStateOf(emptyList<Visitor>())
        private set

    var dailyVisitors by mutableIntStateOf(0)
        private set

    fun onCreate(online: Boolean = false) {
        loadUserData {
            isAdmin = it.role == admin
        }
        loadDailyVisitors(online)
        loadVisitors(online)
    }

    private fun loadDailyVisitors(online: Boolean = false) {
        db.loadDailyVisitorCounter(online).addOnCompleteListener {
            if (it.isSuccessful && it.result.exists()) {
                val visitCount = it.result.toObject(VisitCount::class.java)
                visitCount?.let {
                    val day = System.currentTimeMillis().toDateFormat("dd")
                    dailyVisitors = visitCount.totalDaily[day] ?: 0
                }
            }
            if (!online) loadDailyVisitors(true)
        }
    }

    private fun loadVisitors(online: Boolean = false) {
        showLoading(visitors.isEmpty())
        db.loadVisitors(online).addOnCompleteListener {
            if (it.isSuccessful) {
                visitors = it.result.toObjects(Visitor::class.java)
            }
            if (!online) loadVisitors(true)
            postDelayed { showLoading(false) }
        }
    }

    fun submitVisitor(user: User) {
        showLoading()
        val visitor = Visitor(
            "",
            user.name ?: "",
            user.kelas ?: "",
            user.no_id ?: "",
            user.gender ?: ""
        )
        db.submitVisitor(visitor).addOnCompleteListener {
            if (it.isSuccessful) {
                requestState.value = Pair(true, "Berhasil membuat kunjungan baru")
                onCreate(true)
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

    fun removeVisitRecord(visitor: Visitor) {
        showLoading()
        db.removeVisitRecord(visitor).addOnCompleteListener {
            dismissLoading()
            val message = if (it.isSuccessful) "Berhasil" else "Gagal"
            requestState.value = Pair(it.isSuccessful, "$message menghapus data kunjungan")
            if (it.isSuccessful) onCreate(true)
        }
    }

}