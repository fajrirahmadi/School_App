package com.jhy.project.schoollibrary.model

import java.io.Serializable

const val user = "user"
const val admin = "admin"
const val guru = "guru"
const val siswa = "siswa"
const val pria = "pria"
const val wanita = "wanita"
const val alumni = "alumni"
const val pensiun = "pensiun"

data class User(
    var key: String? = null,
    var name: String? = null,
    var email: String? = null,
    var bio: String? = null,
    var role: String = user,
    var createdDate: Long = System.currentTimeMillis(),
    var no_id: String? = null,
    var url: String? = null,
    var active: Boolean = true,
    var gender: String = pria,
    var pinjam: Boolean = false,
    var kelas: String? = null,
    var alamat: String? = null,
    var tanggalLahir: String? = null,
    var tempatLahir: String? = null,
    var agama: String? = null,
    var wali: String? = null,
    var ponsel: String? = null,
    var job: String? = null,
    var mapel: String? = null,
    var golongan: String? = null,
    var status: String? = null,
    var kode: String? = null,
    var words: List<String> = emptyList()
) : Serializable

fun String.toKelasCategory(): KelasCategory {
    for (data in KelasCategory.values()) {
        if (this.equals(data.value, true))
            return data
    }
    return KelasCategory.IX_1
}

enum class KelasCategory(val value: String) {
    ALL("All"),
    VII_1("VII.1"),
    VII_2("VII.2"),
    VII_3("VII.3"),
    VII_4("VII.4"),
    VII_5("VII.5"),
    VII_6("VII.6"),
    VII_7("VII.7"),
    VII_8("VII.8"),
    VIII_1("VIII.1"),
    VIII_2("VIII.2"),
    VIII_3("VIII.3"),
    VIII_4("VIII.4"),
    VIII_5("VIII.5"),
    VIII_6("VIII.6"),
    VIII_7("VIII.7"),
    IX_1("IX.1"),
    IX_2("IX.2"),
    IX_3("IX.3"),
    IX_4("IX.4"),
    IX_5("IX.5"),
    IX_6("IX.6"),
    IX_7("IX.7"),

}