package com.jhy.project.schoollibrary.feature.library.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentUsersBinding
import com.jhy.project.schoollibrary.extension.hideKeyboard
import com.jhy.project.schoollibrary.extension.initHorizontalAdapter
import com.jhy.project.schoollibrary.extension.initVerticalAdapter
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.guru
import com.jhy.project.schoollibrary.model.siswa
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersFragment : BaseViewBindingFragment<FragmentUsersBinding>() {

    private val viewModel by viewModels<UsersViewModel>()

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.showUserAdapter(s.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()

        viewModel.initKelasFilter()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            if (it.second == LiveDataTag.addEditUser) {
                if (it.first) showInfoDialog(requireContext(), "Berhasil menambahkan pengguna") {
                    viewModel.changeTab(viewModel.selectedTab)
                }
                else showInfoDialog(requireContext(), "Gagal menambahkan pengguna")
            }
        }

        viewModel.userAdapter.onClickListener = { _, _, data, _ ->
            val type = if (viewModel.selectedTab == 0) guru else siswa
            findNavController().navigate(
                UsersFragmentDirections.actionToUserAddEditFragment(
                    "Tambah Pengguna",
                    type,
                    data.user
                ),
                getNavOptions()
            )
            true
        }

        viewModel.tabState.observe(viewLifecycleOwner) {
            binding.filterRv.isVisible = it == 1
        }

        viewModel.changeTab(viewModel.selectedTab)
    }

    private fun bindView() {
        binding.apply {
            searchTab.apply {
                isVisible = true
                addTab(this.newTab().setText("Guru"))
                addTab(this.newTab().setText("Siswa"))
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {

                        viewModel.changeTab(tab?.position ?: 0)

                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }

                })
                getTabAt(viewModel.selectedTab)?.select()
            }
            filterRv.initHorizontalAdapter(requireContext(), viewModel.kelasFilter)
            searchListRv.initVerticalAdapter(requireContext(), viewModel.userAdapter)
            searchTextInput.setEndIconOnClickListener {
                searchBox.text?.clear()
                viewModel.showUserAdapter("")
            }
            searchBox.apply {
                hint = "Cari nama atau nomor identitas..."
                setOnEditorActionListener { search, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        viewModel.showUserAdapter(search.text.toString())
                        search.hideKeyboard()
                    }
                    false
                }
                addTextChangedListener(textWatcher)
            }
            addActionBtn.isVisible = false
            addActionBtn.setOnClickListener {
                val type = if (viewModel.selectedTab == 0) guru else siswa
                findNavController().navigate(
                    UsersFragmentDirections.actionToUserAddEditFragment("Tambah Pengguna", type),
                    getNavOptions()
                )
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUsersBinding
        get() = FragmentUsersBinding::inflate
}