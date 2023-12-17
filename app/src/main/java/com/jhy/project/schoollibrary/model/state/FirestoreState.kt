package com.jhy.project.schoollibrary.model.state

sealed class FirestoreState<T> {

    class Loading<T>: FirestoreState<T>()
    data class Success<T>(val data: T): FirestoreState<T>()
    data class Failed<T>(val message: String?): FirestoreState<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> failed(message: String?)  = Failed<T>(message)
    }
}