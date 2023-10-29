package com.jhy.project.schoollibrary.feature.library.book

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.jhy.project.schoollibrary.base.BaseViewBindingFragment
import com.jhy.project.schoollibrary.databinding.FragmentBookAddEditBinding
import com.jhy.project.schoollibrary.extension.int
import com.jhy.project.schoollibrary.extension.popBack
import com.jhy.project.schoollibrary.extension.setImage
import com.jhy.project.schoollibrary.extension.trim
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.BookCategory
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.toBookCategory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookAddEditFragment : BaseViewBindingFragment<FragmentBookAddEditBinding>() {

    private var uri: String? = null
    private var category: BookCategory = BookCategory.Kelas7_1

    private val args by navArgs<BookAddEditFragmentArgs>()
    private val viewModel by viewModels<BookViewModel>()

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private val askPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var granted = false
            it.forEach {
                if (it.value) granted = true
                else {
                    granted = false
                    return@forEach
                }
            }
            if (granted) cropImageLauncher.launch(
                options {
                    setImageSource(includeGallery = true, includeCamera = true)
                    setGuidelines(CropImageView.Guidelines.ON)
                    setFixAspectRatio(true)
                    setAspectRatio(2, 3)
                }
            )
            else showInfoDialog(
                requireContext(),
                "Berikan akses penyimpanan untuk menambahkan image"
            )
        }

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            this.uri = result.uriContent.toString()
            binding.bookIv.setImage(this.uri)
        } else {
            val exception = result.error
            showInfoDialog(requireContext(), exception.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindView(args.book)

        viewModel.loadingState.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.requestState.observe(viewLifecycleOwner) {
            if (it.second == LiveDataTag.addEditBook) {
                if (it.first) showInfoDialog(requireContext(), "Berhasil menambahkan buku") {
                    popBack()
                } else {
                    showInfoDialog(requireContext(), "Terjadi kesalahan saat menambahkan buku")
                }
            }
        }

    }

    private fun bindView(book: Book?) {
        binding.apply {
            bookIv.apply {
                setImage(book?.image)
                setOnClickListener {
                    askPermission.launch(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        )
                    )
                }
            }
            judulEt.setText(book?.judul)
            category = (book?.kategori?.toBookCategory() ?: BookCategory.Umum)
            kategoriEt.apply {
                setText(category.title)
                setOnClickListener { showBookCategory() }
            }
            pengarangEt.setText(book?.pengarang)
            penerbitEt.setText(book?.penerbit)
            tahunTerbitEt.setText(book?.tahun?.toString())
            jumlahHalamanEt.setText(book?.jumlah_halaman?.toString())
            isbnEt.setText(book?.isbn)
            stockEt.setText(book?.stock?.toString())
            btnSubmit.setOnClickListener {
                submitBook()
            }
        }
    }

    private fun showBookCategory() {
        showBottomSheet(
            BookCategoryBottomSheet() {
                category = it
                binding.kategoriEt.setText(it.title)
            }, bookCategoryBottomSheet
        )
    }

    private fun submitBook() {
        binding.apply {
            val book = args.book ?: Book()
            when {
                uri.isNullOrEmpty() && book.image.isNullOrEmpty() -> showInfoDialog(
                    requireContext(),
                    "Cover tidak boleh kosong"
                )
                judulEt.trim.isEmpty() -> showInfoDialog(
                    requireContext(),
                    "Judul buku tidak boleh kosong"
                )
                else -> {
                    book.apply {
                        judul = judulEt.trim
                        kategori = category.toString()
                        pengarang = pengarangEt.trim
                        penerbit = penerbitEt.trim
                        tahun = tahunTerbitEt.int
                        jumlah_halaman = jumlahHalamanEt.int
                        isbn = isbnEt.trim
                        stock = stockEt.int
                    }

                    viewModel.checkISBN(requireContext(), uri, book)
                }
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBookAddEditBinding
        get() = FragmentBookAddEditBinding::inflate
}