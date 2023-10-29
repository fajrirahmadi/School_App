package com.jhy.project.schoollibrary.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import java.util.*

fun String.capitalize(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}

fun String.capitalizeWord(): String {
    var newString = ""
    this.split(" ").forEach {
        newString += "${it.lowercase().capitalize()} "
    }
    return newString
}

fun String.firstWordCapitalize(): String {
    return this.split(" ").firstOrNull()?.capitalize() ?: ""
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.length >= 8
}

fun String.isNumeric(): Boolean {
    return this.matches(Regex("^\\d*\$"))
}

fun String.copyToClipboard(context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    clipboard?.let {
        val clip = ClipData.newPlainText(this, this)
        it.setPrimaryClip(clip)
        Toast.makeText(context, "Berhasil menyalin data!", Toast.LENGTH_LONG).show()
    }
}

fun String.allowNewLine(): String {
    return this.replace("\\n", "\n")
}

fun String.generateKeyword(): MutableList<String> {
    var title = this.lowercase()
    val keyword = mutableListOf<String>()

    val words = title.split(" ")

    for (word in words) {
        var appendString = ""

        for (charPosition in title.indices) {
            appendString += title[charPosition].toString()
            keyword.add(appendString)
        }

        title = title.replaceFirst("$word ", "")
    }

    return keyword
}