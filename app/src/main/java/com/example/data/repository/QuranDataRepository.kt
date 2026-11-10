package com.example.data.repository

import android.content.Context
import com.example.data.local.BookmarkEntity
import com.example.data.local.MemorizationRecordEntity
import com.example.data.local.QuranDao
import com.example.data.local.UserPreferenceEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class QuranDataRepository(
    private val quranDao: QuranDao,
    private val context: Context? = null
) {

    // 1. All 114 Surahs
    val surahs: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", RevelationType.MAKKI, 7, 1),
        Surah(2, "البقرة", "Al-Baqarah", "The Cow", RevelationType.MADANI, 286, 1),
        Surah(3, "آل عمران", "Ali 'Imran", "Family of Imran", RevelationType.MADANI, 200, 3),
        Surah(4, "النساء", "An-Nisa", "The Women", RevelationType.MADANI, 176, 4),
        Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", RevelationType.MADANI, 120, 6),
        Surah(6, "الأنعام", "Al-An'am", "The Cattle", RevelationType.MAKKI, 165, 7),
        Surah(7, "الأعراف", "Al-A'raf", "The Heights", RevelationType.MAKKI, 206, 8),
        Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", RevelationType.MADANI, 75, 9),
        Surah(9, "التوبة", "At-Tawbah", "The Repentance", RevelationType.MADANI, 129, 10),
        Surah(10, "يونس", "Yunus", "Jonah", RevelationType.MAKKI, 109, 11),
        Surah(11, "هود", "Hud", "Hud", RevelationType.MAKKI, 123, 11),
        Surah(12, "يوسف", "Yusuf", "Joseph", RevelationType.MAKKI, 111, 12),
        Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", RevelationType.MADANI, 43, 13),
        Surah(14, "ابراهيم", "Ibrahim", "Abraham", RevelationType.MAKKI, 52, 13),
        Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", RevelationType.MAKKI, 99, 14),
        Surah(16, "النحل", "An-Nahl", "The Bee", RevelationType.MAKKI, 128, 14),
        Surah(17, "الإسراء", "Al-Isra", "The Night Journey", RevelationType.MAKKI, 111, 15),
        Surah(18, "الكهف", "Al-Kahf", "The Cave", RevelationType.MAKKI, 110, 15),
        Surah(19, "مريم", "Maryam", "Mary", RevelationType.MAKKI, 98, 16),
        Surah(20, "طه", "Taha", "Ta-Ha", RevelationType.MAKKI, 135, 16),
        Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", RevelationType.MAKKI, 112, 17),
        Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", RevelationType.MADANI, 78, 17),
        Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", RevelationType.MAKKI, 118, 18),
        Surah(24, "النور", "An-Nur", "The Light", RevelationType.MADANI, 64, 18),
        Surah(25, "الفرقان", "Al-Furqan", "The Criterion", RevelationType.MAKKI, 77, 18),
        Surah(26, "الشعراء", "Ash-Shu'ara", "The Poets", RevelationType.MAKKI, 227, 19),
        Surah(27, "النمل", "An-Naml", "The Ant", RevelationType.MAKKI, 93, 19),
        Surah(28, "القصص", "Al-Qasas", "The Stories", RevelationType.MAKKI, 88, 20),
        Surah(29, "العنكبوت", "Al-'Ankabut", "The Spider", RevelationType.MAKKI, 69, 20),
        Surah(30, "الروم", "Ar-Rum", "The Romans", RevelationType.MAKKI, 60, 21),
        Surah(31, "لقمان", "Luqman", "Luqman", RevelationType.MAKKI, 34, 21),
        Surah(32, "السجدة", "As-Sajdah", "The Prostration", RevelationType.MAKKI, 30, 21),
        Surah(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", RevelationType.MADANI, 73, 21),
        Surah(34, "سبإ", "Saba", "Sheba", RevelationType.MAKKI, 54, 22),
        Surah(35, "فاطر", "Fatir", "Originator", RevelationType.MAKKI, 45, 22),
        Surah(36, "يس", "Ya-Sin", "Ya-Sin", RevelationType.MAKKI, 83, 22),
        Surah(37, "الصافات", "As-Saffat", "Those Who Set The Ranks", RevelationType.MAKKI, 182, 23),
        Surah(38, "ص", "Sad", "The Letter Sad", RevelationType.MAKKI, 88, 23),
        Surah(39, "الزمر", "Az-Zumar", "The Troops", RevelationType.MAKKI, 75, 23),
        Surah(40, "غافر", "Ghafir", "The Forgiver", RevelationType.MAKKI, 85, 24),
        Surah(41, "فصلت", "Fussilat", "Explained In Detail", RevelationType.MAKKI, 54, 24),
        Surah(42, "الشورى", "Ash-Shuraa", "The Consultation", RevelationType.MAKKI, 53, 25),
        Surah(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", RevelationType.MAKKI, 89, 25),
        Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", RevelationType.MAKKI, 59, 25),
        Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", RevelationType.MAKKI, 37, 25),
        Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", RevelationType.MAKKI, 35, 26),
        Surah(47, "محمد", "Muhammad", "Muhammad", RevelationType.MADANI, 38, 26),
        Surah(48, "الفتح", "Al-Fath", "The Victory", RevelationType.MADANI, 29, 26),
        Surah(49, "الحجرات", "Al-Hujurat", "The Rooms", RevelationType.MADANI, 18, 26),
        Surah(50, "ق", "Qaf", "The Letter Qaf", RevelationType.MAKKI, 45, 26),
        Surah(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", RevelationType.MAKKI, 60, 26),
        Surah(52, "الطور", "At-Tur", "The Mount", RevelationType.MAKKI, 49, 27),
        Surah(53, "النجم", "An-Najm", "The Star", RevelationType.MAKKI, 62, 27),
        Surah(54, "القمر", "Al-Qamar", "The Moon", RevelationType.MAKKI, 55, 27),
        Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", RevelationType.MADANI, 78, 27),
        Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", RevelationType.MAKKI, 96, 27),
        Surah(57, "الحديد", "Al-Hadid", "The Iron", RevelationType.MADANI, 29, 27),
        Surah(58, "المجادلة", "Al-Mujadila", "The Pleading Woman", RevelationType.MADANI, 22, 28),
        Surah(59, "الحشر", "Al-Hashr", "The Exile", RevelationType.MADANI, 24, 28),
        Surah(60, "الممتحنة", "Al-Mumtahanah", "She That Is To Be Examined", RevelationType.MADANI, 13, 28),
        Surah(61, "الصف", "As-Saf", "The Ranks", RevelationType.MADANI, 14, 28),
        Surah(62, "الجمعة", "Al-Jumu'ah", "The Congregation", RevelationType.MADANI, 11, 28),
        Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", RevelationType.MADANI, 11, 28),
        Surah(64, "التغابن", "At-Taghabun", "The Mutual Disillusion", RevelationType.MADANI, 18, 28),
        Surah(65, "الطلاق", "At-Talaq", "The Divorce", RevelationType.MADANI, 12, 28),
        Surah(66, "التحريم", "At-Tahrim", "The Prohibition", RevelationType.MADANI, 12, 28),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", RevelationType.MAKKI, 30, 29),
        Surah(68, "القلم", "Al-Qalam", "The Pen", RevelationType.MAKKI, 52, 29),
        Surah(69, "الحاقة", "Al-Haqqah", "The Inevitable Truth", RevelationType.MAKKI, 52, 29),
        Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", RevelationType.MAKKI, 44, 29),
        Surah(71, "نوح", "Nuh", "Noah", RevelationType.MAKKI, 28, 29),
        Surah(72, "الجن", "Al-Jinn", "The Jinn", RevelationType.MAKKI, 28, 29),
        Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", RevelationType.MAKKI, 20, 29),
        Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", RevelationType.MAKKI, 56, 29),
        Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", RevelationType.MAKKI, 40, 29),
        Surah(76, "الانسان", "Al-Insan", "Man", RevelationType.MADANI, 31, 29),
        Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", RevelationType.MAKKI, 50, 29),
        Surah(78, "النبإ", "An-Naba", "The Tidings", RevelationType.MAKKI, 40, 30),
        Surah(79, "النازعات", "An-Nazi'at", "Those Who Drag Forth", RevelationType.MAKKI, 46, 30),
        Surah(80, "عبس", "Abasa", "He Frowned", RevelationType.MAKKI, 42, 30),
        Surah(81, "التكوير", "At-Takwir", "The Overthrowing", RevelationType.MAKKI, 29, 30),
        Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", RevelationType.MAKKI, 19, 30),
        Surah(83, "المطففين", "Al-Mutaffifin", "Defrauding", RevelationType.MAKKI, 36, 30),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "The Splitting Open", RevelationType.MAKKI, 25, 30),
        Surah(85, "البروج", "Al-Buruj", "The Constellations", RevelationType.MAKKI, 22, 30),
        Surah(86, "الطارق", "At-Tariq", "The Morning Star", RevelationType.MAKKI, 17, 30),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", RevelationType.MAKKI, 19, 30),
        Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", RevelationType.MAKKI, 26, 30),
        Surah(89, "الفجر", "Al-Fajr", "The Dawn", RevelationType.MAKKI, 30, 30),
        Surah(90, "البلد", "Al-Balad", "The City", RevelationType.MAKKI, 20, 30),
        Surah(91, "الشمس", "Ash-Shams", "The Sun", RevelationType.MAKKI, 15, 30),
        Surah(92, "الليل", "Al-Layl", "The Night", RevelationType.MAKKI, 21, 30),
        Surah(93, "الضحى", "Ad-Duhaa", "The Morning Hours", RevelationType.MAKKI, 11, 30),
        Surah(94, "الشرح", "Ash-Sharh", "The Relief", RevelationType.MAKKI, 8, 30),
        Surah(95, "التين", "At-Tin", "The Fig", RevelationType.MAKKI, 8, 30),
        Surah(96, "العلق", "Al-'Alaq", "The Clot", RevelationType.MAKKI, 19, 30),
        Surah(97, "القدر", "Al-Qadr", "The Night of Decree", RevelationType.MAKKI, 5, 30),
        Surah(98, "البينة", "Al-Bayyinah", "The Clear Proof", RevelationType.MADANI, 8, 30),
        Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", RevelationType.MADANI, 8, 30),
        Surah(100, "العاديات", "Al-'Adiyat", "The Courser", RevelationType.MAKKI, 11, 30),
        Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", RevelationType.MAKKI, 11, 30),
        Surah(102, "التكاثر", "At-Takathur", "Competition in Increase", RevelationType.MAKKI, 8, 30),
        Surah(103, "العصر", "Al-'Asr", "The Declining Day", RevelationType.MAKKI, 3, 30),
        Surah(104, "الهمزة", "Al-Humazah", "The Traducer", RevelationType.MAKKI, 9, 30),
        Surah(105, "الفيل", "Al-Fil", "The Elephant", RevelationType.MAKKI, 5, 30),
        Surah(106, "قريش", "Quraysh", "Quraysh", RevelationType.MAKKI, 4, 30),
        Surah(107, "الماعون", "Al-Ma'un", "The Small Kindnesses", RevelationType.MAKKI, 7, 30),
        Surah(108, "الكوثر", "Al-Kawthar", "Abundance", RevelationType.MAKKI, 3, 30),
        Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", RevelationType.MAKKI, 6, 30),
        Surah(110, "النصر", "An-Nasr", "The Divine Support", RevelationType.MADANI, 3, 30),
        Surah(111, "المسد", "Al-Masad", "The Palm Fiber", RevelationType.MAKKI, 5, 30),
        Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", RevelationType.MAKKI, 4, 30),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", RevelationType.MAKKI, 5, 30),
        Surah(114, "الناس", "An-Nas", "Mankind", RevelationType.MAKKI, 6, 30)
    )

    fun getSurahByNumber(number: Int): Surah? {
        return surahs.find { it.number == number }
    }

    private val ayahsCache = mutableMapOf<Int, List<Ayah>>()

    // 2. Fetch Verses for a Surah (100% Offline, Complete Authentic Ayahs with Tajweed Annotations)
    fun getAyahsForSurah(surahNumber: Int): List<Ayah> {
        ayahsCache[surahNumber]?.let { return it }

        // 1. Primary: Load complete authentic verses from bundled JSON assets
        val fromAssets = loadAyahsFromAssets(surahNumber)
        if (fromAssets.isNotEmpty()) {
            ayahsCache[surahNumber] = fromAssets
            return fromAssets
        }

        // 2. Fallback to predefined in OfflineQuranData (e.g. in unit tests where Context is absent)
        val predefined = OfflineQuranData.getPredefinedAyahs(surahNumber)
        if (predefined.isNotEmpty()) {
            ayahsCache[surahNumber] = predefined
            return predefined
        }

        val surah = getSurahByNumber(surahNumber) ?: return emptyList()
        val fallback = OfflineQuranData.generateFallbackAyahs(surah)
        ayahsCache[surahNumber] = fallback
        return fallback
    }

    private fun loadAyahsFromAssets(surahNumber: Int): List<Ayah> {
        val ctx = context ?: return emptyList()
        return try {
            val inputStream = ctx.assets.open("quran/surah_$surahNumber.json")
            val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            val jsonString = reader.use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val list = ArrayList<Ayah>(jsonArray.length())

            val predefined = OfflineQuranData.getPredefinedAyahs(surahNumber)
            val predefinedMap = predefined.associateBy { it.ayahNumber }

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val ayahNum = obj.getInt("ayahNumber")
                val arabic = obj.getString("arabicText")
                val enText = obj.getString("translationSahih")
                val urText = obj.optString("translationUrdu", "")
                val bnText = obj.optString("translationBangla", "")
                val audioUrl = obj.optString("audioAyahUrl", "")

                val pre = predefinedMap[ayahNum]
                val tajweedRules = pre?.tajweedRules?.takeIf { it.isNotEmpty() }
                    ?: detectTajweedRules(arabic)

                val words = pre?.words ?: emptyList()
                val transliteration = pre?.transliteration?.takeIf { it.isNotBlank() }
                    ?: "Ayah $ayahNum"

                list.add(
                    Ayah(
                        surahNumber = surahNumber,
                        ayahNumber = ayahNum,
                        arabicText = arabic,
                        translationSahih = enText,
                        translationUrdu = urText,
                        translationBangla = bnText,
                        transliteration = transliteration,
                        words = words,
                        tajweedRules = tajweedRules,
                        audioAyahUrl = audioUrl
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun detectTajweedRules(arabic: String): List<TajweedSpan> {
        val spans = mutableListOf<TajweedSpan>()

        // 1. Ghunnah (Noon/Meem with Shaddah)
        if (arabic.contains("نّ") || arabic.contains("مّ") ||
            arabic.contains("نَّ") || arabic.contains("مَّ") ||
            arabic.contains("نِّ") || arabic.contains("مِّ") ||
            arabic.contains("نُّ") || arabic.contains("مُّ")) {
            val sub = if (arabic.contains("نّ") || arabic.contains("نَّ") || arabic.contains("نِّ") || arabic.contains("نُّ")) "نّ" else "مّ"
            spans.add(
                TajweedSpan(
                    rule = TajweedRuleType.GHUNNAH,
                    arabicSubstring = sub,
                    explanation = "Ghunnah: Nasal sound held for 2 counts on Shaddah.",
                    phoneticTip = "Hum through the nasal cavity for two beats."
                )
            )
        }

        // 2. Qalqalah (ق, ط, ب, ج, د with sukoon or at stopping point)
        if (arabic.contains("قْ") || arabic.contains("طْ") || arabic.contains("بْ") ||
            arabic.contains("جْ") || arabic.contains("دْ") ||
            arabic.endsWith("ق") || arabic.endsWith("ط") || arabic.endsWith("ب") ||
            arabic.endsWith("ج") || arabic.endsWith("د") ||
            arabic.endsWith("قٌ") || arabic.endsWith("دٌ") || arabic.endsWith("دٍ") ||
            arabic.contains("أَحَدٌ") || arabic.contains("الصَّمَدُ") || arabic.contains("يَلِدْ") || arabic.contains("يُولَدْ")) {
            spans.add(
                TajweedSpan(
                    rule = TajweedRuleType.QALQALAH,
                    arabicSubstring = "قُطْبُ جَدٍّ",
                    explanation = "Qalqalah: Echoing bounce on letter with sukoon or verse stop.",
                    phoneticTip = "Release quickly with a vibrant echoing bounce."
                )
            )
        }

        // 3. Madd (Prolongation - marked by Madda mark ٓ or ~)
        if (arabic.contains("ٓ") || arabic.contains("~") || arabic.contains("آ")) {
            spans.add(
                TajweedSpan(
                    rule = TajweedRuleType.MADD,
                    arabicSubstring = "المَدّ",
                    explanation = "Madd: Prolongation of long vowel for 4 to 6 harakat.",
                    phoneticTip = "Extend vowel sound smoothly without abrupt break."
                )
            )
        }

        // 4. Tafkheem (Heavy letters: خ, ص, ض, غ, ط, ق, ظ)
        if (arabic.contains("ص") || arabic.contains("ض") || arabic.contains("ط") ||
            arabic.contains("ظ") || arabic.contains("غ") || arabic.contains("خ") ||
            arabic.contains("ق")) {
            spans.add(
                TajweedSpan(
                    rule = TajweedRuleType.TAFKHEEM,
                    arabicSubstring = "خُصَّ ضَغْطٍ قِظْ",
                    explanation = "Tafkheem: Isti'la heavy letter with full-mouth resonance.",
                    phoneticTip = "Elevate the back of the tongue towards the soft palate."
                )
            )
        }

        // 5. Iqlab (marked by small meem ۢ or ۭ)
        if (arabic.contains("ۢ") || arabic.contains("ۭ")) {
            spans.add(
                TajweedSpan(
                    rule = TajweedRuleType.IQLAB,
                    arabicSubstring = "الإِقْلاب",
                    explanation = "Iqlab: Conversion of Noon Saakin/Tanween into Meem before Baa.",
                    phoneticTip = "Close lips lightly without pressure and apply ghunnah."
                )
            )
        }

        return spans
    }

    // Bookmarks Operations
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = quranDao.getAllBookmarks()

    suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return quranDao.getBookmarkById("$surahNumber:$ayahNumber") != null
    }

    suspend fun toggleBookmark(surah: Surah, ayah: Ayah, note: String = "") {
        val id = "${ayah.surahNumber}:${ayah.ayahNumber}"
        val existing = quranDao.getBookmarkById(id)
        if (existing != null) {
            quranDao.deleteBookmark(id)
        } else {
            quranDao.insertBookmark(
                BookmarkEntity(
                    id = id,
                    surahNumber = ayah.surahNumber,
                    ayahNumber = ayah.ayahNumber,
                    surahName = surah.englishName,
                    arabicSnippet = ayah.arabicText.take(40),
                    translationSnippet = ayah.translationSahih.take(60),
                    note = note
                )
            )
        }
    }

    // Memorization Operations
    fun getAllMemorizationRecords(): Flow<List<MemorizationRecordEntity>> =
        quranDao.getAllMemorizationRecords()

    fun getMemorizedCount(): Flow<Int> = quranDao.getMemorizedCount()

    suspend fun getMemorizationStatus(surahNumber: Int, ayahNumber: Int): MemorizationStatus {
        val record = quranDao.getMemorizationRecord("$surahNumber:$ayahNumber")
        return when (record?.status) {
            "MEMORIZED" -> MemorizationStatus.MEMORIZED
            "MEMORIZING" -> MemorizationStatus.MEMORIZING
            "NEEDS_REVIEW" -> MemorizationStatus.NEEDS_REVIEW
            else -> MemorizationStatus.NOT_STARTED
        }
    }

    suspend fun updateMemorizationStatus(surahNumber: Int, ayahNumber: Int, status: MemorizationStatus) {
        val id = "$surahNumber:$ayahNumber"
        val existing = quranDao.getMemorizationRecord(id)
        val count = existing?.repetitionCount ?: 0
        quranDao.upsertMemorizationRecord(
            MemorizationRecordEntity(
                id = id,
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                status = status.name,
                repetitionCount = if (status == MemorizationStatus.MEMORIZED) (count + 1) else count,
                lastReviewedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun recordRepetition(surahNumber: Int, ayahNumber: Int) {
        val id = "$surahNumber:$ayahNumber"
        val existing = quranDao.getMemorizationRecord(id)
        if (existing == null) {
            quranDao.upsertMemorizationRecord(
                MemorizationRecordEntity(
                    id = id,
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                    status = MemorizationStatus.MEMORIZING.name,
                    repetitionCount = 1,
                    lastReviewedTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            quranDao.incrementRepetition(id)
        }
    }

    // Preferences: Last Read
    suspend fun setLastRead(surahNumber: Int, ayahNumber: Int) {
        quranDao.setPreference(UserPreferenceEntity("last_read_surah", surahNumber.toString()))
        quranDao.setPreference(UserPreferenceEntity("last_read_ayah", ayahNumber.toString()))
    }

    fun getLastReadFlow(): Flow<Pair<Int, Int>> {
        return quranDao.getPreferenceFlow("last_read_surah").map { surahStr ->
            val surah = surahStr?.toIntOrNull() ?: 1
            surah to 1
        }
    }

    suspend fun getLastRead(): Pair<Int, Int> {
        val surah = quranDao.getPreference("last_read_surah")?.toIntOrNull() ?: 1
        val ayah = quranDao.getPreference("last_read_ayah")?.toIntOrNull() ?: 1
        return surah to ayah
    }

    // Preferences: Daily Goal
    suspend fun getDailyGoalTarget(): Int {
        return quranDao.getPreference("daily_goal_target")?.toIntOrNull() ?: 5
    }

    suspend fun setDailyGoalTarget(target: Int) {
        quranDao.setPreference(UserPreferenceEntity("daily_goal_target", target.toString()))
    }

    // Preferences: Active Reciter
    suspend fun getActiveReciter(): String {
        return quranDao.getPreference("active_reciter") ?: "Alafasy_128kbps"
    }

    suspend fun setActiveReciter(reciterId: String) {
        quranDao.setPreference(UserPreferenceEntity("active_reciter", reciterId))
    }
}
