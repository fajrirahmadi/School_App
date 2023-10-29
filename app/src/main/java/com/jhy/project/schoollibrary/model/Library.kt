package com.jhy.project.schoollibrary.model

data class Kunjungan(
    var key: String = "",
    var name: String = "",
    var nis: String = "",
    var date: Long = System.currentTimeMillis(),
    var createdBy: String = admin
)