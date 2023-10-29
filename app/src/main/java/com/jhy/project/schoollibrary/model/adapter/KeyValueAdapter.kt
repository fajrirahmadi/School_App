package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterKeyValueBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

const val vertical = "vertical"
const val horizontal = "horizontal"

data class KeyValueAdapter(
    val key: String,
    val value: String?,
    var orientation: String = horizontal
) :
    AbstractBindingItem<AdapterKeyValueBinding>() {

    override val type: Int
        get() = R.id.key_value_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterKeyValueBinding {
        return AdapterKeyValueBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterKeyValueBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            horizontalContainer.isVisible = orientation == horizontal
            verticalContainer.isVisible = orientation == vertical
            if (orientation == horizontal) {
                horizontalKeyTv.text = key
                horizontalValueTv.text = value
                horizontalValueTv.isVisible = !value.isNullOrEmpty()
            } else {
                verticalKeyTv.text = key
                verticalValueTv.text = value
                verticalValueTv.isVisible = !value.isNullOrEmpty()

            }
        }
    }

}