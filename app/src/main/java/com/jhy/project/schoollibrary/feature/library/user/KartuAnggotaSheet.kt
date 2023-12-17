package com.jhy.project.schoollibrary.feature.library.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.component.compose.AppColor
import com.jhy.project.schoollibrary.component.compose.HorizontalSpace
import com.jhy.project.schoollibrary.component.compose.ImageComponent
import com.jhy.project.schoollibrary.component.compose.VerticalSpace
import com.jhy.project.schoollibrary.component.compose.WorkSandTextMedium
import com.jhy.project.schoollibrary.component.compose.WorkSandTextNormal
import com.jhy.project.schoollibrary.component.compose.loadImage
import com.jhy.project.schoollibrary.component.compose.shimmerEffect
import com.jhy.project.schoollibrary.databinding.BottomsheetComposeBinding
import com.jhy.project.schoollibrary.extension.toBarCode
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.guru
import com.jhy.project.schoollibrary.model.pria
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class KartuAnggotaSheet(
    private val user: User
) : BaseViewBindingBottomSheet<BottomsheetComposeBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetComposeBinding
        get() = BottomsheetComposeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.composeView.setContent {
            MaterialTheme {
                KartuAnggota(user = user) {
                    createBitmap()
                }
            }
        }
    }

    private fun createBitmap() {
        showLoading()
        binding.composeView.apply {
            postDelayed({
                val bitmap =
                    Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
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
}


@Composable
fun KartuAnggota(
    modifier: Modifier = Modifier,
    user: User,
    onClick: () -> Unit
) {
    val noId = (if (user.role == guru) user.nik else user.no_id) ?: " - "
    val url = user.url

    val bitmap = remember { mutableStateOf<Bitmap?>(noId.toBarCode()) }
    val items = listOf(
        Pair("Nama", user.name ?: "Nama"),
        Pair(if (user.role == guru) "NIK" else "NIS", noId),
        Pair("Gender", if (user.gender == pria) "Laki-laki" else "Perempuan"),
        Pair("T.T.L", "${user.tempatLahir?.trim()}, ${user.tanggalLahir}"),
        Pair("Alamat", user.alamat ?: " - ")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp, 30.dp)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(250.dp)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.background_kartu_anggota),
                contentDescription = null
            )

            Column {
                VerticalSpace(height = 80.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalSpace(width = 32.dp)
                    loadImage(
                        context = LocalContext.current,
                        url = url,
                        defaultImage = R.drawable.ic_logo_smp
                    ).value?.let {
                        ImageComponent(
                            frame = it,
                            modifier = Modifier
                                .width(54.dp)
                                .height(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AppColor.blueSoft)
                                .shimmerEffect()
                        )
                    }
                    HorizontalSpace(width = 24.dp)
                    Column {
                        items.forEach {
                            Row {
                                WorkSandTextNormal(
                                    modifier = Modifier.width(40.dp), text = it.first, size = 8.sp
                                )
                                WorkSandTextNormal(text = " : ", size = 8.sp)
                                WorkSandTextMedium(text = it.second, size = 8.sp)
                            }
                        }
                    }
                }
                VerticalSpace(height = 8.dp)
                Row {
                    HorizontalSpace(width = 32.dp)
                    bitmap.value?.asImageBitmap()?.let {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            VerticalSpace(height = 16.dp)
                            Image(
                                bitmap = it,
                                contentDescription = "Generate BarCode Image",
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(25.dp)
                            )
                            WorkSandTextMedium(text = noId, size = 8.sp)
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WorkSandTextMedium(
                            text = "Painan, ${
                                System.currentTimeMillis().toDateFormat("dd MMMM yyyy")
                            }", size = 8.sp
                        )
                        WorkSandTextNormal(text = "Kepala Sekolah", size = 8.sp)
                        Box(
                            modifier = Modifier
                                .height(20.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ttd_kepala_sekolah),
                                contentDescription = "ttd_kepsek"
                            )
                            Image(
                                painter = painterResource(id = R.drawable.stempel_smp_1_painan),
                                contentDescription = "stempel"
                            )
                        }
                        WorkSandTextMedium(
                            text = "Linda Astuti, S.Pd.\nNIP. 1973005311999032005", size = 8.sp
                        )
                    }
                }
            }
        }
    }
}