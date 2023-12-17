package com.jhy.project.schoollibrary.auth

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.createUser
import com.jhy.project.schoollibrary.repository.deleteUser
import com.jhy.project.schoollibrary.repository.loginWithEmailAndPassword
import com.jhy.project.schoollibrary.repository.sendEmailReset
import com.jhy.project.schoollibrary.repository.setUser
import com.jhy.project.schoollibrary.repository.signOut
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    db: FirebaseRepository
) : BaseViewModel(db) {

    fun doLoginByEmailAndPassword(email: String, password: String) {
        val mail = if (email.contains("@")) email else "$email@mailinator.com"
        loadingState.value = true
        viewModelScope.launch {
            try {
                val task = db.loginWithEmailAndPassword(mail, password)
                requestState.value = Pair(task?.user != null, LiveDataTag.login)
            } catch (e: Exception) {
                requestState.value = Pair(false, LiveDataTag.login)
            }
            postDelayed { showLoading(false) }
        }
    }

    fun createNewUser(email: String, name: String, password: String) {
        loadingState.value = true
        db.createUser(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = User(
                    name = name,
                    email = email
                )
                db.setUser(user).addOnCompleteListener {
                    loadingState.value = false
                    if (it.isSuccessful) {
                        db.signOut()
                        requestState.value = Pair(true, LiveDataTag.register)
                    } else {
                        db.deleteUser()
                        requestState.value = Pair(false, LiveDataTag.register)
                    }
                }
            } else {
                requestState.value = Pair(false, LiveDataTag.register)
                postDelayed {
                    loadingState.value = false
                }
            }
        }
    }

    fun sendEmailReset(context: Context, email: String) {
        loadingState.value = true
        db.sendEmailReset(email).addOnCompleteListener {
            if (it.isSuccessful) {
                showInfoDialog(
                    context,
                    "Link reset kata sandi telah dikirim ke email Anda, silakan gunakan link tersebut untuk mereset kata sandi Anda."
                ) {
                    dismissInfoDialog()
                    requestState.value = Pair(true, LiveDataTag.forgetPassword)
                }
            } else {
                requestState.value = Pair(false, LiveDataTag.forgetPassword)
            }
            loadingState.value = false
        }
    }
}