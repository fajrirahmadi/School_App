package com.jhy.project.schoollibrary.model

data class Kelas(
    var key: String = "",
    var name: String = "",
    var walas: String = "",
    var nip: String = "",
    var male: Int = 0,
    var female: Int = 0,
    var jumlahSiswa: Int = 0,
    var active: Boolean = false,
    var tahun: String = "",
    var order: Int = 0
)

fun String.toKelasText(): String {
    return this.uppercase().replace(".", " ").replace("_", " ")
}

fun String.toKelasRequest(): String {
    return this.uppercase().replace(" ", "_").replace(".", "_")
}