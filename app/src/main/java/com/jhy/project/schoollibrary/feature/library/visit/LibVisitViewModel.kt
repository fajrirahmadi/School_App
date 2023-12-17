package com.jhy.project.schoollibrary.feature.library.visit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.VisitCount
import com.jhy.project.schoollibrary.model.Visitor
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibVisitViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var isAdmin by mutableStateOf(false)
        private set
    var visitors by mutableStateOf(emptyList<Visitor>())
        private set

    var dailyVisitors by mutableIntStateOf(0)
        private set

    fun onCreate() {
        loadUserData {
            isAdmin = it.role == admin
        }
        loadDailyVisitors()
        loadVisitors()
    }

    private var visitorCountJob: Job? = null
    private fun loadDailyVisitors() {
        visitorCountJob?.cancel()
        visitorCountJob = viewModelScope.launch {
            observeStatefulDoc<VisitCount>(
                db.loadDailyVisitorCounter()
            ).collect {
                if (it is FirestoreState.Success) {
                    it.data?.let { visitCount ->
                        val day = System.currentTimeMillis().toDateFormat("dd")
                        dailyVisitors = visitCount.totalDaily[day] ?: 0
                    }
                }
            }
        }
    }

    private var visitorJob: Job? = null
    private fun loadVisitors() {
        visitorJob?.cancel()
        visitorJob = viewModelScope.launch {
            observeStatefulCollection<Visitor>(
                db.loadVisitors()
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> showLoading(false)
                    is FirestoreState.Loading -> showLoading(true)
                    is FirestoreState.Success -> {
                        visitors = it.data.asList()
                        postDelayed { showLoading(false) }
                    }
                }
            }
        }
    }

    fun submitVisitor(user: User) {
        showLoading()
        val visitor = Visitor(
            "", user.name ?: "", user.kelas ?: "", user.no_id ?: "", user.gender, userKey = user.key
        )
        db.submitVisitor(visitor).addOnCompleteListener {
            if (it.isSuccessful) {
                requestState.value = Pair(true, "Berhasil membuat kunjungan baru")
                onCreate()
            } else {
                requestState.value = Pair(false, "Gagal membuat kunjugan baru")
            }
            postDelayed { showLoading(false) }
        }
    }

    private var searchUserJob: Job? = null
    fun searchUserByNis(nis: String) {
        searchUserJob?.cancel()
        searchUserJob = viewModelScope.launch {
            observeStatefulCollection<User>(
                db.searchUserByNis(nis)
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> searchNotFound()
                    is FirestoreState.Loading -> showLoading()
                    is FirestoreState.Success -> {
                        it.data.firstOrNull()?.let { user -> submitVisitor(user) }
                            ?: run { searchNotFound() }
                    }
                }
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
            if (it.isSuccessful) onCreate()
        }
    }

}