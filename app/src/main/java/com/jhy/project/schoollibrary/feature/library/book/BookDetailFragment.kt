package com.jhy.project.schoollibrary.feature.library.book

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jhy.project.schoollibrary.R
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.component.ConfirmationBottomSheet
import com.jhy.project.schoollibrary.component.ZoomImageBottomSheet
import com.jhy.project.schoollibrary.component.confirmationBottomSheet
import com.jhy.project.schoollibrary.databinding.FragmentBookDetailBinding
import com.jhy.project.schoollibrary.extension.fadeIn
import com.jhy.project.schoollibrary.extension.initVerticalAdapter
import com.jhy.project.schoollibrary.extension.popBack
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.admin
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailFragment : BaseViewBindingFragment<FragmentBookDetailBinding>() {

    private val args by navArgs<BookDetailFragmentArgs>()
    private val viewModel by viewModels<BookViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading(true)

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.bookState.observe(viewLifecycleOwner) {
            it?.let { book ->
                binding.apply {
                    bookIv.setImage(book.image, R.drawable.placeholder_book)
                    bookIv.setOnClickListener {
                        book.image?.let { image ->
                            showBottomSheet(ZoomImageBottomSheet(image), "bottom_sheet")
                        }
                    }
                    titleTv.text = book.judul
                    detailRv.initVerticalAdapter(requireContext(), viewModel.bookDetailAdapter)
                    viewModel.userState.value?.let { user ->
                        if (user.role == admin) addMenu(book)
                    }
                    book.downloadUrl?.let { url ->
                        downloadBtn.apply {
                            isVisible = true
                            setOnClickListener {
                                findNavController().navigate(
                                    BookDetailFragmentDirections.actionToBookReadPdfFragment(url),
                                    getNavOptions()
                                )
                            }
                        }
                    }
                    container.fadeIn()
                }
            }
        }

        viewModel.stockState.observe(viewLifecycleOwner) {
            binding.pinjamBtn.isVisible = it > 0
        }

        viewModel.userState.observe(viewLifecycleOwner) {
            it?.let {
                if (it.role == admin) {
                    viewModel.bookState.value?.let { book ->
                        addMenu(book)
                    }
                }
            }
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            if (it.second == LiveDataTag.deleteBook) {
                if (it.first) showInfoDialog(requireContext(), "Berhasil menghapus buku") {
                    popBack()
                } else {
                    showInfoDialog(requireContext(), "Gagal menghapus buku")
                }
            }
        }

        viewModel.loadUserData()

        binding.apply {
            peminjamBtn.setOnClickListener {
                viewModel.bookState.value?.let {
                    findNavController().navigate(
                        BookDetailFragmentDirections.actionToPinjamFragment(bkey = it.key),
                        getNavOptions()
                    )
                }
            }
            pinjamBtn.setOnClickListener {
                if (!viewModel.isLogin) {
                    navigateToLoginPage()
                    return@setOnClickListener
                }
                viewModel.bookState.value?.let { book ->
                    showBottomSheet(
                        BookCodeSheet {
                            book.bookCode = it
                            findNavController().navigate(
                                BookDetailFragmentDirections.actionToBookPinjamFragment(book),
                                getNavOptions()
                            )
                        }, "book_code"
                    )
                }
            }
        }

        postDelayed({
            viewModel.loadBook(args.key)
        }, 300)
    }

    private fun addMenu(book: Book) {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (!menu.hasVisibleItems()) {
                    menuInflater.inflate(R.menu.menu_book, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        findNavController().navigate(
                            BookDetailFragmentDirections.actionToAddEditBookFragment(
                                "Ubah Buku", book
                            ), getNavOptions()
                        )
                        true
                    }
                    R.id.action_delete -> {
                        showBottomSheet(
                            ConfirmationBottomSheet("Konfirmasi",
                                "Apakah kamu yakin ingin menghapus buku ini?",
                                actionYes = {
                                    viewModel.deleteBook(book.key)
                                }), confirmationBottomSheet
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBookDetailBinding
        get() = FragmentBookDetailBinding::inflate
}