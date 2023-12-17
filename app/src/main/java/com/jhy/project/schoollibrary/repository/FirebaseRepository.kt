package com.jhy.project.schoollibrary.repository

import android.content.Context
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.jhy.project.schoollibrary.extension.toDateFormat
import com.jhy.project.schoollibrary.model.Absence
import com.jhy.project.schoollibrary.model.Article
import com.jhy.project.schoollibrary.model.Event
import com.jhy.project.schoollibrary.model.Gallery
import com.jhy.project.schoollibrary.model.HomeMenu
import com.jhy.project.schoollibrary.model.HomeModel
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Kelas
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.SchoolExtraModel
import com.jhy.project.schoollibrary.model.SchoolFacilityModel
import com.jhy.project.schoollibrary.model.SchoolOrganisasiModel
import com.jhy.project.schoollibrary.model.SchoolPrestasi
import com.jhy.project.schoollibrary.model.Visitor
import com.jhy.project.schoollibrary.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
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
class FirebaseRepository @Inject constructor(@ApplicationContext var context: Context) {

    val auth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage

    fun isConnected(): Boolean {
        return NetworkUtils.isNetworkConnected(context)
    }

    fun getUid(): String {
        return auth.uid ?: "invalid"
    }

    fun uploadImageToServer(path: String, file: File): UploadTask {
        return storage.reference.child(path).putFile(Uri.fromFile(file))
    }

    fun updateProfileImage(url: String): Task<Void> {
        return db.collection(userDb).document(getUid()).update("url", url)
    }

    fun getBookByISBN(isbn: String): Query {
        return db.collection(bookDb).whereEqualTo("isbn", isbn)
    }

    fun deleteBook(bookKey: String): Task<Void> {
        val batch = db.batch()
        val bookRef = db.collection(bookDb).document(bookKey)
        val countRef = db.collection(counterDb).document(bookCountKey)

        batch.delete(bookRef)
        batch.update(countRef, "count", FieldValue.increment(-1))
        return batch.commit()
    }

    fun generateBookKey(): String {
        return db.collection(bookDb).document().id
    }

    fun loadBookCounter(): DocumentReference {
        return db.collection(counterDb).document(bookCountKey)
    }

    fun addKelas(kelas: Kelas) {
        db.collection(classDb).document(kelas.key).set(kelas)
    }

    fun getKelas(): Query {
        return db.collection(classDb).whereEqualTo("active", true).orderBy("order")
    }

    fun submitMenu(menu: HomeModel) {
        db.collection(homeDb).document(menu.key).set(menu)
    }

    fun loadHomeMenu(): Query {
        return db.collection(homeDb).orderBy("order")
    }

    fun addArticle(article: Article): Task<Void> {
        article.key = db.collection(articleDb).document().id
        return db.collection(articleDb).document(article.key).set(article)
    }

    fun loadArticle(): Query {
        return db.collection(articleDb).orderBy("postDate", Query.Direction.DESCENDING)
    }

    fun loadSchoolProfile(): DocumentReference {
        return db.collection(schoolDb).document("profile")
    }

    fun submitPrestasi(items: MutableList<SchoolPrestasi>) {
        db.collection(schoolDb).document("prestasi").update("items", items)
    }

    fun loadSchoolPrestasi(): DocumentReference {
        return db.collection(schoolDb).document("prestasi")
    }

    fun loadMapelByKelas(kelas: String): Query {
        return db.collection(mapelDb).whereArrayContains("kelas", kelas)
    }

    fun submitMapel(mapel: Mapel): Task<Void> {
        return db.collection(mapelDb).document(mapel.key).set(mapel)
    }

    fun submitJadwal(jadwal: Jadwal) {
        jadwal.key = db.collection(jadwalDb).document().id
        db.collection(jadwalDb).document(jadwal.key).set(jadwal)
    }

    fun loadJadwalByFilter(filter: String): Query {
        return db.collection(jadwalDb).whereEqualTo("filter", filter.lowercase())
    }

    fun isLogin(): Boolean {
        return auth.currentUser != null
    }

    fun updateHomeMenu(addtionalMenus: MutableList<HomeMenu>) {
        db.collection(homeDb).document("menu").update("items", addtionalMenus)
    }

    fun submitFacility(facility: SchoolFacilityModel) {
        db.collection(schoolDb).document("fasilitas").set(facility)
    }

