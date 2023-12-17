package com.jhy.project.schoollibrary.feature.library.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.constanta.RemoteConfigHelper
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.adapter.FilterCustomAdapter
import com.jhy.project.schoollibrary.model.adapter.PenggunaAdapter
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.guru
import com.jhy.project.schoollibrary.model.siswa
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.createUser
import com.jhy.project.schoollibrary.repository.loadUserList
import com.jhy.project.schoollibrary.repository.loadUserListByRoleAndClass
import com.jhy.project.schoollibrary.repository.updateUser
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    private var keyword = ""
    var role: String? = null

    private var selectedFilter = 0
    private var showDetail = true

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

    private var userListJob: Job? = null
    private var kelasJob: Job? = null
    fun initKelasFilter() {
        if (_kelasFilter.adapterItemCount > 0) return
        kelasJob?.cancel()
        kelasJob = viewModelScope.launch {
            observeStatefulCollection<Kelas>(
                db.getKelas()
            ).collect {
                if (it is FirestoreState.Success) {
                    val list = it.data.asList<Kelas>()
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
        userListJob?.cancel()
        userListJob = viewModelScope.launch {
            observeStatefulCollection<User>(
                db.loadUserListByRoleAndClass(
                    if (selectedTab == 0) guru else siswa,
                    if (selectedTab == 0) "" else kelas
                )
            ).collect {
                if (it is FirestoreState.Success) {
                    userList.clear()
                    userList.addAll(it.data.asList())
                    showUserAdapter(keyword)
                }
                postDelayed { showLoading(false) }
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

    fun loadUserList() {
        showDetail = false
        showLoading()
        userListJob?.cancel()
        userListJob = viewModelScope.launch {
            observeStatefulCollection<User>(
                db.loadUserList(keyword, role ?: "")
            ).collect {
                if (it is FirestoreState.Success) {
                    userList.clear()
                    userList.addAll(
                        it.data.asList<User>().filter { user -> user.role != admin }
                    )
                    showUserAdapter(true)
                }
                postDelayed { showLoading(false) }
            }
        }
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
}