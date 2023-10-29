package com.jhy.project.schoollibrary.feature.library.pinjam

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.roundUpToNearestThousand
import com.jhy.project.schoollibrary.extension.toIDRFormat
import com.jhy.project.schoollibrary.model.PinjamBuku
import com.jhy.project.schoollibrary.model.adapter.FilterPinjamAdapter
import com.jhy.project.schoollibrary.model.adapter.PinjamBukuAdapter
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.dipinjam
import com.jhy.project.schoollibrary.model.selesai
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PinjamViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var keyword = ""
    var status = "all"
    var userKey: String? = null
    var bookKey: String? = null

    private val pinjamList = mutableListOf<PinjamBuku>()
    private val _adapter = ItemAdapter<PinjamBukuAdapter>()
    val adapter by lazy {
        FastAdapter.with(_adapter)
    }

    private val _filterAdapter = ItemAdapter<FilterPinjamAdapter>()
    val filterAdapter by lazy {
        FastAdapter.with(_filterAdapter)
    }

    fun onCreate(userKey: String? = null, bookKey: String? = null) {
        this.userKey = userKey
        this.bookKey = bookKey
        initFilter()
        loadPinjam()
    }

    private fun loadPinjam() {
        showLoading()
        db.loadPinjamBukuList(userKey, bookKey, keyword).addOnCompleteListener {
            if (it.isSuccessful) {
                pinjamList.clear()
                pinjamList.addAll(it.result.toObjects(PinjamBuku::class.java))
                showPinjamAdapter(keyword)
            } else {
                dismissLoading()
            }
        }
    }

    private fun initFilter() {
        if (_filterAdapter.adapterItemCount > 0) return
        _filterAdapter.add(FilterPinjamAdapter("all", status == "all"))
        _filterAdapter.add(FilterPinjamAdapter(dipinjam, status == dipinjam))
        _filterAdapter.add(FilterPinjamAdapter(selesai, status == selesai))

        filterAdapter.notifyAdapterDataSetChanged()
        filterAdapter.onClickListener = { _, _, data, position ->
            for ((index, data) in _filterAdapter.adapterItems.withIndex()) {
                if (data.status == status) {
                    _filterAdapter.getAdapterItem(index).choose = false
                    break
                }
            }
            _filterAdapter.getAdapterItem(position).choose = true
            filterAdapter.notifyAdapterDataSetChanged()

            status = data.status
            showPinjamAdapter(keyword)
            true
        }
    }

    private fun showPinjamAdapter(keyword: String = "") {
        this.keyword = keyword
        _adapter.clear()
        for (data in pinjamList) {
            if ((data.judul?.contains(keyword, true) == true
                        || data.name?.contains(keyword, true) == true
                        || data.uid?.contains(keyword, true) == true
                        ) && (data.status == status || status == "all")
            ) {
                val isLate = data.status == dipinjam && System.currentTimeMillis() > data.returnDate
                val denda = (System.currentTimeMillis() - data.returnDate) / (24 * 3600)
                val dendaText =
                    if (isLate) "Denda: ${denda.roundUpToNearestThousand().toIDRFormat()}"
                    else ""
                val pbAdapter = PinjamBukuAdapter(data, dendaText)
                _adapter.add(pbAdapter)
            }
        }
        adapter.notifyAdapterDataSetChanged()
        postDelayed { dismissLoading() }
    }

    fun markSelesai(data: PinjamBuku) {
        showLoading()
        db.selesaikanPinjamBuku(data).addOnCompleteListener {
            postRequest(it.isSuccessful, LiveDataTag.selesaiPinjamBuku)
            postDelayed { dismissLoading() }
        }
    }

    fun doSearch(keyword: String = "") {
        if (userKey != null || bookKey != null) {
            showPinjamAdapter(keyword)
            return
        }
        if (this.keyword == keyword) return
        this.keyword = keyword
        loadPinjam()
    }
}