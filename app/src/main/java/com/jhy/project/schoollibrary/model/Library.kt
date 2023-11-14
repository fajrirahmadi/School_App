package com.jhy.project.schoollibrary.model

data class Visitor(
    var key: String = "",
    var name: String = "",
    var kelas: String? = null,
    var nis: String = "",
    var gender: String = "",
    var date: Long = System.currentTimeMillis(),
    var createdBy: String = admin
)

data class VisitCount(
    var totalVisitor: Int = 0,
    var totalDaily: Map<String, Int> = emptyMap()
)