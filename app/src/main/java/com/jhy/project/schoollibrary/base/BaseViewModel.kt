package com.jhy.project.schoollibrary.base

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jhy.project.schoollibrary.component.InfoDialog
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.repository.FirebaseRepository

open class BaseViewModel(
    val db: FirebaseRepository,
) : ViewModel() {

    val isLogin: Boolean
        get() = db.isLogin()
    var isNavigate: Boolean = false

    private val baseHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val infoDialog by lazy {
        InfoDialog()
    }

    val userState = MutableLiveData<User?>()
    val loadingState = MutableLiveData<Boolean>()
    val requestState = MutableLiveData<Pair<Boolean, String>>()

    fun loadUserData() {
        db.loadUser().addOnCompleteListener {
            if (it.isSuccessful) {
                userState.value = it.result?.toObject(User::class.java)
            }
        }
    }

    fun postDelayed(action: () -> Unit) {
        baseHandler.postDelayed({ action.invoke() }, 4_00L)
    }

    fun showInfoDialog(context: Context, description: String, action: (() -> Unit)? = null) {
        infoDialog.show(context, "Info", description, action)
    }

    fun dismissInfoDialog() {
        infoDialog.dismiss()
    }

    fun showLoading(isShow: Boolean = true) {
        loadingState.value = isShow
    }

    fun dismissLoading() {
        loadingState.value = false
    }

    fun postRequest(success: Boolean, tag: String) {
        requestState.value = Pair(success, tag)
    }

}