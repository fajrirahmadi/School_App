package com.jhy.project.schoollibrary.feature.library.book

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.allowNewLine
import com.jhy.project.schoollibrary.extension.asList
import com.jhy.project.schoollibrary.extension.compressImage
import com.jhy.project.schoollibrary.extension.createFileFromUri
import com.jhy.project.schoollibrary.extension.generateKeyword
import com.jhy.project.schoollibrary.extension.halfYear
import com.jhy.project.schoollibrary.extension.sevenDay
import com.jhy.project.schoollibrary.extension.showToast
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.PinjamBuku
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import com.jhy.project.schoollibrary.model.adapter.KeyValueAdapter
import com.jhy.project.schoollibrary.model.constant.LiveDataTag
import com.jhy.project.schoollibrary.model.dipinjam
import com.jhy.project.schoollibrary.model.state.FirestoreState
import com.jhy.project.schoollibrary.model.toBookCategory
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.jhy.project.schoollibrary.repository.loadBook
import com.jhy.project.schoollibrary.repository.loadPinjamBukuList
import com.jhy.project.schoollibrary.repository.submitBook
import com.jhy.project.schoollibrary.repository.submitPinjam
import com.jhy.project.schoollibrary.utils.observeStatefulCollection
import com.jhy.project.schoollibrary.utils.observeStatefulDoc
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    var userPinjam: User? = null
    var datePinjam: Long = System.currentTimeMillis()
    var dateReturn: Long = System.currentTimeMillis() + sevenDay
    var isPaket: Boolean = false

    private val _bookAdapter = ItemAdapter<BookAdapter>()
    val bookAdapter by lazy {
        FastAdapter.with(_bookAdapter)
    }

    val bookState by lazy {
        MutableLiveData<Book?>()
    }

    val stockState by lazy {
        MutableLiveData<Int>()
    }

    private val _bookDetailAdapter = ItemAdapter<KeyValueAdapter>()
    val bookDetailAdapter by lazy {
        FastAdapter.with(_bookDetailAdapter)
    }

    fun checkISBN(context: Context, uri: String?, book: Book) {
        book.key?.let {
            submitBook(context, uri, book)
        } ?: run {
            if (book.isbn.isNullOrEmpty()) {
                submitBook(context, uri, book)
                return@run
            }
            showLoading()
            book.isbn?.let {
                db.getBookByISBN(it).get().addOnCompleteListener {
                    if (it.isSuccessful && !it.result.isEmpty) {
                        showInfoDialog(context, "Anda telah mengupload buku ini")
                        dismissLoading()
                    } else {
                        submitBook(context, uri, book)
                    }
                }
            }
        }
    }

    private fun submitBook(context: Context, uri: String?, book: Book) {
        showLoading()
        uri?.let {
            book.key = book.key ?: db.generateBookKey()
            context.createFileFromUri(book.key ?: "invalid", Uri.parse(uri))?.compressImage()
                ?.let { file ->
                    val path = "book/${file.nameWithoutExtension}"
                    db.uploadImageToServer(path, file)
                        .addOnSuccessListener {
                            it.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                                book.image = it.result?.toString() ?: book.image
                                submitBook(context, null, book)
                            }
                        }.addOnFailureListener {
                            submitBook(context, null, book)
                        }
                } ?: run {
                context.showToast("Failed upload Image")
                submitBook(context, null, book)
            }
        } ?: run {
            val words = book.judul?.generateKeyword()
            words?.addAll(book.isbn?.generateKeyword() ?: emptyList())
            book.words = words ?: emptyList()
            db.submitBook(book).addOnCompleteListener {
                postRequest(it.isSuccessful, LiveDataTag.addEditBook)
                dismissLoading()
            }
        }
    }

    var bookJob: Job? = null
    fun loadBook(key: String) {
        bookJob?.cancel()
        bookJob = viewModelScope.launch {
            observeStatefulDoc<Book>(
                db.loadBook(key)
            ).collect {
                when (it) {
                    is FirestoreState.Failed -> {
                        showLoading(false)
                    }

                    is FirestoreState.Loading -> {
                        showLoading()
                    }

                    is FirestoreState.Success -> {
                        bookState.value = it.data
                        bookState.value?.let {
                            showBookDetail(it)
                        }
                        postDelayed { showLoading(false) }
                    }
                }
            }
        }
    }

    private fun showBookDetail(book: Book) {
        _bookDetailAdapter.clear()
        _bookDetailAdapter.add(KeyValueAdapter("Kategori", book.kategori.toBookCategory().title))
        _bookDetailAdapter.add(KeyValueAdapter("Pengarang", book.pengarang?.allowNewLine()))
        _bookDetailAdapter.add(KeyValueAdapter("Penerbit", book.penerbit))
        _bookDetailAdapter.add(KeyValueAdapter("Tahun Terbit", book.tahun.toString()))
        if ((book.jumlah_halaman ?: 0) > 0) {
            _bookDetailAdapter.add(
                KeyValueAdapter(
                    "Jumlah Halaman",
                    book.jumlah_halaman.toString()
                )
            )

        }
        _bookDetailAdapter.add(KeyValueAdapter("ISBN", book.isbn?.ifBlank { "-" }))
        _bookDetailAdapter.add(KeyValueAdapter("Stok Buku", book.stock.toString()))

        val availableStock = book.stock - book.dipinjam
        _bookDetailAdapter.add(
            KeyValueAdapter("Stok tersisa", availableStock.toString())
        )
        stockState.value = availableStock
        bookDetailAdapter.notifyAdapterDataSetChanged()
        loadBookPinjam(book.key, book.stock)
    }

    private var bookPinjamJob: Job? = null
    private fun loadBookPinjam(key: String?, stock: Int) {
        bookPinjamJob?.cancel()
        bookPinjamJob = viewModelScope.launch {
            observeStatefulCollection<PinjamBuku>(
                db.loadPinjamBukuList(bookKey = key)
            ).collect {
                if (it is FirestoreState.Success) {
                    val size = it.data.asList<PinjamBuku>().filter { it.status == dipinjam }.size
                    val availableStock = stock - size
                    stockState.value = availableStock
                    _bookDetailAdapter.add(
                        KeyValueAdapter("Stok tersisa", availableStock.toString())
                    )
                    bookDetailAdapter.notifyAdapterDataSetChanged()
                }
            }
        }
    }

    fun addBook(book: Book) {
        for (data in _bookAdapter.adapterItems) {
            if (data.book.key == book.key) {
                return
            }
        }
        _bookAdapter.add(BookAdapter(book, true))
        bookAdapter.notifyAdapterDataSetChanged()
    }

    fun submitPinjam(context: Context) {
        when {
            userPinjam == null -> showInfoDialog(context, "Anda belum menambahkan id peminjam")
            _bookAdapter.adapterItemCount <= 0 -> showInfoDialog(
                context,
                "Anda belum menambahkan buku yang dipinjam"
            )
            else -> {
                submitPinjam()
            }
        }
    }

    private fun submitPinjam() {
        showLoading()
        db.submitPinjam(
            userPinjam,
            datePinjam,
            dateReturn,
            _bookAdapter.adapterItems).addOnCompleteListener {
            postRequest(it.isSuccessful, LiveDataTag.pinjamBuku)
            dismissLoading()
        }
    }

    fun removeBook(position: Int) {
        if (_bookAdapter.adapterItemCount > position) {
            _bookAdapter.remove(position)
            bookAdapter.notifyAdapterDataSetChanged()
        }
    }

    fun deleteBook(bookKey: String?) {
        bookKey?.let {
            showLoading()
            db.deleteBook(it).addOnCompleteListener {
                requestState.value = Pair(it.isSuccessful, LiveDataTag.deleteBook)
                dismissLoading()
            }
        }
    }

    fun setAsPaket(isPaket: Boolean) {
        this.isPaket = isPaket
        if (isPaket) dateReturn = System.currentTimeMillis() + halfYear
    }

}