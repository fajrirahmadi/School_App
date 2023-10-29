package com.jhy.project.schoollibrary.feature.school.galeri.add

import android.content.Context
import android.net.Uri
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.compressImage
import com.jhy.project.schoollibrary.extension.createFileFromUri
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.Gallery
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddGalleryViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    fun submitGallery(context: Context, albumName: String, selectedImageUris: List<Uri>) {
        showLoading()
        var imageUploaded = 0
        val imageUploadedUrl = mutableListOf<String>()
        var imageFailed = 0
        selectedImageUris.forEachIndexed { index, uri ->
            uploadImage(context, "${albumName.lowercase()}_$index", uri) { success, imageUrl ->
                if (success) {
                    imageUploaded += 1
                    imageUploadedUrl.add(imageUrl)
                } else {
                    imageFailed += 1
                }
                if (imageUploaded + imageFailed == selectedImageUris.size) {
                    val gallery = Gallery(
                        name = albumName,
                        year = System.currentTimeMillis().toDateFormat("yyyy"),
                        items = imageUploadedUrl
                    )
                    db.submitGallery(gallery).addOnCompleteListener {
                        showLoading(false)
                        if (it.isSuccessful) {
                            requestState.value = Pair(true, "Berhasil menambahkan album")
                        } else {
                            requestState.value = Pair(false, "Gagal menambahkan album")
                        }
                    }
                }
            }
        }
    }

    private fun uploadImage(
        context: Context, name: String, uri: Uri, onComplete: (Boolean, String) -> Unit
    ) {
        context.createFileFromUri(name, uri)?.compressImage()?.let { file ->
            val path = "gallery/${file.nameWithoutExtension}"
            db.uploadImageToServer(path, file).addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                    onComplete(it.isSuccessful, it.result.toString())
                }
            }.addOnFailureListener {
                onComplete(false, uri.toString())

            }
        } ?: run {
            onComplete(false, uri.toString())
        }
    }
}