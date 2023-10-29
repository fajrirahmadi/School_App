package com.jhy.project.schoollibrary.model

data class Mapel(
    var key: String = "",
    var name: String = "",
    var code: String = "",
    var kelas: List<String> = emptyList(),
    var guru: String = "",
    var nip: String = "",
    var tahun: String = "",
    var created: Long = System.currentTimeMillis()
)

data class Jadwal(
    var key: String = "",
    var filter: String = "",
    var tahun: String = "",
    var day: String = "",
    var kelas: String = "",
    var mapel: String = "",
    var guru: String = "",
    var jam: Int = 0,
    var actualJam: String = ""
)