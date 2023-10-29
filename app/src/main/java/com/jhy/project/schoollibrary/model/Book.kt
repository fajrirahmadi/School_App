package com.jhy.project.schoollibrary.model

import java.io.Serializable

data class Book(
    var key: String? = null,
    var judul: String? = null,
    var tahun: Int = 2022,
    var stock: Int = 0,
    var penerbit: String? = null,
    var pengarang: String? = null,
    var isbn: String? = null,
    var kategori: String = "Umum",
    var image: String? = null,
    var jumlah_halaman: Int? = 0,
    var downloadUrl: String? = null,
    var createdDate: Long = System.currentTimeMillis(),
    var dipinjam: Int = 0,
    var words: List<String> = emptyList(),
    var bookCode: String = ""
) : Serializable

fun String.toBookCategory(): BookCategory {
    return if (enumContains<BookCategory>(this)) enumValueOf(this)
    else BookCategory.Umum
}

inline fun <reified T : Enum<T>> enumContains(name: String): Boolean {
    return enumValues<T>().any { it.name == name }
}

enum class BookCategory(val title: String) {
    All("All"), Kelas7("Kelas 7"), Kelas7_1("Kelas 7/Sem 1"), Kelas7_2("Kelas 7/Sem 2"), Kelas8("Kelas 8"), Kelas8_1(
        "Kelas 8/Sem 1"
    ),
    Kelas8_2("Kelas 8/Sem 2"), Kelas9("Kelas 9"), Kelas9_1("Kelas 9/Sem 1"), Kelas9_2("Kelas 9/Sem 2"), Umum(
        "Umum"
    ),
    Novel("Novel"), NovelFiksi("Novel/Fiksi"), Sejarah("Sejarah"), Agama("Agama")
}

const val dipinjam = "dipinjam"
const val selesai = "selesai"

data class PinjamBuku(
    var key: String? = null,
    var uid: String? = null,
    var bid: String? = null,
    var date: Long = System.currentTimeMillis(),
    var status: String = dipinjam,
    var name: String? = null,
    var judul: String? = null,
    var url: String? = null,
    var ukey: String? = null,
    var bookCode: String = "",
    var returnDate: Long = System.currentTimeMillis(),
    var actualReturnDate: Long = System.currentTimeMillis(),
    var words: List<String> = emptyList(),
    var denda: Long = 0
)

data class BookCounter(
    var count: Int = 0, var lastUpdate: Long = 0
)