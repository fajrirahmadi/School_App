package com.jhy.project.schoollibrary.feature.school.siswaalumni

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.alumni
import com.jhy.project.schoollibrary.model.guru
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.model.state.ErrorType
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.model.state.SDMListState
import com.jhy.project.schoollibrary.model.state.UIState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.loadUserListByRoleAndClass
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SDMViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    private var keyword: String = ""
    private val defaultKelas = "VII.1"
    private val defaultYear = Calendar.getInstance().get(Calendar.YEAR).toString()

    private val _pageState = MutableStateFlow(guru)
    val pageState = _pageState.asStateFlow()

    private val _alumniTahunState = MutableStateFlow(defaultYear)
    val alumniTahunState = _alumniTahunState.asStateFlow()

    private val _kelasListState = MutableStateFlow(emptyList<String>())
    val kelasListState = _kelasListState.asStateFlow()

    private val _kelasState = MutableStateFlow(defaultKelas)
    val kelasState = _kelasState.asStateFlow()

    var sdmState by mutableStateOf(SDMListState())
        private set

    fun onCreate() {
        if (sdmState.scrollState != 0) return
        loadKelasFilter()
        updatePage(guru)
    }

    fun updatePage(selectedPage: String) {
        _pageState.value = selectedPage
        when (selectedPage) {
            alumni -> updateAlumniTahun(defaultYear)
            siswa -> updateKelas(defaultKelas)
            else -> loadUser("")
        }
    }

    fun updateAlumniTahun(tahun: String) {
        _alumniTahunState.value = tahun
        val kelas = "alumni.${_alumniTahunState.value}"
        loadUser(kelas)
    }

    fun updateKelas(kelas: String) {
        _kelasState.value = kelas
        loadUser(kelas)
    }

    var kelasJob: Job? = null
    private fun loadKelasFilter() {
        kelasJob?.cancel()
        kelasJob = viewModelScope.launch {
            observeStatefulCollection<Kelas>(
                db.getKelas()
            ).collect {
                if (it is FirestoreState.Success) {
                    _kelasListState.value = it.data.asList<Kelas>().map { it.name.uppercase() }
                }
            }
        }
    }

    var guruJob: Job? = null
    private fun loadUser(kelas: String) {
        guruJob?.cancel()
        guruJob = viewModelScope.launch {
            observeStatefulCollection<User>(
                db.loadUserListByRoleAndClass(_pageState.value, kelas, keyword, 100)
            ).collect {
                sdmState = when (it) {
                    is FirestoreState.Failed -> sdmState.copy(
                        userListState = UIState.Error(ErrorType.NetworkError)
                    )

                    is FirestoreState.Loading -> sdmState.copy(
                        userListState = UIState.Loading
                    )

                    is FirestoreState.Success -> sdmState.copy(
                        userListState = UIState.Success(it.data),
                        scrollState = 0
                    )
                }
            }
        }
    }

    fun doSearch(keyword: String) {
        if (this.keyword == keyword) return
        this.keyword = keyword
        updatePage(_pageState.value)
    }

    fun updateScrollState(index: Int) {
        sdmState = sdmState.copy(
            scrollState = index
        )
    }
}