package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterPinjamBinding
import com.jhy.project.schoollibrary.extension.capitalize
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.PinjamBuku
import com.mikepenz.fastadapter.binding.AbstractBindingItem

data class PinjamBukuAdapter(
    val data: PinjamBuku,
    val denda: String = ""
) :
    AbstractBindingItem<AdapterPinjamBinding>() {

    override val type: Int
        get() = R.id.pinjam_buku_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterPinjamBinding {
        return AdapterPinjamBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterPinjamBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            coverIv.setImage(data.url)
            judulTv.text = data.judul
            codeTv.text = "Kode buku: ${data.bookCode}"
            peminjamTv.text = "Dipinjam oleh: ${data.name}"
            dateTv.text = "Tgl. pinjam   : ${data.date.toDateFormat("dd-MM-yyyy, HH:mm")}"
            returnDateTv.text =
                "Tgl. kembali : ${data.actualReturnDate.toDateFormat("dd-MM-yyyy, HH:mm")}"
            statusTv.text = data.status.capitalize()
            dendaTv.apply {
                isVisible = denda.isNotEmpty()
                text = denda
            }
        }
    }

    override fun unbindView(binding: AdapterPinjamBinding) {
        super.unbindView(binding)
        binding.coverIv.setImageResource(0)
    }

}