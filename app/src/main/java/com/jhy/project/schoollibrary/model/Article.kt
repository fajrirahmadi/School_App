package com.jhy.project.schoollibrary.model

import java.io.Serializable

data class Article(
    var key: String = "",
    var title: String = "",
    var content: String = "",
    var author: String = "",
    var postDate: Long = System.currentTimeMillis(),
    var visitor: Int = 0,
    var imageUrl: String = ""
): Serializable