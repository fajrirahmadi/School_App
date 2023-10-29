package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterMainMenuBinding
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.model.HomeMenu
import com.mikepenz.fastadapter.binding.AbstractBindingItem

data class HomeMenuAdapter(
    val menu: HomeMenu
) :
    AbstractBindingItem<AdapterMainMenuBinding>() {

    override val type: Int
        get() = R.id.home_menu_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterMainMenuBinding {
        return AdapterMainMenuBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterMainMenuBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            coverIv.setImage(menu.url, R.drawable.placeholder_book)
            titleTv.text = menu.title
        }
    }

    override fun unbindView(binding: AdapterMainMenuBinding) {
        super.unbindView(binding)
        binding.coverIv.setImageResource(0)
    }

}