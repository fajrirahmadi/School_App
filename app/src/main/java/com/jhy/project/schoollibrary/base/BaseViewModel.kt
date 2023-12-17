package com.jhy.project.schoollibrary.base

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.component.InfoDialog
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.constant.Result
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.loadUser
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel(
    val db: FirebaseRepository,
) : ViewModel() {

    val isLogin: Boolean
        get() = db.isLogin()

    private val baseHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val infoDialog by lazy {
        InfoDialog()
    }

    val userState = MutableLiveData<User?>()
    val loadingState = MutableLiveData<Boolean>()
    val requestState = MutableLiveData<Pair<Boolean, String>>()

    private val _resultState = MutableStateFlow<Result?>(null)
    val resultState = _resultState.asStateFlow()

    private val _errorState = MutableStateFlow<Result?>(null)
    val errorState = _errorState.asStateFlow()

    var userJob: Job? = null
    fun loadUserData(
        key: String? = null,
        completion: ((User) -> Unit)? = null
    ) {
        userJob?.cancel()
        userJob = viewModelScope.launch {
            observeStatefulDoc<User>(db.loadUser(key)).collect {
                if (it is FirestoreState.Success) {
                    it.data?.let { user ->
                        userState.value = user
                        completion?.invoke(user)
                    }
                }
            }
        }
    }

    fun postDelayed(delay: Long = 1000, action: () -> Unit) {
        baseHandler.postDelayed({ action.invoke() }, delay)
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

    fun updateResult(result: Result) {
        _resultState.value = result
    }

}