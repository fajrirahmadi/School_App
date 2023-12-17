package com.jhy.project.schoollibrary.component.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.extension.toBarCode

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
fun loadBarCode(text: String): MutableState<Bitmap?> {
    val bitmapState: MutableState<Bitmap?> = remember {
        mutableStateOf(text.toBarCode())
    }
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

@Composable
fun ImageWithShimmer(
    modifier: Modifier = Modifier,
    rounded: Dp = 12.dp,
    url: String?,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(rounded))
                .background(AppColor.blueSoft)
                .shimmerEffect()
        )
        return
    }
    loadImage(
        context = LocalContext.current,
        url = url,
        defaultImage = R.drawable.ic_logo_smp
    ).value?.let {
        ImageComponent(
            frame = it,
            modifier = modifier
                .clip(RoundedCornerShape(rounded))
        )
    } ?: run {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(rounded))
                .background(AppColor.blueSoft)
                .shimmerEffect()
        )
    }
}