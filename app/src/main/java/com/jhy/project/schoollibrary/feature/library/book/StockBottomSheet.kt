package com.jhy.project.schoollibrary.feature.library.book

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.loadBarCode
import com.jhy.project.schoollibrary.component.compose.shimmerEffect
import com.jhy.project.schoollibrary.databinding.BottomsheetComposeBinding
import com.jhy.project.schoollibrary.model.Book
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class StockBottomSheet(
    private val book: Book
) : BaseViewBindingBottomSheet<BottomsheetComposeBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetComposeBinding
        get() = BottomsheetComposeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            MaterialTheme {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(8.dp)
                        .background(AppColor.white)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            createBitmap()
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalItemSpacing = 8.dp
                ) {
                    items(book.stock) {
                        val code = "BI-${book.isbn}-${
                            String.format(
                                "%03d", it + 1
                            )
                        }-KM"
                        loadBarCode(
                            text = code
                        ).value?.let {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ImageComponent(
                                    frame = it, modifier = Modifier
                                        .width(300.dp)
                                        .height(50.dp)
                                )
                                WorkSandTextNormal(text = code, size = 10.sp)
                            }

                        } ?: run {
                            Box(
                                modifier = Modifier
                                    .width(300.dp)
                                    .height(50.dp)
                                    .background(AppColor.blueSoft)
                                    .shimmerEffect()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createBitmap() {
        showLoading()
        binding.composeView.apply {
            postDelayed({
                val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                this.draw(canvas)
                shareImage(bitmap)
            }, 1_00L)
        }
    }

    private fun shareImage(result: Bitmap?) {
        if (result == null) return
        val cachePath = File(requireActivity().externalCacheDir, "my_images/")
        cachePath.mkdirs()

        val file = File(cachePath, "kartu-anggota.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            result.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val myImageFileUri: Uri = FileProvider.getUriForFile(
            requireContext(), requireContext().packageName + ".provider", file
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
        intent.type = "image/png"
        dismissLoading()
        startActivity(Intent.createChooser(intent, "Share with"))
    }

    override fun isDraggable(): Boolean {
        return false
    }
}