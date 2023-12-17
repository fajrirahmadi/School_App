package com.jhy.project.schoollibrary.feature.school.schoolprofile

import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.model.SchoolProfileModel
import com.jhy.project.schoollibrary.model.SchoolProfile
import com.jhy.project.schoollibrary.model.adapter.FilterCustomAdapter
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolProfileViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    private val _imageState = MutableStateFlow("")
    val imageState = _imageState.asStateFlow()

    private val _nameState = MutableStateFlow("")
    val nameState = _nameState.asStateFlow()

    private val _contentState = MutableStateFlow<SchoolProfile?>(null)
    val contentState = _contentState.asStateFlow()

    val adapter = FastItemAdapter<FilterCustomAdapter>()
    var selectedPage: Int = 0

    fun onCreate() {
        loadSchoolProfile()
    }

    var schoolJob: Job? = null
    private fun loadSchoolProfile() {
        showLoading()
        schoolJob?.cancel()
        schoolJob = viewModelScope.launch {
            observeStatefulDoc<SchoolProfileModel>(
                db.loadSchoolProfile()
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> postDelayed { showLoading(false) }
                    is FirestoreState.Loading -> showLoading()
                    is FirestoreState.Success -> {
                        it.data?.let { model -> initSchoolProfile(model) }
                        postDelayed { showLoading(false) }
                    }
                }
            }
        }
    }

    private fun initSchoolProfile(model: SchoolProfileModel) {
        _imageState.value = model.image
        _nameState.value = model.name

        adapter.clear()
        model.items.forEachIndexed { index, it ->
            adapter.add(FilterCustomAdapter(it.title.capitalizeWord(), selectedPage == index))
        }
        adapter.notifyAdapterDataSetChanged()

        adapter.onClickListener = { _, _, _, position ->
            selectedPage = position
            adapter.adapterItems.firstOrNull { it.choose }?.choose = false
            adapter.getAdapterItem(position).choose = true
            adapter.notifyAdapterDataSetChanged()
            updateContent(model.items[position])
            true
        }

        updateContent(model.items[selectedPage])
    }

    private fun updateContent(content: SchoolProfile) {
        _contentState.value = content
    }

}