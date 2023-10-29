package com.jhy.project.schoollibrary.feature.library.book

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.component.ConfirmationBottomSheet
import com.jhy.project.schoollibrary.component.DatePickerBottomSheet
import com.jhy.project.schoollibrary.component.confirmationBottomSheet
import com.jhy.project.schoollibrary.component.dateBottomSheet
import com.jhy.project.schoollibrary.databinding.FragmentBookPinjamBinding
import com.jhy.project.schoollibrary.extension.initGridAdapter
import com.jhy.project.schoollibrary.extension.popBack
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.feature.library.user.UserListener
import com.jhy.project.schoollibrary.feature.library.user.UsersBottomSheet
import com.jhy.project.schoollibrary.feature.library.user.usersBottomSheet
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookPinjamFragment : BaseViewBindingFragment<FragmentBookPinjamBinding>() {

    private val args by navArgs<BookPinjamFragmentArgs>()
    private val viewModel by viewModels<BookViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView()

        viewModel.userState.observe(viewLifecycleOwner) {
            it?.let {
                if (it.role != admin) {
                    viewModel.userPinjam = it
                    binding.idEt.text = it.no_id
                    binding.nameTv.text = "Peminjam: ${it.name}"
                }
                binding.idEt.isEnabled = it.role == admin
            }
        }

        viewModel.loadUserData()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.bookAdapter.onClickListener = { _, _, _, position ->
            showBottomSheet(
                ConfirmationBottomSheet("Konfirmasi",
                    "Ingin menghapus buku dari list pinjam?",
                    actionYes = {
                        viewModel.removeBook(position)
                    }), confirmationBottomSheet
            )
            true
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            if (it.second == LiveDataTag.pinjamBuku) {
                if (it.first) showInfoDialog(requireContext(), "Pinjam buku berhasil") {
                    popBack()
                } else showInfoDialog(
                    requireContext(), "Terjadi kesalahan saat meminjam buku, silakan coba lagi!"
                )
            }
        }

        args.book?.let {
            viewModel.addBook(it)
        }

    }

    private fun bindView() {
        binding.apply {
            bookRv.initGridAdapter(requireContext(), viewModel.bookAdapter, 2)
            idEt.setOnClickListener {
                showUserBottomSheet()
            }
            addBookBtn.setOnClickListener {
                showBooksBottomSheet()
            }
            pinjamBtn.setOnClickListener {
                viewModel.submitPinjam(requireContext())
            }
            dateEt.apply {
                text = viewModel.datePinjam.toDateFormat("dd-MM-yyyy")
                setOnClickListener {
                    showDateBottomSheet()
                }
            }
            returnDateTv.apply {
                text = viewModel.dateReturn.toDateFormat("dd-MM-yyyy")
                setOnClickListener {
                    showReturnDateBottomSheet()
                }
            }
        }
    }

    private fun showUserBottomSheet() {
        showBottomSheet(
            UsersBottomSheet(object : UserListener {
                @SuppressLint("SetTextI18n")
                override fun pickUser(user: User) {
                    viewModel.userPinjam = user
                    binding.idEt.text = user.no_id
                    binding.nameTv.text = "Peminjam: ${user.name}"
                }
            }), usersBottomSheet
        )
    }

    private fun showBooksBottomSheet() {
        showBottomSheet(
            BookBottomSheet(object : BookListener {
                override fun pickBook(book: Book) {
                    showBottomSheet(
                        BookCodeSheet {
                            book.bookCode = it
                            viewModel.addBook(book)
                        }, "book_code"
                    )
                }
            }), booksBottomSheet
        )
    }

    private fun showDateBottomSheet() {
        showBottomSheet(
            DatePickerBottomSheet(date = viewModel.datePinjam) {
                viewModel.datePinjam = it
                binding.dateEt.text = it.toDateFormat("dd-MM-yyyy")
            }, dateBottomSheet
        )
    }

    private fun showReturnDateBottomSheet() {
        showBottomSheet(
            DatePickerBottomSheet(date = viewModel.dateReturn) {
                viewModel.dateReturn = it
                binding.returnDateTv.text = it.toDateFormat("dd-MM-yyyy")
            }, dateBottomSheet
        )
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBookPinjamBinding
        get() = FragmentBookPinjamBinding::inflate
}