package com.jhy.project.schoollibrary.feature.profile

import android.content.Context
import android.net.Uri
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.compressImage
import com.jhy.project.schoollibrary.extension.createFileFromUri
import com.jhy.project.schoollibrary.model.ProfileMenu
import com.jhy.project.schoollibrary.model.adapter.ProfileMenuAdapter
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

    fun onCreate() {
        initAdapter()
        loadUserData()
    }

    private fun initAdapter() {
        _adapter.clear()
        for (data in ProfileMenu.values()) {
            _adapter.add(ProfileMenuAdapter(data))
        }
        adapter.notifyAdapterDataSetChanged()
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