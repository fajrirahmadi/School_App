package com.jhy.project.schoollibrary.model

const val typeDeeplink = "deeplink"
const val typeWeb = "web"

data class HomeModel(
    var key: String = "",
    var order: Int = 0,
    var items: List<HomeMenu> = emptyList()
)

data class HomeMenu(
    var key: String = "",
    var title: String = "",
    var description: String = "",
    var url: String = "",
    var direction: String = "",
    var type: String = "",
    var createdDate: Long = System.currentTimeMillis(),
    var order: Int = 0
)