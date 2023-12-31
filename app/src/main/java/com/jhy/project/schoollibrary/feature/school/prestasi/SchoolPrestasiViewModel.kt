package com.jhy.project.schoollibrary.feature.school.prestasi

import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.model.SchoolPrestasi
import com.jhy.project.schoollibrary.model.SchoolPrestasiModel
import com.jhy.project.schoollibrary.model.SchoolProfileModel
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchoolPrestasiViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    private val _imageState = MutableStateFlow("")
    val imageState = _imageState.asStateFlow()

    private val _yearState = MutableStateFlow(emptyList<String>())
    val yearState = _yearState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(0)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _prestasiState = MutableStateFlow(emptyList<String>())
    val prestasiState = _prestasiState.asStateFlow()

    private var items = HashMap<String, MutableList<String>>()

    fun onCreate() {
        loadPrestasi()
    }

    var schoolJob: Job? = null
    private fun loadPrestasi() {
        schoolJob?.cancel()
        schoolJob = viewModelScope.launch {
            observeStatefulDoc<SchoolPrestasiModel>(
                db.loadSchoolPrestasi()
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> postDelayed { showLoading(false) }
                    is FirestoreState.Loading -> showLoading()
                    is FirestoreState.Success -> {
                        it.data?.let { model ->
                            _imageState.value = model.image
                            updateData(model.items)
                        }
                        postDelayed { showLoading(false) }
                    }
                }
            }
        }
    }

    private fun updateData(items: List<SchoolPrestasi>) {
        this.items.clear()
        items.forEach {
            if (this.items.keys.contains(it.year)) {
                this.items[it.year]?.add(it.prestasi)
            } else {
                this.items[it.year] = mutableListOf(it.prestasi)
            }
        }
        updateYears()
    }

    private fun updateYears() {
        _yearState.value = items.keys.sortedByDescending { it }
        if (_yearState.value.isNotEmpty()) setSelectedFilter(0)
    }

    fun setSelectedFilter(index: Int) {
        _selectedFilter.value = index
        updatePrestasi()
    }

    private fun updatePrestasi() {
        val year = _yearState.value[_selectedFilter.value]
        _prestasiState.value = items[year] ?: emptyList()
    }
}