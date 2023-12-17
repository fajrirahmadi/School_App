package com.jhy.project.schoollibrary.constanta

object Navigation {
    const val host = "sekolahdigital://"

    const val pinjamBuku = host + "home/library/pinjam-buku"
    const val daftarPinjam = host + "home/library/daftar-pinjam"
    const val daftarPengguna = host + "home/library/daftar-pengguna"
    const val daftarKunjungan = host + "home/library/daftar-kunjungan"

    const val bookDetail = host + "home/library/book/detail?key="
    const val addEditBook = host + "home/library/book/add-edit?title="

    const val presenceFragment = host + "activity/presence"
    const val bookPackagePage = host + "home/library/book-package"

    const val sdmDetail = host + "home/sdm/detail?key="
}