package com.jhy.project.schoollibrary.feature.school.article.add

import android.content.Context
import android.net.Uri
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.compressImage
import com.jhy.project.schoollibrary.extension.createFileFromUri
import com.jhy.project.schoollibrary.model.Article
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.constant.Result
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddArticleViewModel @Inject constructor(db: FirebaseRepository): BaseViewModel(db) {

    private var author: String = "Admin"
    fun onCreate() {
        loadUserData {
            author = it.name ?: "Admin"
        }
    }

    fun submitArticle(
        context: Context,
        uri: Uri?,
        title: String,
        description: String
    ) {
        when {
            uri == null -> updateResult(Result.Error("Anda belum memilih cover"))
            title.isEmpty() -> updateResult(Result.Error("Title tidak boleh kosong"))
            description.isEmpty() -> updateResult(Result.Error("Deskripsi tidak boleh kosong"))
            else -> {
                val id = db.generateArticleKey()
                showLoading()
                uploadImage(context, id, uri) { success, url ->
                    if (success) {
                        val article = Article(
                            id,
                            title,
                            description,
                            author,
                            imageUrl = url
                        )
                        db.submitArticle(article).addOnCompleteListener {
                            if (it.isSuccessful) {
                                updateResult(Result.Success("Berhasil menambahkan artikel/berita"))
                            } else {
                                updateResult(Result.Error("Gagal menambahkan artikel/berita"))
                            }
                            showLoading(false)
                        }
                    } else {
                        updateResult(Result.Error("Gagal membuat artikel/berita baru"))
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun uploadImage(
        context: Context,
        name: String,
        uri: Uri,
        onComplete: (Boolean, String) -> Unit
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