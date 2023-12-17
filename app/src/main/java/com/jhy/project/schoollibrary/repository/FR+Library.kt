package com.jhy.project.schoollibrary.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.storage.UploadTask
import com.jhy.project.schoollibrary.extension.generateKeyword
import com.jhy.project.schoollibrary.extension.roundUpToNearestThousand
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.PinjamBuku
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import com.jhy.project.schoollibrary.model.selesai
import java.io.File

fun FirebaseRepository.loadBookList(
    filter: String? = null, keyword: String = "", limit: Long = 50
): Query {
    val query = db.collection(bookDb)
    return when {
        !filter.isNullOrEmpty() && keyword.isNotBlank() -> {
            query.whereEqualTo("paket", filter).whereArrayContains("words", keyword)
                .orderBy("judul").limit(limit)
        }

        keyword.isNotBlank() -> {
            query.whereArrayContains("words", keyword).orderBy("judul").limit(limit)
        }

        !filter.isNullOrEmpty() -> {
            query.whereEqualTo("paket", filter).orderBy("judul").limit(limit)
        }

        else -> {
            query.orderBy("judul").limit(limit)
        }
    }
}

fun FirebaseRepository.loadBooks(
    selectedFilter: List<String> = emptyList(),
    keyword: String = "",
    limit: Long = 30
): Query {
    val query = db.collection(bookDb)
    return when {
        keyword.isNotBlank() && selectedFilter.isNotEmpty() -> {
            query.whereIn("kategori", selectedFilter).whereArrayContains("words", keyword)
                .orderBy("judul").limit(limit)
        }

        keyword.isNotBlank() -> {
            query.whereArrayContains("words", keyword).orderBy("judul").limit(limit)
        }

        selectedFilter.isNotEmpty() -> {
            query.whereIn("kategori", selectedFilter).orderBy("judul").limit(limit)
        }

        else -> {
            query.orderBy("judul").limit(limit)
        }
    }
}

fun FirebaseRepository.loadBook(key: String): DocumentReference {
    return db.collection(bookDb).document(key)
}

fun FirebaseRepository.submitBook(book: Book): Task<Void> {
    book.key = book.key ?: db.collection(bookDb).document().id

    val batch = db.batch()

    val bookRef = db.collection(bookDb).document(book.key ?: "")
    val bookCountRef = db.collection("counterDb").document("bookCounter")

    batch.set(bookRef, book)
    batch.update(bookCountRef, "count", FieldValue.increment(1))

    return batch.commit()
}

fun FirebaseRepository.updateBook(book: Book) {
    db.collection(bookDb).document(book.key ?: "").set(book)
}

fun FirebaseRepository.submitPinjam(
    userPinjam: User?, datePinjam: Long, dateReturn: Long, bookList: MutableList<BookAdapter>
): Task<Void> {
    val batch = db.batch()

    for (book in bookList) {
        val pinjam = PinjamBuku()
        val words = book.book.judul?.generateKeyword()
        words?.addAll(book.book.isbn?.generateKeyword() ?: emptyList())
        words?.addAll(userPinjam?.words ?: emptyList())
        pinjam.apply {
            key = db.collection(pinjamBukuDb).document().id
            uid = userPinjam?.no_id
            bid = book.book.key
            name = userPinjam?.name
            judul = book.book.judul
            url = book.book.image
            ukey = userPinjam?.key
            bookCode = book.book.bookCode
            this.words = words ?: emptyList()
            date = datePinjam
            returnDate = dateReturn
            actualReturnDate = dateReturn
        }
        val ref = db.collection(pinjamBukuDb).document(pinjam.key ?: "")
        val bookRef = db.collection(bookDb).document(book.book.key ?: "")
        batch.set(ref, pinjam)
        batch.update(bookRef, "dipinjam", FieldValue.increment(1))
    }

    val userRef = db.collection(userDb).document(userPinjam?.key ?: "")
    batch.update(userRef, "dipinjam", FieldValue.increment(bookList.size.toLong()))

    return batch.commit()
}

fun FirebaseRepository.loadPinjamBukuList(
    userKey: String? = null,
    bookKey: String? = null,
    keyword: String = "",
    limit: Long = 100
): Query {
    when {
        !userKey.isNullOrEmpty() -> {
            return db.collection(pinjamBukuDb).whereEqualTo("ukey", userKey)
                .orderBy("date", Query.Direction.DESCENDING)
        }

        !bookKey.isNullOrEmpty() -> {
            return db.collection(pinjamBukuDb).whereEqualTo("ukey", bookKey)
                .orderBy("date", Query.Direction.DESCENDING)
        }

        keyword.isNotBlank() -> {
            return db.collection(pinjamBukuDb).whereArrayContains("words", keyword.lowercase())
                .orderBy("date", Query.Direction.DESCENDING).limit(limit)
        }

        else -> {
            return db.collection(pinjamBukuDb).orderBy("date", Query.Direction.DESCENDING)
                .limit(limit)

        }
    }
}

fun FirebaseRepository.selesaikanPinjamBuku(data: PinjamBuku): Task<Void> {

    val denda =
        ((System.currentTimeMillis() - data.returnDate) / (24 * 3600)).roundUpToNearestThousand()

    val batch = db.batch()

    val userRef = db.collection(userDb).document(data.ukey ?: "")
    val userPinjamRef = db.collection(userDb).document(data.ukey ?: "").collection(
        pinjamBukuDb
    ).document(data.key ?: "")
    val pinjamRef = db.collection(pinjamBukuDb).document(data.key ?: "")
    val bookRef = db.collection(bookDb).document(data.bid ?: "")

    batch.update(
        userRef, mapOf(
            "pinjaman" to FieldValue.increment(-1)
        )
    )
    batch.delete(userPinjamRef)
    batch.update(
        pinjamRef, mapOf(
            "status" to selesai,
            "actualReturnDate" to System.currentTimeMillis(),
            "denda" to if (denda > 0) denda else 0
        )
    )
    batch.update(
        pinjamRef, mapOf(
            "status" to selesai,
            "actualReturnDate" to System.currentTimeMillis()
        )
    )
    batch.update(
        bookRef, mapOf(
            "dipinjam" to FieldValue.increment(-1)
        )
    )

    return batch.commit()
}