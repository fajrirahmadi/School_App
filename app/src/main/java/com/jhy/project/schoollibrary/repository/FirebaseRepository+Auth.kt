package com.jhy.project.schoollibrary.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.toKelasRequest
import kotlinx.coroutines.tasks.await

suspend fun FirebaseRepository.loginWithEmailAndPassword(email: String, password: String): AuthResult? {
    if (!isConnected()) return null
    return auth.signInWithEmailAndPassword(email, password).await()
}

fun FirebaseRepository.sendEmailReset(email: String): Task<Void> {
    return auth.sendPasswordResetEmail(email)
}

fun FirebaseRepository.createUser(email: String, password: String): Task<AuthResult> {
    return auth.createUserWithEmailAndPassword(email, password)
}

fun FirebaseRepository.setUser(user: User): Task<Void> {
    user.key = getUid()
    return db.collection(userDb).document(getUid()).set(user)
}

fun FirebaseRepository.updateUser(user: User, pw: String? = null, currentKey: String? = null): Task<Void> {
    val batch = db.batch()
    val userRef = db.collection(userDb).document(user.key ?: "")
    val authRef = db.collection(userDb).document(user.key ?: "").collection("account")
        .document("account01")

    batch.set(userRef, user)
    pw?.let {
        batch.set(
            authRef, mapOf(
                "email" to user.email, "password" to pw
            )
        )
    }
    currentKey?.let {
        val oldUserRef = db.collection(userDb).document(it)
        batch.delete(oldUserRef)
    }

    return batch.commit()
}

fun FirebaseRepository.loadUser(key: String? = null): DocumentReference {
    return db.collection(userDb).document(key ?: getUid())
}

fun FirebaseRepository.loadUserList(
    word: String = "", role: String = "", limit: Long = 50
): Query {
    val query = db.collection(userDb)
    return when {
        word.isNotBlank() && role.isNotBlank() -> {
            query.whereEqualTo("role", role).whereArrayContains("words", word).orderBy("name")
                .limit(limit)
        }

        role.isNotBlank() -> {
            query.whereEqualTo("role", role).orderBy("name").limit(limit)
        }

        word.isNotBlank() -> {
            query.whereArrayContains("words", word).orderBy("name").limit(limit)
        }

        else -> query.orderBy("name").limit(limit)
    }
}

fun FirebaseRepository.loadUserListByRoleAndClass(
    type: String, classParams: String = "", keyword: String = "", limit: Long = 1000
): Query {
    if (keyword.isNotBlank()) {
        if (classParams.isNotEmpty()) {
            return db.collection(userDb).whereEqualTo("role", type)
                .whereEqualTo("kelas", classParams.toKelasRequest())
                .whereArrayContains("words", keyword.lowercase()).orderBy("name").limit(limit)
        }
        return db.collection(userDb).whereEqualTo("role", type)
            .whereArrayContains("words", keyword.lowercase()).orderBy("name").limit(limit)
    }
    if (classParams.isNotBlank()) {
        return db.collection(userDb).whereEqualTo("role", type)
            .whereEqualTo("kelas", classParams.toKelasRequest())
            .orderBy("name")
            .limit(limit)
    }
    return db.collection(userDb).whereEqualTo("role", type).orderBy("name").limit(limit)
}

fun FirebaseRepository.deleteUser() {
    auth.currentUser?.delete()
}

fun FirebaseRepository.signOut() {
    auth.signOut()
}