package com.jhy.project.schoollibrary.feature.school.kbm

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.constanta.RemoteConfigHelper
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private fun loadKelas(online: Boolean = false) {
        showLoading()
        db.getKelas(online).addOnCompleteListener {
            if (it.isSuccessful) {
                kelasList.clear()
                kelasList.addAll(it.result.toObjects(Kelas::class.java))
                updateKelasState(kelasList.map { it.name.uppercase() })
            }
            if (!online) {
                loadKelas(true)
                return@addOnCompleteListener
            }
            postDelayed { showLoading(false) }
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

    private fun loadMapelByKelas(kelas: String) {
        val currentYear = config.currentSchoolYear()
        val filter = "${currentYear}_$kelas".lowercase()
        db.loadMapelByKelas(filter).addOnCompleteListener {
            if (it.isSuccessful) {
                _mapelListState.value = it.result.toObjects(Mapel::class.java)
            }
        }
    }

    fun updateDaySelected(index: Int) {
        _daySelectedState.value = index
        loadJadwal()
    }

    private fun loadJadwal() {
        val day = days[_daySelectedState.value]
        val kelas = _kelasSelectedState.value
        val filter = "${tahunAjaran}_${day}_${kelas}"

        db.loadJadwalByFilter(filter).addOnCompleteListener {
            if (it.isSuccessful) {
                updateJadwalState(it.result.toObjects(Jadwal::class.java))
            }
        }
    }

    private fun updateJadwalState(jadwalList: List<Jadwal>) {
        _jadwalListState.value = jadwalList
    }

}