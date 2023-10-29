package com.jhy.project.schoollibrary.feature.school.siswaalumni

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.alumni
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SAViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    private var keyword: String = ""
    private val defaultKelas = "VII.1"
    private val defaultYear = Calendar.getInstance().get(Calendar.YEAR).toString()

    private val _pageState = MutableStateFlow(alumni)
    val pageState = _pageState.asStateFlow()
    fun updatePage(selectedPage: String) {
        _pageState.value = selectedPage
        if (selectedPage == alumni) updateAlumniTahun(defaultYear)
        else updateKelas(defaultKelas)
    }

    private val _userListState = MutableStateFlow(emptyList<User>())
    val userListState = _userListState.asStateFlow()

    private val _alumniTahunState = MutableStateFlow(defaultYear)
    val alumniTahunState = _alumniTahunState.asStateFlow()
    fun updateAlumniTahun(tahun: String) {
        _alumniTahunState.value = tahun
        val kelas = "alumni.${_alumniTahunState.value}"
        loadUser(kelas)
    }

    private val _kelasListState = MutableStateFlow(emptyList<String>())
    val kelasListState = _kelasListState.asStateFlow()

    private val _kelasState = MutableStateFlow(defaultKelas)
    val kelasState = _kelasState.asStateFlow()
    fun updateKelas(kelas: String) {
        _kelasState.value = kelas
        loadUser(kelas)
    }

    fun onCreate() {
        loadKelasFilter()
        updatePage(alumni)
    }

    private fun loadKelasFilter() {
        db.getKelas().addOnCompleteListener {
            if (it.isSuccessful) {
                _kelasListState.value = it.result.toObjects(Kelas::class.java).map {
                    it.name.uppercase()
                }
            }
        }
    }

    private fun loadUser(kelas: String) {
        showLoading()
        db.loadUserListByRoleAndClass(_pageState.value, kelas, keyword, 50).addOnCompleteListener {
            if (it.isSuccessful) {
                _userListState.value = it.result.toObjects(User::class.java)
            }
            postDelayed { showLoading(false) }
        }
    }

    fun doSearch(keyword: String) {
        if (this.keyword == keyword) return
        this.keyword = keyword
        updatePage(_pageState.value)
    }
}