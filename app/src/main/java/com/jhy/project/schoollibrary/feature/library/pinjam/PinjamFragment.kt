package com.jhy.project.schoollibrary.feature.library.pinjam

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.component.ConfirmationBottomSheet
import com.jhy.project.schoollibrary.component.confirmationBottomSheet
import com.jhy.project.schoollibrary.databinding.FragmentPinjamBinding
import com.jhy.project.schoollibrary.extension.hideKeyboard
import com.jhy.project.schoollibrary.extension.initHorizontalAdapter
import com.jhy.project.schoollibrary.extension.initVerticalAdapter
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.dipinjam
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinjamFragment : BaseViewBindingFragment<FragmentPinjamBinding>() {

    private val args by navArgs<PinjamFragmentArgs>()
    private val viewModel by viewModels<PinjamViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.adapter.onClickListener = { _, _, data, _ ->
            viewModel.userState.value?.let {
                if (it.role == admin && data.data.status == dipinjam) {
                    showBottomSheet(
                        ConfirmationBottomSheet(
                            "Konfirmasi",
                            "Apakah Anda yakin untuk menyelesaikan daftar pinjam ini?",
                            actionYes = {
                                viewModel.markSelesai(data.data)
                            }
                        ), confirmationBottomSheet
                    )
                }
            }

            true
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            if (it.second == LiveDataTag.selesaiPinjamBuku) {
                if (it.first) showInfoDialog(requireContext(), "Daftar pinjam selesai") {
                    viewModel.onCreate(args.ukey, args.bkey)
                } else showInfoDialog(
                    requireContext(),
                    "Terjadi kesalahan saat menyelesaikan daftar pinjam"
                )
            }
        }

        viewModel.userState.observe(viewLifecycleOwner) {
            val uKey = if (it?.role == admin) args.ukey else it?.key ?: args.ukey
            viewModel.onCreate(uKey, args.bkey)
        }

        viewModel.loadUserData()
    }

    private fun bindView() {
        binding.apply {
            filterRv.initHorizontalAdapter(requireContext(), viewModel.filterAdapter)
            searchListRv.initVerticalAdapter(requireContext(), viewModel.adapter)
            searchTextInput.isVisible = args.bkey.isNullOrEmpty() && args.ukey.isNullOrEmpty()
            searchTextInput.setEndIconOnClickListener {
                searchBox.text?.clear()
                viewModel.doSearch()
            }
            searchBox.apply {
                hint = "Judul buku, nama, atau identitas peminjam..."
                setOnEditorActionListener { search, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.doSearch(search.text.toString())
                        search.hideKeyboard()
                    }
                    false
                }
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPinjamBinding
        get() = FragmentPinjamBinding::inflate
}