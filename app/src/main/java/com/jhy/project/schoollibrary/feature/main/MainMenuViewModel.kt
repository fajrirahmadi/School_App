package com.jhy.project.schoollibrary.feature.main

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.capitalizeWord
import com.jhy.project.schoollibrary.model.Article
import com.jhy.project.schoollibrary.model.HomeMenu
import com.jhy.project.schoollibrary.model.HomeModel
import com.jhy.project.schoollibrary.model.Jadwal
import com.jhy.project.schoollibrary.model.Mapel
import com.jhy.project.schoollibrary.model.User
import com.jhy.project.schoollibrary.model.adapter.ArticleAdapter
import com.jhy.project.schoollibrary.model.adapter.BannerAdapter
import com.jhy.project.schoollibrary.model.adapter.HomeMenuAdapter
import com.jhy.project.schoollibrary.repository.FirebaseRepository
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(db: FirebaseRepository) : BaseViewModel(db) {

    val bannerAdapter = FastItemAdapter<BannerAdapter>()
    val homeMenuAdapter = FastItemAdapter<HomeMenuAdapter>()
    val articleAdapter = FastItemAdapter<ArticleAdapter>()

    fun onCreate(online: Boolean = false) {
        loadHomeMenu(online)
        loadArticle(online)
        loadUserData()
    }

    private fun loadHomeMenu(online: Boolean = false) {
        showLoading(bannerAdapter.adapterItemCount == 0)
        db.loadHomeMenu(online).addOnCompleteListener {
            if (it.isSuccessful) {
                val responses = it.result.toObjects(HomeModel::class.java)
                if (responses.isEmpty() && !online) {
                    loadHomeMenu(true)
                    return@addOnCompleteListener
                }
                showHomeMenu(responses)
            }
            postDelayed { showLoading(false) }
        }
    }

    private fun loadArticle(online: Boolean = false) {
        db.loadArticle(online).addOnCompleteListener {
            if (it.isSuccessful) {
                val responses = it.result.toObjects(Article::class.java)
                if (responses.isEmpty() && !online) {
                    loadArticle(true)
                    return@addOnCompleteListener
                }
                showArticle(responses)
            }
        }
    }

    private fun showHomeMenu(responses: List<HomeModel>) {
        responses.forEach {
            when (it.key) {
                "banner" -> updateBanner(it.items)
                "menu" -> updateMenu(it.items)
            }
        }
    }

    private fun updateMenu(menus: List<HomeMenu>) {
        homeMenuAdapter.clear()
        menus.forEach {
            homeMenuAdapter.add(HomeMenuAdapter(it))
        }
        homeMenuAdapter.notifyAdapterDataSetChanged()
    }

    private fun updateBanner(banners: List<HomeMenu>) {
        bannerAdapter.clear()
        banners.forEach {
            bannerAdapter.add(BannerAdapter(it))
        }
        bannerAdapter.notifyAdapterDataSetChanged()
    }

    private fun showArticle(articles: List<Article>) {
        articleAdapter.clear()
        articles.forEach {
            articleAdapter.add(ArticleAdapter(it))
        }
        articleAdapter.notifyAdapterDataSetChanged()
    }

    fun addJadwal() {
        val jadwals = "senin;07.00-08.00;;;Upacara\n" +
                "senin;08.00-08.40;Aditya Hermawan, S.Pd;43;MM\n" +
                "senin;08.40-09.20;Aditya Hermawan, S.Pd;43;MM\n" +
                "senin;09.20-10.00;Sri Hartini Dwiyanti P, S.Psd;10;B.INDO\n" +
                "senin;10.00-10.30;;;Istirahat\n" +
                "senin;10.30-11.10;Sri Hartini Dwiyanti P, S.Psd;10;B.INDO\n" +
                "senin;11.10-11.50;Sri Hartini Dwiyanti P, S.Psd;10;B.INDO\n" +
                "senin;11.50-12.20;;;P5\n" +
                "senin;12.20-13.00;;;Ishoma\n" +
                "senin;13.00-13.50;;;P5\n" +
                "selasa;07.00-07.30;;;Pembiasaan/Literasi\n" +
                "selasa;07.30-08.10;Desmawarni, S.Pd;35;IPS\n" +
                "selasa;08.10-08.50;Desmawarni, S.Pd;35;IPS\n" +
                "selasa;08.50-09.30;Desmawarni, S.Pd;35;IPS\n" +
                "selasa;09.30-10.10;Tuti Novia, S.Pd;24;B.ING\n" +
                "selasa;10.10-10.40;;;Istirahat\n" +
                "selasa;10.40-11.20;Tuti Novia, S.Pd;24;B.ING\n" +
                "selasa;11.20-12.00;Tuti Novia, S.Pd;24;B.ING\n" +
                "selasa;12.00-12.20;;;P5\n" +
                "selasa;12.20-13.00;;;Ishoma\n" +
                "selasa;13.00-14.00;;;P5\n" +
                "rabu;07.00-07.30;;;Pembiasaan/Literasi\n" +
                "rabu;07.30-08.10;Rahmawirda, S.Pd;42;B.ING\n" +
                "rabu;08.10-08.50;Rahmawirda, S.Pd;42;B.ING\n" +
                "rabu;08.50-09.30;Aditya Hermawan, S.Pd;43;MM\n" +
                "rabu;09.30-10.10;Aditya Hermawan, S.Pd;43;MM\n" +
                "rabu;10.10-10.40;;;Istirahat\n" +
                "rabu;10.40-11.20;Herni Nilarita, S.Pd.I;34;PAI\n" +
                "rabu;11.20-12.00;Herni Nilarita, S.Pd.I;34;PAI\n" +
                "rabu;12.00-12.20;Reno Yuni, S, Si;17;IPA\n" +
                "rabu;12.20-13.00;;;Ishoma\n" +
                "rabu;13.00-14.00;Reno Yuni, S, Si;17;IPA\n" +
                "kamis;07.00-07.30;;;Pembiasaan/Literasi\n" +
                "kamis;07.30-08.10;Reno Yuni, S, Si;17;IPA\n" +
                "kamis;08.10-08.50;Reno Yuni, S, Si;17;IPA\n" +
                "kamis;08.50-09.30;Septania Caesaria S, S.Pd;40;P.Kn\n" +
                "kamis;09.30-10.10;Septania Caesaria S, S.Pd;40;P.Kn\n" +
                "kamis;10.10-10.40;;;Istirahat\n" +
                "kamis;10.40-11.20;;;Pramuka\n" +
                "kamis;11.20-12.00;;;Pramuka\n" +
                "kamis;12.00-12.20;Prassetyo Fajar Gumilang, S.Pd;39;S.Budaya\n" +
                "kamis;12.20-13.00;;;Ishoma\n" +
                "kamis;13.00-14.00;Prassetyo Fajar Gumilang, S.Pd;39;S.Budaya\n" +
                "jumat;07.00-08.00;;;Kultum\n" +
                "jumat;08.00-08.40;Sri Hartini Dwiyanti P, S.Psd;10;B.INDO\n" +
                "jumat;08.40-09.20;Sri Hartini Dwiyanti P, S.Psd;10;B.INDO\n" +
                "jumat;09.20-10.00;;;P5\n" +
                "jumat;10.00-10.20;;;Istirahat\n" +
                "jumat;10.20-11.00;;;P5\n" +
                "jumat;11.00-11.40;;;P5\n" +
                "sabtu;07.00-08.00;;;Tahfidz/Senam/Goro\n" +
                "sabtu;08.00-08.40;Junaidi, S, Sos;28;Penjas\n" +
                "sabtu;08.40-09.20;Junaidi, S, Sos;28;Penjas\n" +
                "sabtu;09.20-10.00;;;P5\n" +
                "sabtu;10.00-10.30;;;Istirahat\n" +
                "sabtu;10.30-11.10;;;P5\n" +
                "sabtu;11.10-12.10;;;P5\n" +
                "sabtu;12.10-12.50;;;Ekstrakurikuler"

        var counter = 1
        var day = ""

        jadwals.split("\n").forEach {
            val jadwal = it.split(";")
            val hari = jadwal[0]
            val jam = jadwal[1]
            val guru = jadwal[2]
            val kode = jadwal[3]
            val mapel = jadwal[4]
            val kelas = "vii.8"
            val tahun = "2023/2024"
            if (day == hari) {
                counter++
            } else {
                day = hari
                counter = 1
            }
            val jadwalModel = Jadwal(
                "",
                "${tahun}_${hari}_${kelas}",
                tahun,
                hari,
                kelas,
                mapel,
                guru,
                counter,
                jam,
                kode
            )
            db.submitJadwal(jadwalModel)
        }
    }

    fun updateKelas() {
        db.loadUserByClass(listOf("VIII.1", "VIII.2")).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toObjects(User::class.java).forEach {
                    db.updateUserByNis(
                        it.key ?: "", mapOf(
                            "kelas" to (it.kelas?.replace(".", "_") ?: "")
                        )
                    )
                }
            }
        }
        val key = "2023-2024_1_vii.1_41"
    }

    fun createJadwal() {
        val dataset =
            "vii.1,20,20,20,,45,45,,,,,39,39,42,42,,40,40,,,,,8,8,8,23,,23,23,2,,2,,45,45,28,28,,,,15,,15,,8,8,,,,,,15,15,,,,\n" +
                    "vii.2,8,8,15,,15,20,,,,,42,42,45,45,,34,34,,,,,15,15,45,45,,40,40,28,,28,,20,20,23,8,,,,8,,8,,39,39,,,,,,23,23,,,,\n" +
                    "vii.3,15,15,8,,8,8,,,,,20,20,20,23,,23,23,,,,,45,45,40,40,,28,28,15,,15,,8,8,39,39,,,,34,,34,,42,42,,,,,,45,45,,,,\n" +
                    "vii.4,45,45,42,,42,23,,,,,8,8,8,20,,20,20,,,,,23,23,15,15,,45,45,8,,8,,39,39,15,15,,,,40,,40,,28,28,,,,,,34,34,,,,\n" +
                    "vii.5,23,23,23,,34,34,,,,,40,40,39,39,,15,15,,,,,28,28,20,20,,20,10,10,,10,,15,15,42,42,,,,45,,45,,45,45,,,,,,10,10,,,,\n" +
                    "vii.6,24,11,11,,28,28,,,,,10,10,34,34,,39,39,,,,,20,20,42,42,,16,16,11,,11,,10,10,10,20,,,,24,,24,,40,40,,,,,,16,16,,,,\n" +
                    "vii.7,17,17,43,,43,35,,,,,24,24,24,10,,10,10,,,,,17,17,39,39,,42,42,40,,40,,28,28,35,35,,,,10,,10,,34,34,,,,,,43,43,,,,\n" +
                    "vii.8,43,43,10,,10,10,,,,,35,35,35,24,,24,24,,,,,42,42,43,43,,34,34,17,,17,,17,17,40,40,,,,39,,39,,10,10,,,,,,28,28,,,,\n" +
                    "viii.1,3,3,6,,6,6,,,,,4,4,13,13,,37,37,,,,,21,21,21,25,,25,25,18,,18,,,,33,33,,29,29,6,,6,,13,13,,,,,,18,18,,,,\n" +
                    "viii.2,21,21,21,,33,33,,,,,6,6,6,25,,25,25,,,,,18,18,36,36,,3,3,37,,37,,,,29,29,,18,18,13,,13,,6,6,,,,,,13,13,,,,\n" +
                    "viii.3,18,18,13,,13,21,,,,,21,21,25,6,,6,6,,,,,13,13,29,29,,37,37,36,,36,,,,18,18,,25,25,3,,3,,33,33,,,,,,6,6,,,,\n" +
                    "viii.4,6,6,25,,25,25,,,,,13,13,36,36,,29,29,,,,,37,37,13,13,,21,21,3,,3,,,,21,6,,6,6,18,,18,,18,18,,,,,,33,33,,,,\n" +
                    "viii.5,25,25,9,,9,9,,,,,33,33,18,18,,21,21,,,,,3,3,25,21,,18,18,13,,13,,,,13,13,,37,37,9,,9,,36,36,,,,,,29,29,,,,\n" +
                    "viii.6,37,37,3,,3,24,,,,,17,17,9,9,,9,35,,,,,24,24,33,33,,43,43,29,,29,,,,17,17,,9,9,36,,36,,43,43,,,,,,35,35,,,,\n" +
                    "viii.7,35,35,35,,36,36,,,,,3,3,29,29,,43,43,,,,,9,9,9,24,,24,24,33,,33,,,,37,37,,17,17,43,,43,,17,17,,,,,,9,9,,,,\n" +
                    "viii.8,27,27,24,,26,26,,,,,9,9,43,43,,3,3,,,,,35,35,35,17,,17,9,9,,9,,,,36,36,,43,43,33,,33,,24,24,,,,,,17,17,,,,\n" +
                    "ix.1,4,4,4,,41,41,14,,14,,23,23,27,27,,27,7,7,,7,,11,11,11,7,,7,7,19,,19,,23,23,1,1,,1,30,,,,,19,19,26,,26,26,,14,14,14,,11,11\n" +
                    "ix.2,7,7,7,,11,11,19,,19,,14,14,14,1,,1,1,41,,41,,22,22,26,26,,26,4,4,,4,,19,19,30,27,,27,27,,,,,11,11,11,,22,22,,7,7,7,,14,14\n" +
                    "ix.3,19,19,27,,27,27,22,,22,,7,7,7,14,,14,14,19,,19,,14,14,22,22,,30,1,1,,1,,4,4,4,7,,7,7,,,,,12,12,12,,41,41,,26,26,26,,12,12\n" +
                    "ix.4,22,22,14,,14,14,41,,41,,19,19,12,12,,30,26,26,,26,,7,7,7,12,,12,12,14,,14,,27,27,27,4,,4,4,,,,,22,22,7,,7,7,,19,19,1,,1,1\n" +
                    "ix.5,26,26,26,,19,19,12,,12,,5,5,5,19,,19,4,4,,4,,5,5,5,14,,14,14,22,,22,,12,12,12,2,,2,2,,,,,41,41,30,,14,14,,27,27,27,,22,22\n" +
                    "ix.6,16,16,22,,22,4,4,,4,,2,2,2,41,,41,5,5,,5,,12,12,12,19,,19,26,26,,26,,30,16,16,16,,12,12,,,,,27,27,27,,19,19,,22,22,5,,5,5\n" +
                    "ix.7,46,46,46,,16,16,16,,30,,26,26,26,22,,22,12,12,,12,,27,27,27,2,,2,2,21,,21,,22,22,41,41,,16,16,,,,,21,21,46,,46,46,,12,12,4,,4,4"

        val roster = dataset.split("\n")

        updateDataMapel(1, roster)
    }

    fun updateDataMapel(counter: Int = 1, rosters: List<String>) {
        if (counter == 48) {
            showLoading(false)
            return
        }
        showLoading()
        db.loadUserByKode(counter.toString()).addOnCompleteListener {
            if (it.isSuccessful && !it.result.isEmpty) {
                val user = it.result.toObjects(User::class.java).first()
                val kelas = mutableMapOf<String, Boolean>()
                rosters.forEach {
                    val codes = it.split(",")
                    val parent = codes.first()
                    val found = codes.contains(counter.toString())
                    kelas["2023_2024_${parent.lowercase()}"] = found
                }

                val nama =
                    user.mapel?.lowercase()?.replace("guru mapel", "")?.capitalizeWord()
                        ?: ""

                val mapel = Mapel(
                    "2023_2024_1_$counter",
                    nama,
                    user.name ?: "",
                    counter.toString(),
                    user.no_id ?: "",
                    kelas.filter { it.value }.keys.toList(),
                    "2023/2024",
                    "1"
                )

                db.submitMapel(mapel).addOnCompleteListener {
                    if (it.isSuccessful) {
                        updateDataMapel(counter + 1, rosters)
                    }
                }
            } else {
                updateDataMapel(counter + 1, rosters)
            }
        }
    }

    fun migrateUser() {
        val currentKey = "xPjJWp6XXpgyPdTJfyIEt4NWXVC2"
        val newKey = "0jUuhSkSMQg6gKiFxAvn50S4dEh2"

        showLoading()
        db.loadUser(currentKey, true).continueWith {
            it.result.toObject(User::class.java)?.let {
                it.key = newKey
                return@continueWith db.updateUser(it, currentKey = currentKey)
            }
        }.addOnCompleteListener {
            postDelayed { dismissLoading() }
        }
    }

    fun migrateAlumni() {
        db.loadUserList().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toObjects(User::class.java).forEach {
                    db.updateUserByNis(
                        it.key ?: "",
                        mapOf(
                            "kelas" to (it.kelas?.replace("alumni", "ALUMNI") ?: "")
                        )
                    )
                }
            }
        }
    }

}