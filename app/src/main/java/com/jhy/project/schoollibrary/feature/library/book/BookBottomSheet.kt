package com.jhy.project.schoollibrary.feature.library.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetListBinding
import com.jhy.project.schoollibrary.extension.hideKeyboard
import com.jhy.project.schoollibrary.extension.initGridAdapter
import com.jhy.project.schoollibrary.feature.library.home.CaptureAct
import com.jhy.project.schoollibrary.model.Book
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint

const val booksBottomSheet = "books_bottom_sheet"

@AndroidEntryPoint
class BookBottomSheet(
    private val filter: String? = null,
    private val listener: BookListener
) :
    BaseViewBindingBottomSheet<BottomsheetListBinding>() {

    private val viewModel by viewModels<BookSheetViewModel>()

    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let {
            binding.searchBox.setText(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            if (it) showLoading() else dismissLoading()
        }

        viewModel.bookAdapter.onClickListener = { _, _, data, _ ->
            listener.pickBook(data.book, viewModel.code)
            dismiss()
            true
        }

        viewModel.filter = filter
        viewModel.loadBookList()
    }

    private fun bindView() {
        binding.apply {
            titleTv.text = "Daftar Buku"
            listRv.initGridAdapter(requireContext(), viewModel.bookAdapter, 2)
            scanBarCode.apply {
                isVisible = true
                setOnClickListener {
                    val options = ScanOptions()
                    options.apply {
                        setPrompt("Volume up to flash on")
                        setBeepEnabled(true)
                        setOrientationLocked(true)
                        captureActivity = CaptureAct::class.java
                    }
                    barLauncher.launch(options)
                }
            }
            searchTextInput.setEndIconOnClickListener {
                searchBox.text?.clear()
                viewModel.doSearch("")
            }
            searchBox.apply {
                hint = "Cari nama buku atau isbn..."
                setOnEditorActionListener { search, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.doSearch(search.text.toString())
                        search.hideKeyboard()
                    }
                    false
                }
            }
            scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
                val childView = scrollView.getChildAt(scrollView.childCount - 1)
                val diff = childView.bottom - (scrollView.height + scrollView.scrollY)
                if (diff < 20) {
                    viewModel.showBookAdapter(false)
                }
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetListBinding
        get() = BottomsheetListBinding::inflate

}

interface BookListener {
    fun pickBook(book: Book, code: String?)
}