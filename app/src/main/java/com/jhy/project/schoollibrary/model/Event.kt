package com.jhy.project.schoollibrary.model

data class Event(
    var key: String = "",
    var name: String = "",
    var agenda: String = "",
    var place: String = "",
    var startDate: Long = System.currentTimeMillis(),
    var endDate: Long = System.currentTimeMillis(),
    var user: String = "",
    var noId: String = "",
    var attachments: List<String> = emptyList(),
    var filters: List<String> = emptyList()
)