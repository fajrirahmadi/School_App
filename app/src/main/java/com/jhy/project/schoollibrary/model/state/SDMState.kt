package com.jhy.project.schoollibrary.model.state

data class SDMListState(
    var scrollState: Int = 0,
    var userListState: UIState = UIState.Loading
)