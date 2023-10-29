package com.jhy.project.schoollibrary.model.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.databinding.AdapterUserBinding
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.guru
import com.mikepenz.fastadapter.binding.AbstractBindingItem

data class PenggunaAdapter(
    val user: User,
    val showDetail: Boolean = true
) :
    AbstractBindingItem<AdapterUserBinding>() {

    override val type: Int
        get() = R.id.pengguna_type

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): AdapterUserBinding {
        return AdapterUserBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: AdapterUserBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.apply {
            namaTv.text = user.name
            idTv.text = when (user.role) {
                admin -> "Admin"
                guru -> "NIP: ${user.no_id}"
                else -> "NIS: ${user.no_id}"
            }
            detailBtn.isVisible = showDetail
        }
    }

}