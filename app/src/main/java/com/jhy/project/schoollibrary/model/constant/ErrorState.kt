package com.jhy.project.schoollibrary.model.constant

sealed class Result {
    data class Success(val message: String) : Result()
    data class Error(val message: String) : Result()
}