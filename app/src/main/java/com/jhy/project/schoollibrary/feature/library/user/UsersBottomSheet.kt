package com.jhy.project.schoollibrary.feature.library.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.jhy.project.schoollibrary.base.BaseViewBindingBottomSheet
import com.jhy.project.schoollibrary.databinding.BottomsheetListBinding
import com.jhy.project.schoollibrary.extension.hideKeyboard
import com.jhy.project.schoollibrary.extension.initVerticalAdapter
import com.jhy.project.schoollibrary.model.User
import dagger.hilt.android.AndroidEntryPoint

const val usersBottomSheet = "users_bottom_sheet"

@AndroidEntryPoint
class UsersBottomSheet(
    private val role: String? = null,
    private val listener: UserListener
) :
    BaseViewBindingBottomSheet<BottomsheetListBinding>() {

    private val viewModel by viewModels<UsersViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            if (it) showLoading() else dismissLoading()
        }

        viewModel.userAdapter.onClickListener = { _, _, data, _ ->
            listener.pickUser(data.user)
            dismiss()
            true
        }

        viewModel.role = role
        viewModel.loadUserList()
    }

    @SuppressLint("SetTextI18n")
    private fun bindView() {
        binding.apply {
            titleTv.text = "Daftar Pengguna"
            listRv.initVerticalAdapter(requireContext(), viewModel.userAdapter)
            scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
                val childView = scrollView.getChildAt(scrollView.childCount - 1)
                val diff = childView.bottom - (scrollView.height + scrollView.scrollY)
                if (diff < 20) {
                    viewModel.showUserAdapter(false)
                }
            }
            searchTextInput.setEndIconOnClickListener {
                searchBox.text?.clear()
                viewModel.doSearch("")
            }
            searchBox.apply {
                hint = "Cari nama atau nomor identitas..."
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

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomsheetListBinding
        get() = BottomsheetListBinding::inflate

    override fun isAlwaysFull(): Boolean {
        return true
    }
}

interface UserListener {
    fun pickUser(user: User)
}