package com.jhy.project.schoollibrary.feature.activity.presence

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.Absence
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.NIS
import com.jhy.project.schoollibrary.model.Presence
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.constant.Result
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private fun loadAbsence(key: String) {
        showLoading()
        db.loadAbsenceByKey(key).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toObject(Absence::class.java)?.let {
                    pertemuanText = it.pertemuan
                    jurnalText = it.jurnal
                    presenceState = it.absence
                }
            }
            loadSiswaByKelas()
        }
    }

    private fun loadSiswaByKelas() {
        showLoading()
        val kelasParams = kelas.split("_").lastOrNull() ?: ""
        db.loadUserListByRoleAndClass(siswa, kelasParams)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    siswaState = it.result.toObjects(User::class.java)
                }
                postDelayed { dismissLoading() }
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