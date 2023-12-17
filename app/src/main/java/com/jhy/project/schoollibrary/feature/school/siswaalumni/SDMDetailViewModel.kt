package com.jhy.project.schoollibrary.feature.school.siswaalumni

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.guru
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.model.toKelasText
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SDMDetailViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var user by mutableStateOf<User?>(null)
        private set

    var isAdmin by mutableStateOf(false)
        private set

    var userInformation by mutableStateOf<List<Pair<String, String>>>(emptyList())
        private set

    fun onCreate(key: String) {
        loadUserData {
            isAdmin = it.role == admin
            loadUserData(key) { detailUser ->
                user = detailUser
                updateUserInformation(detailUser)
            }
        }
    }

    private fun updateUserInformation(user: User) {
        val infoList = mutableListOf<Pair<String, String>>()

        infoList.add(Pair("Nama", user.name ?: " - "))

        val role =
            if (user.role == siswa) "Siswa ${user.kelas?.toKelasText()}"
            else user.role.capitalizeWord()
        infoList.add(Pair("Role", role))

        val id = when (user.role) {
            siswa -> "NIS"
            guru -> if (user.no_id.isNullOrEmpty()) "NIK" else "NIP"
            else -> "No. ID"
        }
        infoList.add(
            Pair(
                id,
                if (user.no_id.isNullOrEmpty()) user.nik ?: "-"
                else user.no_id ?: "-"
            )
        )
        infoList.add(
            Pair(
                "Tempat, Tanggal Lahir",
                "${(user.tempatLahir ?: "-").capitalizeWord().trim()}, ${user.tanggalLahir ?: "-"}"
            )
        )
        infoList.add(Pair("Jenis Kelamin", user.gender.capitalizeWord()))
        infoList.add(Pair("Agama", user.agama?.capitalizeWord() ?: "Islam"))
        infoList.add(Pair("Alamat", user.alamat ?: "-"))

        when (user.role) {
            siswa -> {
                infoList.add(Pair("Wali Murid", user.wali?.capitalizeWord() ?: " - "))
            }

            guru -> {
                infoList.add(
                    Pair(
                        "Status/Golongan",
                        "${user.status ?: " - "}/${user.golongan ?: " - "}"
                    )
                )
                infoList.add(Pair("Mata Pelajaran", user.mapel ?: " - "))
            }
        }

        userInformation = infoList
    }
}