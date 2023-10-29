package com.jhy.project.schoollibrary.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingActivity<B : ViewBinding> : AppCompatActivity() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> B
    private var _baseHandler: Handler? = Handler(Looper.getMainLooper())

    @Suppress("UNCHECKED_CAST")
    protected val binding: B
        get() = _binding as B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _baseHandler?.removeCallbacksAndMessages(null)
        _baseHandler = null
    }

    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
        liveData.observe(this, Observer(body))
    }

    inline fun <T : Any, L : LiveData<T>> LifecycleOwner.observeNonNull(
        liveData: L,
        crossinline body: (T) -> Unit
    ) {
        liveData.observe(this) { it?.let(body) }
    }

    fun postDelayed(runnable: Runnable, delay: Long) {
        _baseHandler?.postDelayed(runnable, delay)
    }

    protected fun blockButton() {
        disableClick()
        _baseHandler?.postDelayed(
            {
                enableClick()
            }, 1_00L
        )
    }

    private fun disableClick() {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun enableClick() {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}