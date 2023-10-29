package com.jhy.project.schoollibrary.extension

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.R

fun View.setAnim(anim: Int): Animation {
    return AnimationUtils.loadAnimation(context, anim)
}

fun View.fadeIn() {
    this.isVisible = true
    val anim = this.rootView.setAnim(R.anim.fade_in)
    this.animation = anim
}

fun View.slideToBottom() {
    if (translationY == 0f) {
        val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        animate().translationY(height.toFloat() + bottomMargin)
    }
}

fun View.slideFromBottom() {
    if (translationY > 0f) animate().translationY(0f)
}

fun Animation.onAnimationEnd(onAnimationEnd: (Animation) -> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation) {}
        override fun onAnimationEnd(p0: Animation) {
            onAnimationEnd.invoke(p0)
        }

        override fun onAnimationRepeat(p0: Animation) {}
    })
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

val TextView.trim: String
    get() = text.toString().trim()

val TextView.string: String
    get() = text.toString()

val TextView.count: Int
    get() = text.toString().length

val TextView.int: Int
    get() = text.toString().toIntOrNull() ?: 0

fun TextView.setDrawableStart(drawable: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
}

fun Context.convertPxToDp(px: Float): Float {
    return px / this.resources.displayMetrics.density
}