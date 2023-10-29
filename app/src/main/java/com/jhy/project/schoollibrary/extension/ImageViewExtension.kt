package com.jhy.project.schoollibrary.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.google.firebase.storage.FirebaseStorage
import org.apache.commons.io.FileUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun ImageView.setImage(
    url: String?,
    placeholderResId: Int? = null,
    errorResId: Int? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    if (url.isNullOrEmpty()) {
        this.apply {
            setImageResource(placeholderResId ?: 0)
            clipToOutline = true
        }
    } else {
        this.apply {
            Glide.with(this)
                .load(url)
                .optionalCenterCrop()
                .transform(CenterCrop())
                .apply {
                    placeholderResId?.let { placeholder(it) }
                    errorResId?.let { kotlin.error(it) }
                    requestListener?.let { listener(it) }
                }
                .into(this)
        }
    }
}

fun ImageView.setImageNoCrop(
    url: String?,
    placeholderResId: Int? = null,
    errorResId: Int? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    if (url.isNullOrEmpty()) {
        this.apply {
            setImageResource(placeholderResId ?: 0)
            clipToOutline = true
        }
    } else {
        this.apply {
            Glide.with(this)
                .load(url)
                .apply {
                    placeholderResId?.let { placeholder(it) }
                    errorResId?.let { kotlin.error(it) }
                    requestListener?.let { listener(it) }
                }
                .into(this)
        }
    }
}

fun ImageView.setFirebaseImage(
    url: String?,
    placeholderResId: Int? = null,
    errorResId: Int? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    if (url.isNullOrEmpty()) {
        this.setImageResource(placeholderResId ?: 0)
        this.clipToOutline = true
    } else {
        FirebaseStorage.getInstance().reference.child(url).downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result.toString())
                    .optionalCenterCrop()
                    .transform(CenterCrop())
                    .apply {
                        placeholderResId?.let { placeholder(it) }
                        errorResId?.let { kotlin.error(it) }
                        requestListener?.let { listener(it) }
                    }
                    .into(this)
                this.clipToOutline = true
            }
        }
    }
}

fun File.compressImage(destWidth: Int = 1080): File {
    val b = BitmapFactory.decodeFile(this.absolutePath)

    val origWidth = b.width
    val origHeight = b.height

    if (origWidth > destWidth) {
        // picture is wider than we want it, we calculate its target height
        val destHeight = origHeight / (origWidth / destWidth.toFloat())
        // we create an scaled bitmap so it reduces the image, not just trim it
        val b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight.toInt(), false)
        val outStream = ByteArrayOutputStream()
        // compress to the format you want, JPEG, PNG...
        // 70 is the 0-100 quality percentage
        b2.compress(Bitmap.CompressFormat.PNG, 50, outStream)
        // we save the file, at least until we have made use of it
        val fileName = this.absolutePath.replace(
            this.name,
            "${this.nameWithoutExtension}-compressed.${this.extension}"
        )
        val f = File(fileName)
        f.createNewFile()
        //write the bytes in file
        val fo = FileOutputStream(f)
        fo.write(outStream.toByteArray())
        // remember close de FileOutput
        fo.close()

        b.recycle()
        b2.recycle()
        return f
    }

    b.recycle()
    return this
}

fun Context.createFileFromUri(name: String, uri: Uri): File? {
    return try {
        val stream = this.contentResolver?.openInputStream(uri)
        val file =
            File.createTempFile(
                "${name}_${System.currentTimeMillis()}",
                ".png",
                this.cacheDir
            )
        FileUtils.copyInputStreamToFile(
            stream,
            file
        )  // Use this one import org.apache.commons.io.FileUtils
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}