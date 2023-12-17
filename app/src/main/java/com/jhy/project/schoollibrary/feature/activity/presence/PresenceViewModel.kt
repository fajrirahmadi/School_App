package com.jhy.project.schoollibrary.feature.activity.presence

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Absence
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.NIS
import com.jhy.project.schoollibrary.model.Presence
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.constant.Result
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.loadUserListByRoleAndClass
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresenceViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var absenceKey by mutableStateOf<String?>(null)
        private set

    var mapelState by mutableStateOf<Mapel?>(null)
        private set

    var pertemuanText by mutableStateOf("")
    var jurnalText by mutableStateOf("")

    var kelas by mutableStateOf("")
        private set

    var siswaState by mutableStateOf<List<User>>(emptyList())
        private set

    var presenceState by mutableStateOf<Map<NIS, Presence>>(emptyMap())
        private set

    private val _validationState = MutableStateFlow<Result?>(null)
    val validationState = _validationState.asStateFlow()

    fun onCreate(key: String?, mapel: Mapel, kelas: String) {
        this.absenceKey = key
        this.mapelState = mapel
        this.kelas = kelas

        key?.let {
            loadAbsence(it)
        } ?: run {
            loadSiswaByKelas()
        }
    }

    private var absenceJob: Job? = null
    private fun loadAbsence(key: String) {
        showLoading()
        absenceJob?.cancel()
        absenceJob = viewModelScope.launch {
            observeStatefulDoc<Absence>(
                db.loadAbsenceByKey(key)
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> showLoading(false)
                    is FirestoreState.Loading -> showLoading()
                    is FirestoreState.Success -> {
                        it.data?.let { absence ->
                            pertemuanText = absence.pertemuan
                            jurnalText = absence.jurnal
                            presenceState = absence.absence
                        }
                    }
                }
                loadSiswaByKelas()
            }
        }
    }

    var siswaJob: Job? = null
    private fun loadSiswaByKelas() {
        showLoading()
        val kelasParams = kelas.split("_").lastOrNull() ?: ""
        siswaJob?.cancel()
        siswaJob = viewModelScope.launch {
            observeStatefulCollection<User>(
                db.loadUserListByRoleAndClass(siswa, kelasParams)
            ).collect {
                if (it is FirestoreState.Success) {
                    siswaState = it.data.asList()
                }
                postDelayed { showLoading(false) }
            }
        }
    }

    fun updatePresence(noId: String?, presence: Presence) {
        noId?.let {
            val currentValue = presenceState.toMutableMap()
            currentValue[it] = presence
            presenceState = currentValue
        }
    }

    fun updateData() {
        when {
            pertemuanText.isEmpty() -> {
                _validationState.value = Result.Error("Pertemuan tidak boleh kosong")
            }

            jurnalText.isEmpty() -> {
                _validationState.value = Result.Error("Jurnal tidak boleh kosong")
            }

            else -> {
                val siswaMap = siswaState.associate { (it.no_id ?: "") to (it.name ?: "") }
                val kelas = this.kelas.split("_").lastOrNull() ?: ""
                val tahun = this.kelas.split("_").firstOrNull() ?: ""
                val semester = this.kelas.split("_").getOrNull(1) ?: ""
                val mapel = mapelState?.name ?: ""
                val guru = mapelState?.guru ?: ""
                val kodeGuru = mapelState?.kode ?: ""
                val absence = Absence(
                    absenceKey ?: "",
                    mapel,
                    guru,
                    kodeGuru,
                    pertemuanText,
                    jurnalText,
                    kelas,
                    siswaMap,
                    presenceState,
                    tahun,
                    semester,
                    listOf("${tahun}_${semester}_${kelas}_${kodeGuru}", "${tahun}_${semester}_${kelas}", "${tahun}_${semester}",
                        tahun, kelas, kodeGuru, "${kelas}_${kodeGuru}"
                    )
                )

                showLoading()
                db.submitAbsence(absence).addOnCompleteListener {
                    if (it.isSuccessful) {
                        _validationState.value = Result.Success("Berhasil menambahkan pertemuan")
                    }
                    postDelayed { showLoading(false) }
                }
            }
        }
    }
}