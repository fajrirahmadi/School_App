package com.jhy.project.schoollibrary.model

typealias NIS = String

enum class Presence {
    Hadir, Sakit, Izin, TanpaKeterangan
}
data class Asesmen(
    var key: String = "",
    var mapel: String = "",
    var guru: String = "",
    var kode: String = "",
    var pertemuan: String = "",
    var jurnal: String = "",
    var kelas: String = "",
    var siswa: Map<NIS, String> = emptyMap(),
    var asesmen: Map<NIS, Presence> = emptyMap(),
    var tahun: String = "",
    var semester: String = "",
    var words: List<String> = emptyList(),
    var createdDate: Long = System.currentTimeMillis()
)

data class Absence(
    var key: String = "",
    var mapel: String = "",
    var guru: String = "",
    var kode: String = "",
    var pertemuan: String = "",
    var jurnal: String = "",
    var kelas: String = "",
    var siswa: Map<NIS, String> = emptyMap(),
    var absence: Map<NIS, Presence> = emptyMap(),
    var tahun: String = "",
    var semester: String = "",
    var words: List<String> = emptyList(),
    var createdDate: Long = System.currentTimeMillis()
)