    fun getFasilitas(): DocumentReference {
        return db.collection(schoolDb).document("fasilitas")
    }

    fun getOrganisasi(): DocumentReference {
        return db.collection(schoolDb).document("organisasi")
    }

    fun setOrganisasi(organisasi: SchoolOrganisasiModel) {
        db.collection(schoolDb).document("organisasi").set(organisasi)
    }

    fun submitExtra(extraModel: SchoolExtraModel) {
        db.collection(schoolDb).document("extra").set(extraModel)
    }

    fun getExtra(): DocumentReference {
        return db.collection(schoolDb).document("extra")
    }

    fun loadGallery(): CollectionReference {
        return db.collection(galleryDb)
    }

    fun submitGallery(gallery: Gallery): Task<Void> {
        gallery.key = db.collection(galleryDb).document().id
        return db.collection(galleryDb).document(gallery.key).set(gallery)
    }

    fun loadVisitors(): Query {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return db.collection(visitorDb).whereGreaterThan("date", calendar.timeInMillis)
            .orderBy("date", Query.Direction.DESCENDING)
    }

    fun submitVisitor(visitor: Visitor): Task<Void> {
        visitor.key = db.collection(visitorDb).document().id
        val countKey = System.currentTimeMillis().toDateFormat("yyyy-MM")

        val batch = db.batch()
        val visitorRef = db.collection(visitorDb).document(visitor.key)
        val counterRef = db.collection(visitorCountDb).document(countKey)
        val userRef = db.collection(userDb).document(visitor.userKey ?: "unknown")

        batch.set(visitorRef, visitor)
        batch.update(
            counterRef, mapOf(
                "totalVisitor" to FieldValue.increment(1), "totalDaily.${
                    System.currentTimeMillis().toDateFormat("dd")
                }" to FieldValue.increment(1)
            )
        )
        batch.update(
            userRef, mapOf(
                "totalVisit" to FieldValue.increment(1)
            )
        )
        return batch.commit()
    }

    fun searchUserByNis(nis: String): Query {
        return db.collection(userDb).whereEqualTo("no_id", nis)
    }

    fun loadDailyVisitorCounter(
        date: Long = System.currentTimeMillis()
    ): DocumentReference {
        val countKey = date.toDateFormat("yyyy-MM")
        return db.collection(visitorCountDb).document(countKey)
    }

    fun removeVisitRecord(visitor: Visitor): Task<Void> {
        val countKey = visitor.date.toDateFormat("yyyy-MM")
        val batch = db.batch()
        val visitorRef = db.collection(visitorDb).document(visitor.key)
        val counterRef = db.collection(visitorCountDb).document(countKey)

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

    fun loadJadwalByCode(day: String, kode: String): Query {
        return db.collection(jadwalDb).whereEqualTo("day", day.lowercase())
            .whereEqualTo("kode", kode).orderBy("jam")
    }

    fun loadUserByKode(kode: String): Query {
        return db.collection(userDb).whereEqualTo("kode", kode)
    }

    fun loadMapelByCode(key: String): DocumentReference {
        return db.collection(mapelDb).document(key)
    }

    fun submitAbsence(absence: Absence): Task<Void> {
        absence.key = absence.key.ifEmpty { db.collection(absenceDb).document().id }
        return db.collection(absenceDb).document(absence.key).set(absence)
    }

    fun loadAbsence(filter: String): Query {
        return db.collection(absenceDb).whereArrayContains("words", filter)
    }

    fun loadAbsenceByKey(key: String): DocumentReference {
        return db.collection(absenceDb).document(key)
    }

    fun loadAsesment(filter: String): Task<QuerySnapshot> {
        return db.collection(asesmenDb).whereArrayContains("words", filter).get()
    }

    fun loadEventMyMonth(filter: String): Query {
        return db.collection(eventDb).whereArrayContains("filters", filter)
    }

    fun submitEvent(event: Event): Task<Void> {
        event.key = db.collection(eventDb).document().id
        return db.collection(eventDb).document(event.key).set(event)
    }

    fun updateArticle(key: String, mapOf: Map<String, String>) {
        db.collection(articleDb).document(key).update(mapOf)
    }

    fun generateArticleKey(): String {
        return db.collection(articleDb).document().id
    }

    fun submitArticle(article: Article): Task<Void> {
        return db.collection(articleDb).document(article.key).set(article)
    }

}