package com.jhy.project.schoollibrary.extension

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.select.getSelectExtension

fun RecyclerView.initVerticalAdapter(
    context: Context?,
    adapter: FastAdapter<*>
) {
    this.layoutManager = LinearLayoutManager(context)
    this.adapter = adapter
    this.isNestedScrollingEnabled = false
    this.isFocusable = false
    this.itemAnimator = null
    val selection = adapter.getSelectExtension()
    if (!selection.isSelectable) selection.isSelectable = true
}

fun RecyclerView.initHorizontalAdapter(
    context: Context?,
    adapter: FastAdapter<*>
) {
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    this.adapter = adapter
    this.isNestedScrollingEnabled = false
    this.isFocusable = false
    this.itemAnimator = null
    val selection = adapter.getSelectExtension()
    if (!selection.isSelectable) selection.isSelectable = true
}

fun RecyclerView.initGridAdapter(
    context: Context?,
    adapter: FastAdapter<*>,
    span: Int
) {
    this.layoutManager = GridLayoutManager(context, span)
    this.adapter = adapter
    this.isNestedScrollingEnabled = false
    this.isFocusable = false
    this.itemAnimator = null
    val selection = adapter.getSelectExtension()
    if (!selection.isSelectable) selection.isSelectable = true
}