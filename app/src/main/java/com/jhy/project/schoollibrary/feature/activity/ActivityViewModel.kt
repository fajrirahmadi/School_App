package com.jhy.project.schoollibrary.feature.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.constanta.RemoteConfigHelper
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Absence
import com.jhy.project.schoollibrary.model.Asesmen
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.model.toKelasRequest
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    db: FirebaseRepository,
    private val config: RemoteConfigHelper
) : BaseViewModel(db) {
    var selectedUser by mutableStateOf<String?>(null)
        private set

    var userName by mutableStateOf<String?>(null)
        private set

    var isAdmin by mutableStateOf(false)
        private set

    var semester by mutableStateOf("1")
        private set

    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")

    var todayActivities by mutableStateOf<List<Jadwal>>(emptyList())
        private set

    var mapelState by mutableStateOf<Mapel?>(null)
        private set

    var selectedFilter by mutableStateOf<Map<ActivityFragment.SectionType, String>>(emptyMap())

    var absenceList by mutableStateOf<List<Absence>>(emptyList())
        private set

    var asesmenList by mutableStateOf<List<Asesmen>>(emptyList())
        private set

    fun onCreate() {
        loadUserData {
            if (it.role != admin) {
                updateUser(it.kode, it.name)
            } else {
                isAdmin = true
            }
        }
    }

    fun updateUser(kode: String?, name: String?) {
        selectedUser = kode
        userName = name
        loadMapel()
    }

    private var mapelJob: Job? = null
    private fun loadMapel() {
        selectedUser?.let { userCode ->
            val tahun = config.currentSchoolYear().replace("/", "_")
            val key = "${tahun}_${semester}_$userCode"
            mapelJob?.cancel()
            mapelJob = viewModelScope.launch {
                observeStatefulDoc<Mapel>(
                    db.loadMapelByCode(key)
                ).collect {
                    if (it is FirestoreState.Success) {
                        mapelState = it.data
                        if (!mapelState?.kelas.isNullOrEmpty()) {
                            val firstClass = mapelState?.kelas?.get(0) ?: ""
                            updateSelectedFilter(ActivityFragment.SectionType.Kegiatan, "Senin")
                            updateSelectedFilter(ActivityFragment.SectionType.Kurikulum, firstClass)
                            updateSelectedFilter(ActivityFragment.SectionType.Pertemuan, firstClass)
                        }
                    }
                }
            }
        }
    }

    fun updateSelectedFilter(section: ActivityFragment.SectionType, updatedValue: String) {
        val currentState = selectedFilter.toMutableMap()
        currentState[section] = updatedValue.split("_").lastOrNull() ?: ""
        selectedFilter = currentState

        when (section) {
            ActivityFragment.SectionType.Kegiatan -> {
                loadActivities()
            }

            ActivityFragment.SectionType.Kurikulum -> {

            }

            ActivityFragment.SectionType.Pertemuan -> {
                loadPresence()
            }

            ActivityFragment.SectionType.Asesmen -> {
                loadAsesmen()
            }
        }
    }

    private var activityJob: Job? = null
    private fun loadActivities() {
        selectedUser?.let { userCode ->
            val selectedDay = selectedFilter[ActivityFragment.SectionType.Kegiatan] ?: ""
            activityJob?.cancel()
            activityJob = viewModelScope.launch {
                observeStatefulCollection<Jadwal>(
                    db.loadJadwalByCode(selectedDay, userCode)
                ).collect {
                    if (it is FirestoreState.Success) {
                        todayActivities = it.data.asList()
                    }
                }
            }
        }
    }

    private var presenceJob: Job? = null
    private fun loadPresence() {
        selectedUser?.let { userCode ->
            val tahun = config.currentSchoolYear().replace("/", "_")
            val kelas =
                selectedFilter[ActivityFragment.SectionType.Pertemuan]?.toKelasRequest() ?: ""
            presenceJob?.cancel()
            presenceJob = viewModelScope.launch {
                observeStatefulCollection<Absence>(
                    db.loadAbsence(tahun + "_" + kelas + "_" + userCode)
                ).collect {
                    if (it is FirestoreState.Success) {
                        val list = it.data.asList<Absence>().toMutableList()
                        list.sortBy { absence -> absence.pertemuan }
                        absenceList = list
                    }
                }
            }
        }
    }

    private fun loadAsesmen() {
        selectedUser?.let { userCode ->
            val tahun = config.currentSchoolYear().replace("/", "_")
            val kelas = selectedFilter[ActivityFragment.SectionType.Asesmen] ?: ""
            val filter = "$tahun.$kelas.$userCode".toKelasRequest()
            db.loadAsesment(filter).addOnCompleteListener {
                if (it.isSuccessful) {

                }
            }
        }
    }
}