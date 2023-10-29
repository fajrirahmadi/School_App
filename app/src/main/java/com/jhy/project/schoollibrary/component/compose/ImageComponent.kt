package com.jhy.project.schoollibrary.component.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@Composable
fun loadImage(
    context: Context, url: String?, @DrawableRes defaultImage: Int
): MutableState<Bitmap?> {

    val bitmapState: MutableState<Bitmap?> = remember {
        mutableStateOf(null)
    }

    Glide.with(context).asBitmap().load(url ?: defaultImage).into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            bitmapState.value = resource
        }

        override fun onLoadCleared(placeholder: Drawable?) {}

    })

    return bitmapState
}

@Composable
fun ImageComponent(
    modifier: Modifier = Modifier,
    frame: Bitmap,
    scale: ContentScale = ContentScale.FillBounds
) {
    Image(
        modifier = modifier,
        bitmap = frame.asImageBitmap(),
        contentDescription = "image",
        alpha = 1f,
        contentScale = scale
    )
}