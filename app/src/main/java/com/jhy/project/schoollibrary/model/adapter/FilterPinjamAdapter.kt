package com.jhy.project.schoollibrary.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterFilterBinding
import com.jhy.project.schoollibrary.extension.capitalize
import com.mikepenz.fastadapter.binding.AbstractBindingItem

class FilterPinjamAdapter(
    val status: String,
    var choose: Boolean = false
) :
    AbstractBindingItem<AdapterFilterBinding>() {

    override val type: Int
        get() = R.id.filter_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterFilterBinding {
        return AdapterFilterBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: AdapterFilterBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.valueTV.apply {
            text = status.capitalize()
            isEnabled = !choose
        }
        binding.cardContainer.apply {
            setCardBackgroundColor(
                ResourcesCompat.getColor(
                    binding.root.resources,
                    if (choose) R.color.primary_color else R.color.white,
                    null
                )
            )
        }
    }

}