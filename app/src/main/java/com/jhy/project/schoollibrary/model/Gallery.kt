package com.jhy.project.schoollibrary.model

data class Gallery(
    var key: String = "",
    var name: String = "",
    var date: Long = System.currentTimeMillis(),
    var items: List<String> = emptyList(),
    var year: String = ""
)