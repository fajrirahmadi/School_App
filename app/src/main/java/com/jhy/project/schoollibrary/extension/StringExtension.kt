package com.jhy.project.schoollibrary.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import java.text.SimpleDateFormat
import java.util.Locale

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

fun String.toMillis(format: String = "yyyy-MM-dd"): Long {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val date = sdf.parse(this)
    return date?.time ?: 0L
}

fun String.timeInMillis(): Long {
    val hour = this.split(":")[0].toInt()
    val minute = this.split(":")[1].toInt()
    return (hour * 60 + minute) * 60000L
}

fun String.toBarCode(width: Int = 600, height: Int = 100): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val codeWriter = MultiFormatWriter()
    try {
        val bitMatrix = codeWriter.encode(
            this, BarcodeFormat.CODE_128, width, height
        )
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                bitmap.setPixel(x, y, color)
            }
        }
    } catch (e: WriterException) {
        Log.d("TAG", "generateBarCode: ${e.message}")
    }
    return bitmap
}