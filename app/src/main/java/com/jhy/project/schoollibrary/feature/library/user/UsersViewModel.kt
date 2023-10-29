package com.jhy.project.schoollibrary.feature.library.user

import androidx.lifecycle.MutableLiveData
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.constanta.RemoteConfigHelper
import com.jhy.project.schoollibrary.model.*
import com.jhy.project.schoollibrary.model.adapter.FilterCustomAdapter
import com.jhy.project.schoollibrary.model.adapter.PenggunaAdapter
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    db: FirebaseRepository, private val config: RemoteConfigHelper
) : BaseViewModel(db) {

    val kepalaPustaka: String
        get() {
            return config.kepalaPustaka
        }

    var selectedTab = 0
    var keyword = ""

    private var selectedFilter = 0
    private var showDetail = true
    var adaPinjaman = true

    val tabState = MutableLiveData<Int>()
    private val userList = mutableListOf<User>()
    private val _userAdapter = ItemAdapter<PenggunaAdapter>()
    val userAdapter by lazy {
        FastAdapter.with(_userAdapter)
    }

    private val _kelasFilter = ItemAdapter<FilterCustomAdapter>()
    val kelasFilter by lazy {
        FastAdapter.with(_kelasFilter)
    }

    fun initKelasFilter(online: Boolean = false) {
        if (_kelasFilter.adapterItemCount > 0) return
        db.getKelas(online).addOnCompleteListener {
            if (it.isSuccessful) {
                val list = it.result.toObjects(Kelas::class.java)
                if (list.isEmpty()) {
                    initKelasFilter(true)
                    return@addOnCompleteListener
                }
                for ((index, data) in list.withIndex()) {
                    _kelasFilter.add(
                        FilterCustomAdapter(
                            data.name.uppercase(), index == selectedFilter
                        )
                    )
                }
                kelasFilter.notifyAdapterDataSetChanged()
                kelasFilter.onClickListener = { _, _, _, position ->
                    if (selectedFilter != position) {
                        _kelasFilter.getAdapterItem(selectedFilter).choose = false
                        _kelasFilter.getAdapterItem(position).choose = true
                        selectedFilter = position
                        kelasFilter.notifyAdapterDataSetChanged()
                        loadUserListByRole()
                    }
                    true
                }
            }
        }
    }

    fun changeTab(tab: Int) {
        this.selectedTab = tab
        loadUserListByRole()
        tabState.value = tab
    }

    private fun loadUserListByRole() {
        showLoading()
        val kelas = if (_kelasFilter.adapterItemCount > selectedFilter) _kelasFilter.getAdapterItem(
            selectedFilter
        ).filter.uppercase()
        else "VII.1"
        db.loadUserListByRoleAndClass(
            if (selectedTab == 0) guru else siswa, if (selectedTab == 0) "" else kelas
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                userList.clear()
                userList.addAll(it.result.toObjects(User::class.java))
                showUserAdapter(keyword)
            } else {
                dismissLoading()
            }
        }
    }

    fun loadUserList() {
        showDetail = false
        showLoading()
        db.loadUserList(keyword).addOnCompleteListener {
            if (it.isSuccessful) {
                userList.clear()
                userList.addAll(
                    it.result.toObjects(User::class.java).filter { user -> user.role != admin })
                showUserAdapter(true)
            } else {
                dismissLoading()
            }
        }
    }

    fun showUserAdapter(keyword: String = "") {
        this.keyword = keyword
        _userAdapter.clear()
        for (data in userList) {
            if (data.name?.contains(keyword, true) == true || data.no_id?.contains(
                    keyword, true
                ) == true
            ) {
                _userAdapter.add(PenggunaAdapter(data, showDetail))
            }
        }
        userAdapter.notifyAdapterDataSetChanged()
        postDelayed { dismissLoading() }
    }

    fun doSearch(keyword: String = "") {
        if (keyword == this.keyword) return
        this.keyword = keyword
        loadUserList()
    }

    fun showUserAdapter(firstPage: Boolean = true) {
        if (firstPage) {
            _userAdapter.clear()
        }
        val startIndex = _userAdapter.adapterItemCount
        val endIndex = _userAdapter.adapterItemCount + 20

        for ((index, data) in userList.filterIndexed { index, _ -> index >= startIndex }
            .withIndex()) {
            _userAdapter.add(PenggunaAdapter(data, showDetail))
            if (index == endIndex) break
        }
        userAdapter.notifyAdapterDataSetChanged()
        postDelayed { dismissLoading() }
    }

    fun submitUser(user: User) {
        showLoading()
        user.key?.let {
            db.updateUser(user).addOnCompleteListener {
                postRequest(it.isSuccessful, LiveDataTag.addEditUser)
                postDelayed { dismissLoading() }
            }
        } ?: run {
            db.createUser(user.email ?: "", "${user.no_id}2022").continueWith {
                user.key = it.result.user?.uid
                user.role = if (selectedTab == 0) guru else siswa
                return@continueWith db.updateUser(user)
            }.addOnCompleteListener {
                postRequest(it.isSuccessful, LiveDataTag.addEditUser)
                postDelayed { dismissLoading() }
            }
        }
    }

    fun checkPinjaman(key: String) {
        db.loadPinjamBukuList(key).addOnCompleteListener {
            if (it.isSuccessful) {
                adaPinjaman = it.result.toObjects(PinjamBuku::class.java)
                    .any { book -> book.status == dipinjam }
            }
        }
    }
}