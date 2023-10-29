package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterHomeMenuBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

data class LibraryMenuAdapter(
    val menu: LibraryMenu
) :
    AbstractBindingItem<AdapterHomeMenuBinding>() {

    override val type: Int
        get() = R.id.library_menu_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterHomeMenuBinding {
        return AdapterHomeMenuBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterHomeMenuBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            menuIv.setImageResource(menu.icon)
            menuIv.clipToOutline = true
            menuTv.text = menu.title
        }
    }

    override fun unbindView(binding: AdapterHomeMenuBinding) {
        super.unbindView(binding)
        binding.menuIv.setImageResource(0)
    }

}

enum class LibraryMenu(val icon: Int, val title: String) {
    PinjamBuku(R.drawable.lib_menu_pinjam_buku, "Pinjam\nBuku"),
    DaftarPinjam(R.drawable.lib_menu_daftar_pinjam, "Daftar\nPinjam"),
    DaftarPengguna(R.drawable.lib_menu_daftar_pengguna, "Daftar\nPengguna"),
    DaftarKunjungan(R.drawable.lib_menu_daftar_kunjungan, "Daftar\nKunjungan")
}