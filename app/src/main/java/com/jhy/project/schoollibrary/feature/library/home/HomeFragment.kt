package com.jhy.project.schoollibrary.feature.library.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentHomeBinding
import com.jhy.project.schoollibrary.extension.hideKeyboard
import com.jhy.project.schoollibrary.extension.initGridAdapter
import com.jhy.project.schoollibrary.extension.initHorizontalAdapter
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.model.adapter.LibraryMenu
import com.jhy.project.schoollibrary.model.admin
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseViewBindingFragment<FragmentHomeBinding>() {

    private val viewModel by viewModels<HomeViewModel>()

    private val barLauncher = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let {
            binding.searchBox.setText(it.replace("-", ""))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        lifecycleScope.launch {
            viewModel.bookCountState.collectLatest {
                binding.titleCount.text = "Total Buku ($it)"
            }
        }

        viewModel.userState.observe(viewLifecycleOwner) {
            binding.homeMenuRv.isVisible = it != null
            it?.let {
                binding.addBookBtn.isVisible = it.role == admin
                viewModel.initHomeMenuAdapter(it.role == admin)
            }
        }

        viewModel.bookAdapter.onClickListener = { _, _, data, _ ->
            data.book.key?.let {
                findNavController().navigate(
                    HomeFragmentDirections.actionToDetailBookFragment(it), getNavOptions()
                )
            }
            true
        }

        viewModel.homeMenuAdapter.onClickListener = { _, _, data, _ ->
            findNavController().navigate(
                when (data.menu) {
                    LibraryMenu.PinjamBuku -> HomeFragmentDirections.actionToBookPinjamFragment()
                    LibraryMenu.DaftarPengguna -> HomeFragmentDirections.actionToDaftarPenggunaFragment()
                    LibraryMenu.DaftarPinjam -> HomeFragmentDirections.actionToPinjamFragment()
                    LibraryMenu.DaftarKunjungan -> HomeFragmentDirections.actionToLibVisitFragment()
                }, getNavOptions()
            )

            true
        }

        postDelayed({
            viewModel.onCreate()
        }, 300)
    }

    private fun bindView() {
        binding.apply {
            homeMenuRv.initGridAdapter(requireContext(), viewModel.homeMenuAdapter, 4)
            filterRecyclerView.initHorizontalAdapter(requireContext(), viewModel.filterAdapter)
            filterRecyclerView.scrollToPosition(viewModel.filterIndex())
            bookRecyclerView.initGridAdapter(requireContext(), viewModel.bookAdapter, 2)
            swipeRefresh.apply {
                setOnRefreshListener {
                    isRefreshing = false
                    viewModel.onCreate(true)
                }
            }
            searchTextInput.setEndIconOnClickListener {
                searchBox.text?.clear()
                viewModel.doSearch("")
            }
            searchBox.apply {
                setOnEditorActionListener { search, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.doSearch(search.text.toString())
                        search.hideKeyboard()
                    }
                    false
                }
            }
            addBookBtn.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionToAddEditBookFragment("Tambah Buku", null),
                    getNavOptions()
                )
            }
            scanBarCode.setOnClickListener {
                scanQR()
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

    private fun scanQR() {
        val options = ScanOptions()
        options.apply {
            setPrompt("Volume up to flash on")
            setBeepEnabled(true)
            setOrientationLocked(true)
            captureActivity = CaptureAct::class.java
        }
        barLauncher.launch(options)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate
}