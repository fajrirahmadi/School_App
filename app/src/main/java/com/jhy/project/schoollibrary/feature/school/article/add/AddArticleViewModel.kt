package com.jhy.project.schoollibrary.feature.school.article.add

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddArticleViewModel @Inject constructor(db: FirebaseRepository): BaseViewModel(db) {

    fun submitArticle() {

    }
}