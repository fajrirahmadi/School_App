package com.jhy.project.schoollibrary.model

data class SchoolProfileModel(
    var name: String = "",
    var image: String = "",
    var items: List<SchoolProfile> = emptyList()
)

data class SchoolProfile(
    var title: String = "", var content: String = "", var key: String = ""
)

data class SchoolPrestasiModel(
    var name: String = "",
    var image: String = "",
    var items: List<SchoolPrestasi> = emptyList()
)

data class SchoolPrestasi(
    var key: String = "",
    var year: String = "",
    var prestasi: String = ""
)

data class SchoolFacilityModel(
    var name: String = "",
    var image: String = "",
    var items: List<SchoolFacility> = emptyList()
)

data class SchoolFacility(
    var name: String = "",
    var count: Int = 0
)

data class SchoolOrganisasiModel(
    var name: String = "",
    var image: String = "",
    var items: List<SchoolOrganisasi> = emptyList()
)

data class SchoolOrganisasi(
    var name: String = "",
    var jabatan: String = ""
)

data class SchoolExtraModel(
    var name: String = "",
    var image: String = "",
    var items: List<SchoolExtra> = emptyList()
)

data class SchoolExtra(
    var name: String = "",
    var target: String = "",
    var tujuan: String = "",
    var lingkup: String = "",
    var penanggungJawab: String = "",
    var waktu: String = ""
)