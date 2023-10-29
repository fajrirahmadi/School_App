package com.jhy.project.schoollibrary.feature.library.user

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetBebasPustakaBinding
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.siswa
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

const val cetakBottomSheet = "cetak_bottom_sheet"

class CetakBottomSheet(val user: User, var cetak: String) :
    BaseViewBindingBottomSheet<BottomsheetBebasPustakaBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            nameValueTv.text = user.name
            noTv.text = if (user.role == siswa) "NIS" else "NIP"
            nisValueTv.text = user.no_id
            kelasContainer.isVisible = user.role == siswa
            kelasValueTv.text = user.kelas?.replace(".", " ")
            cetakBtn.setOnClickListener {
                createBitmap()
            }
            signTv.text = "Painan, ${
                System.currentTimeMillis().toDateFormat("dd MMMM yyyy")
            }\n\n\n\n\n${cetak}"
        }

    }

    private fun createBitmap() {
        showLoading()
        binding.container.apply {
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

        val file = File(cachePath, "cetak_pustaka.png")
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

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetBebasPustakaBinding
        get() = BottomsheetBebasPustakaBinding::inflate
}