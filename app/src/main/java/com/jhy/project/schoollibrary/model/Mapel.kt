package com.jhy.project.schoollibrary.model

import java.io.Serializable

data class Mapel(
    var key: String = "",
    var name: String = "",
    var guru: String = "",
    var kode: String = "",
    var nip: String = "",
    var kelas: List<String> = emptyList(),
    var tahun: String = "",
    var semester: String = "",
    var created: Long = System.currentTimeMillis()
): Serializable

data class Jadwal(
    var key: String = "",
    var filter: String = "",
    var tahun: String = "",
    var day: String = "",
    var kelas: String = "",
    var mapel: String = "",
    var guru: String = "",
    var jam: Int = 0,
    var actualJam: String = "",
    var kode: String = ""
)