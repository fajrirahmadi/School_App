package com.jhy.project.schoollibrary.feature.main

import com.jhy.project.schoollibrary.base.BaseViewModel
import com.jhy.project.schoollibrary.extension.capitalize
import com.jhy.project.schoollibrary.extension.generateKeyword
import com.jhy.project.schoollibrary.model.*
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

    fun addArticle() {
        val article = Article(
            "",
            "Kunci Sukses Seorang Pelajar: Panduan Menuju Prestasi",
            "<p>Sukses sebagai seorang pelajar tidak hanya tentang memiliki kecerdasan yang tinggi. Ini tentang pengembangan keterampilan dan sikap yang mendukung pencapaian tujuan pendidikan Anda. Berikut adalah beberapa kunci sukses seorang pelajar:" +
                    "</p><p><b>" +
                    "Motivasi yang Kuat</b>: Motivasi adalah mesin penggerak yang akan membantu Anda menghadapi tantangan dan menjalani proses belajar dengan tekun. Temukan alasan yang kuat mengapa Anda ingin sukses dalam pendidikan Anda." +
                    "</p><p><b>" +
                    "Manajemen Waktu yang Efektif</b>: Pelajari bagaimana mengatur waktu Anda dengan baik. Buat jadwal studi yang terstruktur, dan jangan lupakan waktu untuk istirahat dan rekreasi." +
                    "</p><p><b>" +
                    "Disiplin yang Konsisten</b>: Konsistensi dalam belajar adalah kunci. Jangan hanya belajar ketika ada ujian, tetapi pelajari materi secara teratur." +
                    "</p><p><b>" +
                    "Kemampuan Belajar yang Efektif</b>: Kenali cara belajar Anda sendiri. Apakah Anda seorang pembelajar visual, auditori, atau kinestetik? Gunakan metode yang sesuai untuk meningkatkan retensi informasi." +
                    "</p><p><b>" +
                    "Kerja Keras dan Ketekunan</b>: Tidak ada pengganti untuk usaha keras. Teruslah bekerja untuk mencapai tujuan Anda, bahkan ketika hal itu sulit." +
                    "</p><p><b>" +
                    "Kolaborasi dan Bantuan</b>: Jangan ragu untuk mencari bantuan ketika Anda mengalami kesulitan. Diskusi dengan teman sekelas, guru, atau tutor dapat memberikan wawasan tambahan." +
                    "</p><p><b>" +
                    "Keseimbangan Kehidupan</b>: Jangan lupakan pentingnya keseimbangan. Pastikan Anda merawat kesehatan fisik dan mental Anda serta memberikan waktu untuk kegiatan di luar belajar." +
                    "</p><p><b>" +
                    "Tujuan yang Jelas</b>: Tentukan tujuan pendidikan Anda dengan jelas. Mengetahui apa yang Anda ingin capai akan memberikan arah dan motivasi." +
                    "</p><p>" +
                    "Jadi, sukses sebagai seorang pelajar melibatkan kombinasi dari faktor-faktor ini. Ingatlah bahwa setiap individu unik, dan Anda dapat mengadaptasi kunci-kunci ini sesuai dengan kebutuhan dan gaya belajar Anda sendiri. Dengan komitmen dan upaya, Anda dapat meraih prestasi yang luar biasa dalam pendidikan Anda." +
                    "</p>",
            "Admin",
            System.currentTimeMillis(),
            1,
            "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/article%2Fkunci_kesuksesan.png?alt=media&token=58158259-4471-4b59-b742-217863d99d6b"
        )

        db.addArticle(article)
    }

    fun addPrestasi() {
        val prestasies = "Tahun 2011,Juara 1 Menulis Cerpen Tingkat Provinsi se-Sumatera Barat\n" +
                "Tahun 2012,Juara 1 Olimpiade IPS se-Provinsi Sumatera Barat\n" +
                "Tahun 2012,Juara 1 Olimpiade Fisika se-Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Olimpiade Biologi se-Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Menulis Cerpen se-Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Cipta dan Baca Puisi se-Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Tari dalam FLS2N se- Provinsi Sumatera Barat\n" +
                "Tahun 2012,Juara 1 Karate se-Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 2 Olimpiade Matematika se-Kab. Pesisir Selatan\n" +
                "Tahun 2012,Finalis Parsiad se-Indonesia\n" +
                "Tahun 2012,Juara 3 Matematika Batik Biru di SMA Sumatera Barat\n" +
                "Tahun 2012,Juara 2 Cerdas Cermat Siaga Bencana tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2012,Juara 1 Kreatif Games KSBS tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2012,Juara 2 lomba Festival Band tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2012,Juara 1 Lomba Tari FLS2N tingkat Prov. Sumatera Barat\n" +
                "Tahun 2012,Juara 1 OSN IPS tingkat Prov. Sumatera Barat\n" +
                "Tahun 2012,Juara 1 OSN Biologi tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 OSN Fisika tingkat kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 OSN Matematika tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Solo Song tingkat kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Cipta Puisi\n" +
                "Tahun 2012,Juara 1 Story Telling tingkat kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Melukis tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Renang tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Karya Ilmiah tingkat kab. Pesisir Selatan\n" +
                "Tahun 2012,Juara 1 Karate tingkat kab. Pesisir Selatan\n" +
                "Tahun 2013,Juara 1 OSN IPS tingkat Prov. Sumatera Barat\n" +
                "Tahun 2013,Juara 1 OSN Biologi tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2013,Juara 3 OSN Matematika tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2013,Juara 1 Pidato Bahasa Inggris tingkat Prov. Sumatera Barat\n" +
                "Tahun 2013,Juara 2 OSN Fisika tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2013,Juara 1 OSN IPS tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2013,Juara 3 Tari FLS2N tingkat Prov. Sumatera Barat\n" +
                "Tahun 2013,Juara 3 lagu Minang Remaja tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2013,Harapan 1 Dakwah tingkat Prov. Sumatera Barat\n" +
                "Tahun 2013,Juara 1 Pidato Bahasa Inggris tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2013,Juara 3 Pidato Bahasa Inggris tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1 Bintang Sains STOP TV tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 2 Olimpiade tingkat Prov. Sumatera Barat\n" +
                "Tahun 2014,Juara 1 Olimpiade tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1 OSN Biologi tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1 Siswa Berprestasi tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1 Lomba Bahasa Inggris tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 3 Pidato Bahasa Inggris tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1; 2; 3 OSN IPS tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1 Kaligrafi tingkat Prov. Sumatera Barat\n" +
                "Tahun 2014,Juara 1 Melukis tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1 Menulis Cerpen tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 1 Cipta Puisi tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 2 OSN Fisika tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara Nyanyi Solo FLS2N tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 3 OSN Matematika tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 3 Karate tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2014,Juara 3 Pencak Silat tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 2 Lomba MIPA tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 1 Lomba Sains tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 1 Lomba Karate tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 3 Lomba Karate tingkat Prov. Sumatera Barat\n" +
                "Tahun 2015,Juara 1 Olimpiade Biologi tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 1 Menulis Cerpen tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 1 Tari FLS2N tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 1 Story Telling tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 2 Vocal Group tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2015,Juara 1 Tari Kreasi FLS2N tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2015,Juara 1 Festival Band SMP tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2015,Juara 1 Lomba Baju Kurung Basiba tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2015,Juara 3 Lomba Baca Puisi tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2016,Juara 1 Lomba MIPA tingkat kab. Pesisir Selatan\n" +
                "Tahun 2016,Juara 1 Lomba Story Telling tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2016,Juara 2 OSN Matematika Tingkat Kab. Pesisir selatan\n" +
                "Tahun 2016,Juara 1 OSN IPS tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2017,Juara 1 O2SN Bulu Tangkis (PA) tingkat Kab. Pesisir selatan\n" +
                "Tahun 2017,Juara 3 O2SN Bulu Tangkis (PI) tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2017,Juara 1 O2SN Renang (PA) tingkat Kab. Pesisir selatan\n" +
                "Tahun 2017,Juara 1 O2SN Renang (PI) tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2017,Juara 2 O2SN Silat (PA) tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2017,Juara 1 O2sn Karate (PI) tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2017,Juara 2 Lomba Story Telling tingkat  Kab. Pesisir selatan\n" +
                "Tahun 2017,Juara 1 Musabaqah Hifzil Quran (PA) tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2017,Juara 1 Pidato tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2017,Juara 1 Lomba Sprrch Contest tingkat  Kab. Pesisir Selatan\n" +
                "Tahun 2018,Juara 1 Lomba Pidato tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2018,Juara 1 Lomba Cipta Baca Puisi tingkat Provinsi Sumatera  Barat\n" +
                "Tahun 2018,Juara 1 Lomba O2SN Karate (PA) tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2018,Juara 2 Lomba Cipta Baca Puisi tingkat kabupaten\n" +
                "Tahun 2018,Juara 1 Lomba OSN IPS tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2018,Juara 3 Lomba FLSN2N Cipta Baca Puisi tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2018,Juara 2 Lomba FLS2SN Lomba Story Telling (PA) tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2019,Juara 1 Lomba Pidato  tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2021,Juara 1 Lomba Story Telling tingkat Kab. Pesisir Selatan\n" +
                "Tahun 2021,Juara 2 Lomba Biologi Tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2022,5 terbaik Festival  Permainan Tradisional (Tangkelek Raksasa)\n" +
                "Tahun 2022,Juara 1 Lomba Event Speech (Pidato) tingkat Nasional\n" +
                "Tahun 2022,Juara 1 Lomba Pidato Bahasa Inggris tingkat KabPesisir Selatan\n" +
                "Tahun 2022,Juara 1 Lomba Pidato Bahasa Indonesia (PI) tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2022,Juara 1 Lomba Puisi Islami (PA) tingkat Provinsi Sumatera Barat\n" +
                "Tahun 2022,Juara 3 Lomba FLS2N Vocal Solo tingkat Kab. Pesisir Selatan"

        val items = mutableListOf<SchoolPrestasi>()
        prestasies.split("\n").forEach {
            val prestasi = it.split(",")
            items.add(SchoolPrestasi("", prestasi[0].replace("Tahun ", ""), prestasi[1]))
        }
        db.submitPrestasi(items)
    }

    fun addMapel() {
        val mapels = "Matematika;matematika;Laura Aulia, S.Pd.;;2023/2024\n" +
                "Bahasa Indonesia;b.ind;Imlafihasna, S.Pd.;196811142002122001;2023/2024\n" +
                "Bahasa Inggris;b.ing;Edriandi, S.Pd.;197507212008011001;2023/2024\n" +
                "Seni Budaya;senbud;Prassetyo Fajar Gumilang, S.Pd.;;2023/2024"

        mapels.split("\n").forEach {
            val mapel = it.split(";")
            val mapelModel = Mapel(
                "", mapel[0], mapel[1], listOf("${mapel[4]}_vii.1"), mapel[2], mapel[3], mapel[4]
            )
            db.submitMapel(mapelModel)
        }
    }

    fun addJadwal() {
        val jadwals = "senin;07.00-08.00;;Upacara\n" +
                "senin;08.00-08.40;Dam Irzam, S.Pd;IPS\n" +
                "senin;08.40-09.20;Dam Irzam, S.Pd;IPS\n" +
                "senin;09.20-10.00;Dam Irzam, S.Pd;IPS\n" +
                "senin;10.00-10.30;;Istirahat\n" +
                "senin;10.30-11.10;Laura Aulia, S.Pd;MM\n" +
                "senin;11.10-11.50;Laura Aulia, S.Pd;MM\n" +
                "senin;11.50-12.20;;P5\n" +
                "senin;12.20-13.00;;Ishoma\n" +
                "senin;13.00-13.50;;P5\n" +
                "selasa;07.00-07.30;;Pembiasaan/Literasi\n" +
                "selasa;07.30-08.10;Prassetyo Fajar Gumilang,S.Pd;S.Budaya\n" +
                "selasa;08.10-08.50;Prassetyo Fajar Gumilang,S.Pd;S.Budaya\n" +
                "selasa;08.50-09.30;Rahmawirda, S.Pd;B.ING\n" +
                "selasa;09.30-10.10;Rahmawirda, S.Pd;B.ING\n" +
                "selasa;10.10-10.40;;Istirahat\n" +
                "selasa;10.40-11.20;Septania Caesaria S, s.Pd;P.Kn\n" +
                "selasa;11.20 -12.00;Septania Caesaria S, s.Pd;P.Kn\n" +
                "selasa;12.00-12.20;;P5\n" +
                "selasa;12.20-13.00;;Ishoma\n" +
                "selasa;13.00-14.00;;P5\n" +
                "rabu;07.00-07.30;;Pembiasaan/Literasi\n" +
                "rabu;07.30-08.10;Imlafihasna,S.Pd;B.INDO\n" +
                "rabu;08.10-08.50;Imlafihasna,S.Pd;B.INDO\n" +
                "rabu;08.50-09.30;Imlafihasna,S.Pd;B.INDO\n" +
                "rabu;09.30-10.10;Edriandi, S.Pd;B.ING\n" +
                "rabu;10.10-10.40;;Istirahat\n" +
                "rabu;10.40-11.20;Edriandi, S.Pd;B.ING\n" +
                "rabu;11.20 -12.00;Edriandi, S.Pd;B.ING\n" +
                "rabu;12.00-12.20;Mislawati, S.Ag;PAI\n" +
                "rabu;12.20-13.00;;Ishoma\n" +
                "rabu;13.00-14.00;Mislawati, S.Ag;PAI\n" +
                "kamis;07.00-07.30;;Pembiasaan/Literasi\n" +
                "kamis;07.30-08.10;Laura Aulia, S.Pd;MM\n" +
                "kamis;08.10-08.50;Laura Aulia, S.Pd;MM\n" +
                "kamis;08.50-09.30;Junaidi,S.sos;Penjas\n" +
                "kamis;09.30-10.10;Junaidi,S.sos;Penjas\n" +
                "kamis;10.10-10.40;;Istirahat\n" +
                "kamis;10.40-11.20;;Pramuka\n" +
                "kamis;11.20 -12.00;;Pramuka\n" +
                "kamis;12.00-12.20;Meldawati.S.Pd;IPA\n" +
                "kamis;12.20-13.00;;Ishoma\n" +
                "kamis;13.00-14.00;Meldawati.S.Pd;IPA\n" +
                "jumat;07.00-08.00;;Kultum\n" +
                "jumat;08.00-08.40;Imlafihasna,S.Pd;B.INDO\n" +
                "jumat;08.40-09.20;Imlafihasna,S.Pd;B.INDO\n" +
                "jumat;09.20-10.00;;P5\n" +
                "jumat;10.00-10.20;;Istirahat\n" +
                "jumat;10.20-11.00;;P5\n" +
                "jumat;11.00-11.40;;P5\n" +
                "sabtu;07.00-08.00;;Tahfidz/Senam/Goro\n" +
                "sabtu;08.00-08.40;Meldawati.S.Pd;IPA\n" +
                "sabtu;08.40-09.20;Meldawati.S.Pd;IPA\n" +
                "sabtu;09.20-10.00;;P5\n" +
                "sabtu;10.00-10.30;;Istirahat\n" +
                "sabtu;10.30-11.10;;P5\n" +
                "sabtu;11.10-12.10;;P5\n" +
                "sabtu;12.10- 12.50;;Ekstrakurikuler"

        var counter = 1
        var day = ""

        jadwals.split("\n").forEach {
            val jadwal = it.split(";")
            val hari = jadwal[0]
            val jam = jadwal[1]
            val guru = jadwal[2]
            val mapel = jadwal[3]
            val kelas = "vii.1"
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
                jam
            )
            db.submitJadwal(jadwalModel)
        }
    }

    fun addKelas() {
        val kelases = "VII.1;Sonia Dwi Helfira, S.Pd;199109282019032000;14;18;32\n" +
                "VII.2;Junaidi,S.sos, M.Pd;196701212000121001;14;18;32\n" +
                "VII.3;Dam Irzam, S.Pd;197008212003121002;14;16;30\n" +
                "VII.4;Meldawati.S.Pd;197507102000122002;14;14;28\n" +
                "VII.5;Sri Hartini Dwiyanti P, S.Pd;197003272008012003;14;14;28\n" +
                "VII.6;Tuti Novia, S.Pd;197311132002122003;14;17;31\n" +
                "VII.7;Reno Yuni, S.Si;198006202009022006;14;17;31\n" +
                "VII.8;Prassetyo Fajar Gumilang,S.Pd;199506172023211000;14;14;28\n" +
                "VIII.1;Indrawita, S.Pd.I;198106012010012034;14;18;32\n" +
                "VIII.2;Rozi Syahriawida,S.Pd;198505032019032001;15;17;32\n" +
                "VIII.3;Ayu Permata Sari,S.Pd;199603202023212029;15;14;29\n" +
                "VIII.4;Rogendra Midona, S.Pd;198209192009011005;13;16;29\n" +
                "VIII.5;Admi Putri,S.Si;197802122014072002;15;14;29\n" +
                "VIII.6;Oktri Fahmi Rani,S.Pd,M.Pd;198410102005012006;15;14;29\n" +
                "VIII.7;Anggrianti Sofia Ningsih, S.Pd;198003202010012015;14;13;27\n" +
                "VIII.8;Afdilla Wahyuni, S.Pd;199406112023212030;15;13;28\n" +
                "IX.1;Edriandi, S.Pd;197507212008011001;18;14;32\n" +
                "IX.2;Zaimah, S.Pd;196411151989032004;16;16;32\n" +
                "IX.3;Drs. Yaksaf;196512121991031010;18;14;32\n" +
                "IX.4;Ernita, S.Pd;196506031998022001;17;15;32\n" +
                "IX.5;Yessi Agustin, S.Pd;198608192023212027;16;16;32\n" +
                "IX.6;Uwin,S.Pd I;196512101991031007;14;17;31\n" +
                "IX.7;Efni R, S.Pd;197505052014072003;13;18;31"

        var counter = 0
        kelases.split("\n").forEach {
            val kelas = it.split(";")
            val name = kelas[0].lowercase()
            val walas = kelas[1]
            val nip = kelas[2]
            val male = kelas[3].toInt()
            val female = kelas[4].toInt()
            val kelasModel = Kelas(
                "2023_2024_$name",
                name,
                walas,
                nip,
                male,
                female,
                male + female,
                true,
                "2023/2024",
                counter
            )
            db.addKelas(kelasModel)
            counter++
        }
    }

    fun migrateSiswa() {
        db.loadUserListByRoleAndClass(siswa).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toObjects(User::class.java).forEach {
                    val user = it
                    val nameWords = it.name?.generateKeyword()
                    nameWords?.addAll(it.no_id?.generateKeyword() ?: emptyList())
                    user.words = nameWords ?: emptyList()
                    db.updateUser(user)
                }
            }
        }
    }

    fun generateKeywordBook() {
        db.loadBookList(online = true, limit = 5000).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.toObjects(Book::class.java).forEach {
                    val words = it.judul?.generateKeyword()
                    words?.addAll(it.isbn?.generateKeyword() ?: emptyList())
                    it.words = words ?: emptyList()
                    db.updateBook(it)
                }
            }
        }
    }


    val list = mutableListOf<User>()
    fun addUser() {
        list.clear()
        val userData = "19352,Deandra Putri,P\n" +
                "19360,Fadil Pratama Putra,L\n" +
                "19363,Farel Ramadhan,L\n" +
                "19367,Fattan Akhtar,L\n" +
                "19375,Grevan Causta Iqbal,L\n" +
                "19382,Irfan Mahdi,L\n" +
                "19398,Lovely Anjani,P\n" +
                "19401,M.Alif Pratama,L\n" +
                "19409,M.Varrel Adrian,L\n" +
                "19428,Muhammad Rayhan,L\n" +
                "19432,Muhammad Septian Gifari,L\n" +
                "19434,Mutiara Silvika Zamuna,P\n" +
                "19463,Pangeran Qalbi,L\n" +
                "19470,Rafael Bil Janua,L\n" +
                "19477,Rahmadani,P\n" +
                "19497,Ronenzo Sultan Anand,L\n" +
                "19499,Saskia Septiani,P\n" +
                "19509,Syaira,P\n" +
                "19511,Syifa Putri Lindri,P\n" +
                "19518,Ufairah Ghaziyah,P\n" +
                "19520,Vanesya Elvano Utami,P\n" +
                "19525,Vitri Devira,P\n" +
                "19533,Zefiza Leora,P"
        for (data in userData.split("\n")) {
            val info = data.split(",")
            val user = User()
            user.apply {
                no_id = info[0]
                name = info[1].trim().split(" ")
                    .joinToString(separator = " ", transform = String::capitalize)
                gender = if (info[2].trim() == "L") pria else wanita
                email = "${info[0]}@mailinator.com"
                kelas = "VII_7"
            }
            list.add(user)
        }

        submitNewUser(0)
    }

    private fun submitNewUser(index: Int) {
        if (index > list.size - 1) {
            dismissLoading()
            return
        }
        showLoading()
        val user = list[index]
        db.loginWithEmailAndPassword(user.email ?: "", "${user.no_id}2022").continueWith {
            user.key = it.result.user?.uid
            user.role = siswa
            user.createdDate = System.currentTimeMillis()
            return@continueWith db.updateUser(user)
        }.addOnCompleteListener {
            submitNewUser(index + 1)
        }
    }

    fun generateHomeMenu() {
        val homeMenu = mutableListOf<HomeModel>()

        homeMenu.add(
            HomeModel(
                "banner", 0, listOf(
                    HomeMenu(
                        "",
                        "Peringatan Hari Batik 2023",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/banner%2Fperingatan_hari_batik_2023.png?alt=media&token=483bf963-5e58-46ec-9cf7-d3f1565df959",
                        "https://smpn1painan.sch.id/html/index.php?id=berita&kode=73",
                        typeWeb,
                        System.currentTimeMillis(),
                        0
                    ),
                    HomeMenu(
                        "",
                        "Peringatan Maulid Nabi 2023",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/banner%2Fperingatan_maulid_nabi_2023.png?alt=media&token=36b87e90-931d-4dbd-8ff8-223ff3084013",
                        "https://smpn1painan.sch.id/html/index.php?id=berita&kode=72",
                        typeWeb,
                        System.currentTimeMillis(),
                        1
                    ),
                    HomeMenu(
                        "",
                        "Ujian ANBK 2023",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/banner%2Fujian_anbk_2023.png?alt=media&token=d029fe51-7e11-4e49-aafb-243e51232adb",
                        "https://smpn1painan.sch.id/html/index.php?id=berita&kode=71",
                        typeWeb,
                        System.currentTimeMillis(),
                        2
                    ),
                    HomeMenu(
                        "",
                        "Kegiatan Senam P5 2023",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/banner%2Fsenam_kreasi_p5.png?alt=media&token=37384f43-2407-4994-98db-aec4fc11b1bb",
                        "https://smpn1painan.sch.id/html/index.php?id=berita&kode=70",
                        typeWeb,
                        System.currentTimeMillis(),
                        3
                    )
                )
            )
        )
        homeMenu.add(
            HomeModel(
                "menu", 0, listOf(
                    HomeMenu(
                        "profile_sekolah",
                        "Profil Sekolah",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/menu%2Fprofile_sekolah.png?alt=media&token=5b5f200e-2cab-4cf0-ba12-cf1f0198ab02",
                        "sekolahdigital://home/profile",
                        typeDeeplink,
                        System.currentTimeMillis(),
                        0
                    ),
                    HomeMenu(
                        "perpustakaan",
                        "Perpustakaan",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/menu%2Fperpustakaan.png?alt=media&token=65e5ba9e-aaf7-4c71-aee9-2155064c6b7f",
                        "sekolahdigital://home/perpustakaan",
                        typeDeeplink,
                        System.currentTimeMillis(),
                        1
                    ),
                    HomeMenu(
                        "siswa_alumni",
                        "Siswa & Alumni",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/menu%2Fsiswa.png?alt=media&token=13fd21ee-61ab-4973-8c85-c1023b3c6e40",
                        "sekolahdigital://home/siswaalumni",
                        typeDeeplink,
                        System.currentTimeMillis(),
                        2
                    ),
                    HomeMenu(
                        "kbm",
                        "KBM",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/menu%2Fkbm.png?alt=media&token=4bf69ac7-0d8b-4fdb-86f3-0b853d7fe73d",
                        "sekolahdigital://home/kbm",
                        typeDeeplink,
                        System.currentTimeMillis(),
                        3
                    ),
                    HomeMenu(
                        "kegiatan",
                        "Intra & Ekstra",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/menu%2Fkegiatan.png?alt=media&token=43255e94-d328-4d37-957f-44711e88e36c",
                        "sekolahdigital://home/intraekstra",
                        typeDeeplink,
                        System.currentTimeMillis(),
                        4
                    ),
                    HomeMenu(
                        "prestasi",
                        "Prestasi",
                        "",
                        "https://firebasestorage.googleapis.com/v0/b/school-library-d7c6d.appspot.com/o/menu%2Fprestasi.png?alt=media&token=1fc01aa9-f5f9-4e5d-b91b-7cded4236278",
                        "sekolahdigital://home/prestasi",
                        typeDeeplink,
                        System.currentTimeMillis(),
                        5
                    )
                )
            )
        )

        homeMenu.forEach {
            db.submitMenu(it)
        }
    }

    fun importExtra() {
        val extra = "Pramuka;Seluruh Siswa;;;1. Indrawita, S.Pd.I*2. Fauziah Syawalni, S.Pd*3. Ahmad Syukri M, S.Pd;\n" +
                "KSN;Seluruh siswa kelas VII dan VIII yang berminat;Untuk membina siswa agar memiliki kemampuan dalam mengikuti lomba;1. Matematika*2. IPA*3. IPS;1. Windri Affela, S.Pd*2. Meldawati, S.Pd*3. Ernita, S.Pd;Sabtu\n" +
                "Korsik;Seluruh siswa yang berminat;Membina siswa supaya mahir bermain musik sesuai dengan kebutuhan sekolah untuk persiapan lomba;Musik tradisional;Prasettyo Fajar Gumilang, S.Pd;Sabtu\n" +
                "Story Telling;Seluruh siswa yang berminat;Membina siswa agar memiliki kemapuan bercerita dengan berbahasa inggris yang baik dan benar untuk persiapan mengikuti lomba;Cerita dongeng;Rozi Syahriawida, S.Pd;Sabtu\n" +
                "Karate;Seluruh siswa yang berminat;Membina siswa agar mampu seni bela diri;Gerakan dasar seni bela diri;Annisa Septiara Effendi, S.Pd;Sabtu\n" +
                "OSIS;Seluruh siswa yang berminat;Melatih jiwa kepemimpinan siswa;Memimpin organisasi di sekolah;Dam Irzam, S.Pd;\n" +
                "ROHIS;Seluruh Siswa;Peningkatan pengetahuan keagamaan;Pengetahuan keseharian siswa;1. Jon Harmen, M.A*2. Mislawati, S.Ag;"

        val models = mutableListOf<SchoolExtra>()
        extra.split("\n").forEach {
            val data = it.split(";")
            val model = SchoolExtra(
                data[0], data[1], data[2], data[3], data[4], data[5]
            )
            models.add(model)
        }

        val extraModel = SchoolExtraModel(
            "Ekstrakurikuler",
            "",
            models
        )

        db.submitExtra(extraModel)
    }
}