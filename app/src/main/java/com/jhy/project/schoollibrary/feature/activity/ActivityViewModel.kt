package com.jhy.project.schoollibrary.feature.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.constanta.RemoteConfigHelper
import com.jhy.project.schoollibrary.model.Absence
import com.jhy.project.schoollibrary.model.Asesmen
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.toKelasRequest
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    db: FirebaseRepository,
    private val config: RemoteConfigHelper
) : BaseViewModel(db) {
    var selectedUser by mutableStateOf<String?>(null)
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
        loadUserData(selectedUser) {
            selectedUser = it.kode ?: ""
            loadMapel()
        }
    }

    private fun loadMapel() {
        selectedUser?.let { userCode ->
            val tahun = config.currentSchoolYear().replace("/", "_")
            val key = "${tahun}_${semester}_$userCode"
            db.loadMapelByCode(key).addOnCompleteListener {
                if (it.isSuccessful) {
                    mapelState = it.result.toObject(Mapel::class.java)
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

    private fun loadActivities() {
        selectedUser?.let { userCode ->
            val selectedDay = selectedFilter[ActivityFragment.SectionType.Kegiatan] ?: ""
            db.loadJadwalByCode(selectedDay, userCode).addOnCompleteListener {
                if (it.isSuccessful) {
                    todayActivities = it.result.toObjects(Jadwal::class.java)
                }
            }
        }
    }

    private fun loadPresence() {
        selectedUser?.let { userCode ->
            val tahun = config.currentSchoolYear().replace("/", "_")
            val kelas = selectedFilter[ActivityFragment.SectionType.Pertemuan]?.toKelasRequest() ?: ""
            db.loadAbsence(tahun + "_" + kelas + "_" + userCode).addOnCompleteListener {
                if (it.isSuccessful) {
                    val list = it.result.toObjects(Absence::class.java)
                    list.sortBy { absence -> absence.pertemuan }
                    absenceList = list
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