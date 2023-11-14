package com.jhy.project.schoollibrary.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.jhy.project.schoollibrary.extension.generateKeyword
import com.jhy.project.schoollibrary.extension.roundUpToNearestThousand
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.Absence
import com.jhy.project.schoollibrary.model.Article
import com.jhy.project.schoollibrary.model.Book
import com.jhy.project.schoollibrary.model.Event
import com.jhy.project.schoollibrary.model.Gallery
import com.jhy.project.schoollibrary.model.HomeMenu
import com.jhy.project.schoollibrary.model.HomeModel
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.PinjamBuku
import com.jhy.project.schoollibrary.model.SchoolExtraModel
import com.jhy.project.schoollibrary.model.SchoolFacilityModel
import com.jhy.project.schoollibrary.model.SchoolOrganisasiModel
import com.jhy.project.schoollibrary.model.SchoolPrestasi
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.Visitor
import com.jhy.project.schoollibrary.model.adapter.BookAdapter
import com.jhy.project.schoollibrary.model.selesai
import com.jhy.project.schoollibrary.model.toKelasRequest
import java.io.File
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

const val userDb = "user"
const val accountDb = "accountDb"
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
const val visitorCountDb = "visitorCountDb"
const val absenceDb = "absenceDb"
const val asesmenDb = "asesmenDb"
const val eventDb = "eventDb"

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

    fun updateUser(user: User, pw: String? = null, currentKey: String? = null): Task<Void> {
        val batch = _db.batch()
        val userRef = _db.collection(userDb).document(user.key ?: "")
        val authRef = _db.collection(userDb).document(user.key ?: "").collection("account")
            .document("account01")

        batch.set(userRef, user)
        pw?.let {
            batch.set(
                authRef, mapOf(
                    "email" to user.email, "password" to pw
                )
            )
        }
        currentKey?.let {
            val oldUserRef = _db.collection(userDb).document(it)
            batch.delete(oldUserRef)
        }

        return batch.commit()
    }

    fun loadUser(key: String? = null, online: Boolean): Task<DocumentSnapshot> {
        return _db.collection(userDb).document(key ?: getUid()).get(source(online))
    }

    fun loadUserList(word: String = "", limit: Long = 50): Task<QuerySnapshot> {
        return _db.collection(userDb).whereEqualTo("role", "alumni").get()
        if (word.isNotBlank()) {
            return _db.collection(userDb)
                .whereArrayContains("words", word)
                .orderBy("name")
                .limit(limit).get()
        }
        return _db.collection(userDb).orderBy("name").limit(limit).get()
    }

    fun loadUserListByRoleAndClass(
        type: String, classParams: String = "", keyword: String = "", limit: Long = 1000
    ): Task<QuerySnapshot> {
        if (keyword.isNotBlank()) {
            if (classParams.isNotEmpty()) {
                return _db.collection(userDb).whereEqualTo("role", type)
                    .whereEqualTo("kelas", classParams.toKelasRequest())
                    .whereArrayContains("words", keyword.lowercase()).orderBy("name").limit(limit)
                    .get()
            }
            return _db.collection(userDb).whereEqualTo("role", type)
                .whereArrayContains("words", keyword.lowercase()).orderBy("name").limit(limit).get()
        }
        if (classParams.isNotBlank()) {
            return _db.collection(userDb).whereEqualTo("role", type)
                .whereEqualTo("kelas", classParams.toKelasRequest())
                .orderBy("name")
                .limit(limit)
                .get()
        }
        return _db.collection(userDb).whereEqualTo("role", type).orderBy("name").limit(limit).get()
    }

    fun loadUserByClass(listOfClass: List<String>): Task<QuerySnapshot> {
        return _db.collection(userDb).whereIn("kelas", listOfClass).get()
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

    fun submitMapel(mapel: Mapel): Task<Void> {
        return _db.collection(mapelDb).document(mapel.key).set(mapel)
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

    fun submitVisitor(visitor: Visitor): Task<Void> {
        visitor.key = _db.collection(visitorDb).document().id
        val countKey = System.currentTimeMillis().toDateFormat("yyyy-MM")

        val batch = _db.batch()
        val visitorRef = _db.collection(visitorDb).document(visitor.key)
        val counterRef = _db.collection(visitorCountDb).document(countKey)

        batch.set(visitorRef, visitor)
        batch.update(
            counterRef, mapOf(
                "totalVisitor" to FieldValue.increment(1), "totalDaily.${
                    System.currentTimeMillis().toDateFormat("dd")
                }" to FieldValue.increment(1)
            )
        )
        return batch.commit()
    }

    fun searchUserByNis(nis: String): Task<QuerySnapshot> {
        return _db.collection(userDb).whereEqualTo("no_id", nis).get()
    }

    fun loadDailyVisitorCounter(
        online: Boolean, date: Long = System.currentTimeMillis()
    ): Task<DocumentSnapshot> {
        val countKey = date.toDateFormat("yyyy-MM")
        return _db.collection(visitorCountDb).document(countKey).get(source(online))
    }

    fun updateUserByNis(key: String, updatedData: Map<String, String>): Task<Void> {
        return _db.collection(userDb).document(key).update(updatedData)
    }

    fun getUserByNis(nis: String): Task<QuerySnapshot> {
        return _db.collection(userDb).whereEqualTo("no_id", nis).get()
    }

    fun removeVisitRecord(visitor: Visitor): Task<Void> {
        val countKey = visitor.date.toDateFormat("yyyy-MM")
        val batch = _db.batch()
        val visitorRef = _db.collection(visitorDb).document(visitor.key)
        val counterRef = _db.collection(visitorCountDb).document(countKey)

        batch.delete(visitorRef)
        batch.update(
            counterRef, mapOf(
                "totalVisitor" to FieldValue.increment(-1), "totalDaily.${
                    System.currentTimeMillis().toDateFormat("dd")
                }" to FieldValue.increment(-1)
            )
        )
        return batch.commit()
    }

    fun loadJadwalByCode(day: String, kode: String): Task<QuerySnapshot> {
        return _db.collection(jadwalDb).whereEqualTo("day", day.lowercase())
            .whereEqualTo("kode", kode).orderBy("jam").get()
    }

    fun loadUserByKode(kode: String): Task<QuerySnapshot> {
        return _db.collection(userDb).whereEqualTo("kode", kode).get()
    }

    fun loadMapelByCode(key: String): Task<DocumentSnapshot> {
        return _db.collection(mapelDb).document(key).get()
    }

    fun submitAbsence(absence: Absence): Task<Void> {
        absence.key = absence.key.ifEmpty { _db.collection(absenceDb).document().id }
        return _db.collection(absenceDb).document(absence.key).set(absence)
    }

    fun loadAbsence(filter: String): Task<QuerySnapshot> {
        return _db.collection(absenceDb).whereArrayContains("words", filter).get()
    }

    fun loadAbsenceByKey(key: String): Task<DocumentSnapshot> {
        return _db.collection(absenceDb).document(key).get()
    }

    fun loadAsesment(filter: String): Task<QuerySnapshot> {
        return _db.collection(asesmenDb).whereArrayContains("words", filter).get()
    }

    fun loadEventMyMonth(filter: String): Task<QuerySnapshot> {
        return _db.collection(eventDb).whereArrayContains("filters", filter).get()
    }

    fun submitEvent(event: Event): Task<Void> {
        event.key = _db.collection(eventDb).document().id
        return _db.collection(eventDb).document(event.key).set(event)
    }

    fun updateArticle(key: String, mapOf: Map<String, String>) {
        _db.collection(articleDb).document(key).update(mapOf)
    }

}