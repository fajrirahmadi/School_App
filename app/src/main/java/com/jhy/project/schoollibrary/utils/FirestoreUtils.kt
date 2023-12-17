package com.jhy.project.schoollibrary.utils

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.jhy.project.schoollibrary.model.state.FirestoreState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

inline fun <reified T> getStatefulDoc(docRef: DocumentReference) = flow {
    emit(FirestoreState.loading())

    val task = docRef.get()
    val result = kotlin.runCatching { Tasks.await(task) } .getOrNull()
    val exception = task.exception

    if (exception != null) {
        emit(FirestoreState.failed(exception.message)) as T
    } else {
        val data = result?.toObject(T::class.java)
        emit(FirestoreState.success(data))
    }
}


inline fun <reified T> getStatefulCollection(colRef: Query) = flow {
    emit(FirestoreState.loading())

    val task = colRef.get()
    val result = kotlin.runCatching { Tasks.await(task) } .getOrNull()

    val exception = task.exception

    if (exception != null) {
        emit(FirestoreState.failed(exception.message)) as T
    } else {
        val data = result?.documents?.map {
            it.toObject(T::class.java)
        }
        emit(FirestoreState.success(data))
    }
}

inline fun <reified  T>observeStatefulDoc(docRef: DocumentReference):
        Flow<FirestoreState<T?>> = callbackFlow {

    trySend(FirestoreState.loading())

    val subscription = docRef.addSnapshotListener { snapshot, error ->
        if(error != null) {
            trySend( FirestoreState.failed(error.message))
            return@addSnapshotListener
        }
        snapshot?.exists()?.let {
            val obj = snapshot.toObject(T::class.java)
            trySend(FirestoreState.success(obj))
        }
    }

    awaitClose { subscription.remove() }
}

inline fun <reified T> observeStatefulCollection(colRef: Query):
        Flow<FirestoreState<List<T?>>> = callbackFlow {

    trySend(FirestoreState.loading())

    val subscription = colRef.addSnapshotListener { query, error ->

        if (error != null) {
            trySend( FirestoreState.failed(error.message))
            return@addSnapshotListener
        }

        query?.documents?.let {
            if(it.isEmpty()) {
                trySend( FirestoreState.failed("Collection is empty"))
                return@addSnapshotListener
            } else {
                val docs = it.map {
                    it.toObject(T::class.java)
                }
                trySend(FirestoreState.success(docs))
            }
        }
    }
    awaitClose { subscription.remove() }
}