package com.jhy.project.schoollibrary.feature.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.extension.compressImage
import com.jhy.project.schoollibrary.extension.createFileFromUri
import com.jhy.project.schoollibrary.model.ProfileMenu
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.adapter.ProfileMenuAdapter
import com.jhy.project.schoollibrary.model.guru
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    private val _adapter = ItemAdapter<ProfileMenuAdapter>()
    val adapter by lazy {
        FastAdapter.with(_adapter)
    }

    var user by mutableStateOf<User?>(null)
        private set

    var userInformation by mutableStateOf<List<Pair<String, String>>>(emptyList())
        private set

    fun onCreate() {
        loadUserData {
            user = it
            updateUserInformation(it)
        }
    }

    private fun updateUserInformation(user: User) {
        val infoList = mutableListOf<Pair<String, String>>()

        val id = when (user.role) {
            siswa -> "NIS"
            guru -> "NIP"
            else -> "No. ID"
        }
        infoList.add(Pair("Role", user.role.capitalizeWord()))
        infoList.add(Pair(id, user.no_id ?: " - "))
        infoList.add(
            Pair(
                "Tempat/Tanggal Lahir",
                "${(user.tempatLahir ?: " - ").capitalizeWord()}/${user.tanggalLahir ?: " - "}"
            )
        )
        infoList.add(Pair("Jenis Kelamin", user.gender.capitalizeWord()))
        infoList.add(Pair("Agama", user.agama?.capitalizeWord() ?: "Islam"))
        infoList.add(Pair("Alamat", user.alamat ?: " - "))

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

    fun logout() {
        db.signOut()
    }

    fun updateImage(context: Context, uri: String) {
        showLoading()
        context.createFileFromUri(db.getUid(), Uri.parse(uri))?.compressImage()?.let { file ->
            val path = "profile/${file.nameWithoutExtension}"
            db.uploadImageToServer(path, file)
                .addOnSuccessListener {
                    it.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                        db.updateProfileImage(it.result.toString()).addOnCompleteListener {
                            loadUserData()
                            postDelayed { dismissLoading() }
                        }
                    } ?: run {
                        dismissLoading()
                    }
                }.addOnFailureListener {
                    dismissLoading()
                }
        } ?: run { dismissLoading() }
    }

}