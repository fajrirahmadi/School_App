package com.jhy.project.schoollibrary.extension

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.binding.BindingViewHolder

inline fun <reified T : ViewBinding> RecyclerView.ViewHolder.asBinding(block: (T) -> View): View? {
    return if (this is BindingViewHolder<*> && this.binding is T) {
        block(this.binding as T)
    } else {
        null
    }
}