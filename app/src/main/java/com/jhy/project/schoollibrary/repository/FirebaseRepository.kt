package com.jhy.project.schoollibrary.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.jhy.project.schoollibrary.extension.generateKeyword
import com.jhy.project.schoollibrary.extension.roundUpToNearestThousand
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.*
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

const val userDb = "user"
const val bookDb = "book"
const val pinjamBukuDb = "pinjamBuku"
const val counterDb = "counterDb"
const val bookCountKey = "bookCount"
const val classDb = "kelasDb"
const val homeDb = "homeDb"
const val articleDb = "articleDb"
const val schoolDb = "schoolDb"
const val mapelDb = "mapelDb"
const val jadwalDb = "jadwalDb"
const val galleryDb = "galleryDb"
const val visitorDb = "visitorDb"

@Singleton
class FirebaseRepository @Inject constructor() {

    private val _auth = Firebase.auth
    private val _db = Firebase.firestore
    private val _storage = Firebase.storage

    fun source(online: Boolean): Source {
        return if (online) Source.SERVER else Source.CACHE
    }

    fun getUid(): String {
        return _auth.uid ?: "invalid"
    }

    fun loginWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return _auth.signInWithEmailAndPassword(email, password)
    }

    fun sendEmailReset(email: String): Task<Void> {
        return _auth.sendPasswordResetEmail(email)
    }

    fun createUser(email: String, password: String): Task<AuthResult> {
        return _auth.createUserWithEmailAndPassword(email, password)
    }

    fun setUser(user: User): Task<Void> {
        user.key = getUid()
        return _db.collection(userDb).document(getUid()).set(user)
    }

    fun updateUser(user: User): Task<Void> {
        return _db.collection(userDb).document(user.key ?: "").set(user)
    }

    fun loadUser(): Task<DocumentSnapshot> {
        return _db.collection(userDb).document(getUid()).get()
    }

    fun loadUserList(word: String = "", limit: Long = 50): Task<QuerySnapshot> {
        if (word.isNotBlank()) {
            return _db.collection(userDb).whereArrayContains("words", word).orderBy("name")
                .limit(limit).get()
        }
        return _db.collection(userDb).orderBy("name").limit(limit).get()
    }

    fun loadUserListByRoleAndClass(
        type: String, classParams: String = "", keyword: String = "", limit: Long = 1000
    ): Task<QuerySnapshot> {
        if (keyword.isNotBlank()) {
            return _db.collection(userDb).whereEqualTo("role", type)
                .whereEqualTo("kelas", classParams.replace(".", "_"))
                .whereArrayContains("words", keyword.lowercase()).orderBy("name").limit(limit).get()
        }
        if (classParams.isNotBlank()) {
            return _db.collection(userDb).whereEqualTo("role", type)
                .whereEqualTo("kelas", classParams.replace(".", "_")).orderBy("name").limit(limit)
                .get()
        }
        return _db.collection(userDb).whereEqualTo("role", type).orderBy("name").limit(limit).get()
    }

    fun deleteUser() {
        _auth.currentUser?.delete()
    }

    fun signOut() {
        _auth.signOut()
    }

    fun loadBookList(
        selectedFilter: List<String> = emptyList(),
        online: Boolean = false,
        keyword: String = "",
        limit: Long = 50
    ): Task<QuerySnapshot> {
        val query = _db.collection(bookDb)
        if (keyword.isNotBlank()) {
            return if (selectedFilter.isNotEmpty()) query.whereIn("kategori", selectedFilter)
                .whereArrayContains("words", keyword).orderBy("judul").limit(limit)
                .get(source(online))
            else query.whereArrayContains("words", keyword).orderBy("judul").limit(limit)
                .get(source(online))
        }
        return if (selectedFilter.isNotEmpty()) query.whereIn("kategori", selectedFilter)
            .orderBy("judul").limit(limit).get(source(online))
        else query.orderBy("judul").limit(limit).get(source(online))
    }

    fun uploadImageToServer(path: String, file: File): UploadTask {
        return _storage.reference.child(path).putFile(Uri.fromFile(file))
    }

    fun submitBook(book: Book): Task<Void> {
        book.key = book.key ?: _db.collection(bookDb).document().id

        val batch = _db.batch()

        val bookRef = _db.collection(bookDb).document(book.key ?: "")
        val bookCountRef = _db.collection("counterDb").document("bookCounter")

        batch.set(bookRef, book)
        batch.update(bookCountRef, "count", FieldValue.increment(1))

        return batch.commit()
    }

    fun updateBook(book: Book) {
        _db.collection(bookDb).document(book.key ?: "").set(book)
    }

    fun loadBook(key: String): Task<DocumentSnapshot> {
        return _db.collection(bookDb).document(key).get()
    }

    fun submitPinjam(
        userPinjam: User?, datePinjam: Long, dateReturn: Long, bookList: MutableList<BookAdapter>
    ): Task<Void> {
        val batch = _db.batch()

        for (book in bookList) {
            val pinjam = PinjamBuku()
            val words = book.book.judul?.generateKeyword()
            words?.addAll(book.book.isbn?.generateKeyword() ?: emptyList())
            words?.addAll(userPinjam?.words ?: emptyList())
            pinjam.apply {
                key = _db.collection(pinjamBukuDb).document().id
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
            val ref = _db.collection(pinjamBukuDb).document(pinjam.key ?: "")
            val bookRef = _db.collection(bookDb).document(book.book.key ?: "")
            batch.set(ref, pinjam)
            batch.update(bookRef, "pinjam", FieldValue.increment(1))
        }

        return batch.commit()
    }

    fun loadPinjamBukuList(
        userKey: String? = null, bookKey: String? = null, keyword: String = "", limit: Long = 100
    ): Task<QuerySnapshot> {
        userKey?.let {
            return _db.collection(pinjamBukuDb).whereEqualTo("ukey", it)
                .orderBy("date", Query.Direction.DESCENDING).get()
        }
        bookKey?.let {
            return _db.collection(pinjamBukuDb).whereEqualTo("bid", it)
                .orderBy("date", Query.Direction.DESCENDING).get()
        }
        if (keyword.isNotBlank()) {
            return _db.collection(pinjamBukuDb).whereArrayContains("words", keyword.lowercase())
                .orderBy("date", Query.Direction.DESCENDING).limit(limit).get()
        }
        return _db.collection(pinjamBukuDb).orderBy("date", Query.Direction.DESCENDING).limit(limit)
            .get()
    }

    fun selesaikanPinjamBuku(data: PinjamBuku): Task<Void> {

        val denda =
            ((System.currentTimeMillis() - data.returnDate) / (24 * 3600)).roundUpToNearestThousand()

        val batch = _db.batch()

        val userRef = _db.collection(userDb).document(data.ukey ?: "").collection(
            pinjamBukuDb
        ).document(data.key ?: "")
        val pinjamRef = _db.collection(pinjamBukuDb).document(data.key ?: "")

        batch.delete(userRef)
        batch.update(
            pinjamRef, mapOf(
                "status" to selesai,
                "actualReturnDate" to System.currentTimeMillis(),
                "denda" to if (denda > 0) denda else 0
            )
        )
        batch.update(pinjamRef, "status", selesai)
        batch.update(pinjamRef, "actualReturnDate", System.currentTimeMillis())

        return batch.commit()
    }

    fun updateProfileImage(url: String): Task<Void> {
        return _db.collection(userDb).document(getUid()).update("url", url)
    }

    fun getBookByISBN(isbn: String): Task<QuerySnapshot> {
        return _db.collection(bookDb).whereEqualTo("isbn", isbn).get()
    }

    fun deleteBook(bookKey: String): Task<Void> {
        val batch = _db.batch()
        val bookRef = _db.collection(bookDb).document(bookKey)
        val countRef = _db.collection(counterDb).document(bookCountKey)

        batch.delete(bookRef)
        batch.update(countRef, "count", FieldValue.increment(-1))
        return batch.commit()
    }

    fun generateBookKey(): String {
        return _db.collection(bookDb).document().id
    }

    fun loadBookCounter(online: Boolean): Task<DocumentSnapshot> {
        return _db.collection(counterDb).document(bookCountKey).get(source(online))
    }

    fun addKelas(kelas: Kelas) {
        _db.collection(classDb).document(kelas.key).set(kelas)
    }

    fun getKelas(online: Boolean = false): Task<QuerySnapshot> {
        return _db.collection(classDb).whereEqualTo("active", true).orderBy("order")
            .get(if (online) Source.SERVER else Source.CACHE)
    }

    fun submitMenu(menu: HomeModel) {
        _db.collection(homeDb).document(menu.key).set(menu)
    }

    fun loadHomeMenu(online: Boolean): Task<QuerySnapshot> {
        return _db.collection(homeDb).orderBy("order")
            .get(if (online) Source.SERVER else Source.CACHE)
    }

    fun addArticle(article: Article): Task<Void> {
        article.key = _db.collection(articleDb).document().id
        return _db.collection(articleDb).document(article.key).set(article)
    }

    fun loadArticle(online: Boolean = false): Task<QuerySnapshot> {
        return _db.collection(articleDb).orderBy("postDate", Query.Direction.DESCENDING)
            .get(source(online))
    }

    fun loadSchoolProfile(online: Boolean): Task<DocumentSnapshot> {
        return _db.collection(schoolDb).document("profile").get(source(online))
    }

    fun submitPrestasi(items: MutableList<SchoolPrestasi>) {
        _db.collection(schoolDb).document("prestasi").update("items", items)
    }

    fun loadSchoolPrestasi(online: Boolean): Task<DocumentSnapshot> {
        return _db.collection(schoolDb).document("prestasi").get(source(online))
    }

    fun loadMapelByKelas(kelas: String): Task<QuerySnapshot> {
        return _db.collection(mapelDb).whereArrayContains("kelas", kelas).get()
    }

    fun submitMapel(mapel: Mapel) {
        mapel.key = _db.collection(mapelDb).document().id
        _db.collection(mapelDb).document(mapel.key).set(mapel)
    }

    fun submitJadwal(jadwal: Jadwal) {
        jadwal.key = _db.collection(jadwalDb).document().id
        _db.collection(jadwalDb).document(jadwal.key).set(jadwal)
    }

    fun loadJadwalByFilter(filter: String): Task<QuerySnapshot> {
        return _db.collection(jadwalDb).whereEqualTo("filter", filter.lowercase()).get()
    }

    fun isLogin(): Boolean {
        return _auth.currentUser != null
    }

    fun updateHomeMenu(addtionalMenus: MutableList<HomeMenu>) {
        _db.collection(homeDb).document("menu").update("items", addtionalMenus)
    }

    fun submitFacility(facility: SchoolFacilityModel) {
        _db.collection(schoolDb).document("fasilitas").set(facility)
    }

    fun getFasilitas(online: Boolean): Task<DocumentSnapshot> {
        return _db.collection(schoolDb).document("fasilitas").get(source(online))
    }

    fun getOrganisasi(online: Boolean): Task<DocumentSnapshot> {
        return _db.collection(schoolDb).document("organisasi").get(source(online))
    }

    fun setOrganisasi(organisasi: SchoolOrganisasiModel) {
        _db.collection(schoolDb).document("organisasi").set(organisasi)
    }

    fun submitExtra(extraModel: SchoolExtraModel) {
        _db.collection(schoolDb).document("extra").set(extraModel)
    }

    fun getExtra(online: Boolean): Task<DocumentSnapshot> {
        return _db.collection(schoolDb).document("extra").get(source(online))
    }

    fun loadGallery(online: Boolean): Task<QuerySnapshot> {
        return _db.collection(galleryDb).get(source(online))
    }

    fun submitGallery(gallery: Gallery): Task<Void> {
        gallery.key = _db.collection(galleryDb).document().id
        return _db.collection(galleryDb).document(gallery.key).set(gallery)
    }

    fun loadVisitors(online: Boolean): Task<QuerySnapshot> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return _db.collection(visitorDb).whereGreaterThan("date", calendar.timeInMillis)
            .orderBy("date", Query.Direction.DESCENDING).get(source(online))
    }

    fun submitVisitor(visitor: Kunjungan): Task<Void> {
        visitor.key = _db.collection(visitorDb).document().id

        val batch = _db.batch()
        val visitorRef = _db.collection(visitorDb).document(visitor.key)
        val counterRef = _db.collection(counterDb).document("libVisitor")

        batch.set(visitorRef, visitor)
        batch.update(
            counterRef, mapOf(
                "total_visitor" to FieldValue.increment(1),
                "total_daily.${
                    System.currentTimeMillis().toDateFormat("yyyy-MM-dd")
                }" to FieldValue.increment(1)
            )
        )
        return _db.collection(visitorDb).document(visitor.key).set(visitor)
    }

    fun searchUserByNis(nis: String): Task<QuerySnapshot> {
        return _db.collection(userDb).whereEqualTo("no_id", nis).get()
    }

}