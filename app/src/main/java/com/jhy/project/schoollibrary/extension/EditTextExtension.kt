package com.jhy.project.schoollibrary.extension

import android.widget.EditText

fun EditText.sameAs(otherEditText: EditText): Boolean {
    return this.string == otherEditText.string
}