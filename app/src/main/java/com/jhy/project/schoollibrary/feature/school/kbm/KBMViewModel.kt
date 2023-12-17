package com.jhy.project.schoollibrary.feature.school.kbm

import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.constanta.RemoteConfigHelper
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KBMViewModel @Inject constructor(
    db: FirebaseRepository, private val config: RemoteConfigHelper
) : BaseViewModel(db) {

    val tahunAjaran: String = config.currentSchoolYear()

    private val kelasList = mutableListOf<Kelas>()
    private val _kelasFilterState = MutableStateFlow(emptyList<String>())
    val kelasFilterState = _kelasFilterState.asStateFlow()

    private val _kelasState = MutableStateFlow<Kelas?>(null)
    val kelasState = _kelasState.asStateFlow()

    private val _kelasSelectedState = MutableStateFlow("")
    val kelasSelectedState = _kelasSelectedState.asStateFlow()

    private val _mapelListState = MutableStateFlow(emptyList<Mapel>())
    val mapelListState = _mapelListState.asStateFlow()

    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")

    private val _daySelectedState = MutableStateFlow(0)
    val daySelectedState = _daySelectedState.asStateFlow()

    private val _jadwalListState = MutableStateFlow(emptyList<Jadwal>())
    val jadwalListState = _jadwalListState.asStateFlow()

    fun onCreate() {
        loadKelas()
    }

    var kelasJob: Job? = null
    private fun loadKelas() {
        showLoading()
        kelasJob?.cancel()
        kelasJob = viewModelScope.launch {
            observeStatefulCollection<Kelas>(
                db.getKelas()
            ).collect {
                if (it is FirestoreState.Success) {
                    kelasList.clear()
                    kelasList.addAll(it.data.asList())
                    updateKelasState(kelasList.map { it.name.uppercase() })
                }
                postDelayed { showLoading(false) }
            }
        }
    }

    private fun updateKelasState(kelasList: List<String>) {
        _kelasFilterState.value = kelasList
        if (kelasList.isEmpty()) return
        updateKelasSelected(kelasList[0])
    }

    fun updateKelasSelected(kelas: String) {
        _kelasSelectedState.value = kelas
        updateKelasInfo(kelas)
        loadMapelByKelas(kelas)
        updateDaySelected(0)
    }

    private fun updateKelasInfo(kelas: String) {
        kelasList.firstOrNull { kelas.equals(it.name, true) }?.let { kelasModel ->
            _kelasState.value = kelasModel
        }
    }

    var mapelJob: Job? = null
    private fun loadMapelByKelas(kelas: String) {
        val currentYear = config.currentSchoolYear().replace("/", "_")
        val filter = "${currentYear}_$kelas".lowercase()

        mapelJob?.cancel()
        mapelJob = viewModelScope.launch {
            observeStatefulCollection<Mapel>(
                db.loadMapelByKelas(filter)
            ).collect {
                if (it is FirestoreState.Success) {
                    _mapelListState.value = it.data.asList()
                }
            }
        }
    }

    fun updateDaySelected(index: Int) {
        _daySelectedState.value = index
        loadJadwal()
    }

    var jadwalJob: Job? = null
    private fun loadJadwal() {
        val day = days[_daySelectedState.value]
        val kelas = _kelasSelectedState.value
        val filter = "${tahunAjaran}_${day}_${kelas}"

        jadwalJob?.cancel()
        jadwalJob = viewModelScope.launch {
            observeStatefulCollection<Jadwal>(
                db.loadJadwalByFilter(filter)
            ).collect {
                if (it is FirestoreState.Success) {
                    updateJadwalState(it.data.asList())
                }
            }
        }
    }

    private fun updateJadwalState(jadwalList: List<Jadwal>) {
        _jadwalListState.value = jadwalList
    }

}