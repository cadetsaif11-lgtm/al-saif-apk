package com.example

import com.example.data.local.BookmarkEntity
import com.example.data.local.MemorizationRecordEntity
import com.example.data.local.QuranDao
import com.example.data.local.UserPreferenceEntity
import com.example.data.model.MemorizationStatus
import com.example.data.model.TajweedRuleType
import com.example.data.repository.QuranDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: QuranDataRepository
    private val fakeDao = object : QuranDao {
        val bookmarks = mutableMapOf<String, BookmarkEntity>()
        val records = mutableMapOf<String, MemorizationRecordEntity>()
        val prefs = mutableMapOf<String, String>()

        override fun getAllBookmarks(): Flow<List<BookmarkEntity>> = flowOf(bookmarks.values.toList())
        override suspend fun getBookmarkById(id: String): BookmarkEntity? = bookmarks[id]
        override suspend fun insertBookmark(bookmark: BookmarkEntity) { bookmarks[bookmark.id] = bookmark }
        override suspend fun deleteBookmark(id: String) { bookmarks.remove(id) }

        override fun getAllMemorizationRecords(): Flow<List<MemorizationRecordEntity>> = flowOf(records.values.toList())
        override suspend fun getMemorizationRecord(id: String): MemorizationRecordEntity? = records[id]
        override fun getRecordsByStatus(status: String): Flow<List<MemorizationRecordEntity>> =
            flowOf(records.values.filter { it.status == status })
        override fun getMemorizedCount(): Flow<Int> =
            flowOf(records.values.count { it.status == "MEMORIZED" })
        override suspend fun upsertMemorizationRecord(record: MemorizationRecordEntity) { records[record.id] = record }
        override suspend fun incrementRepetition(id: String, timestamp: Long) {
            val r = records[id]
            if (r != null) {
                records[id] = r.copy(repetitionCount = r.repetitionCount + 1, lastReviewedTimestamp = timestamp)
            }
        }

        override suspend fun getPreference(key: String): String? = prefs[key]
        override fun getPreferenceFlow(key: String): Flow<String?> = flowOf(prefs[key])
        override suspend fun setPreference(pref: UserPreferenceEntity) { prefs[pref.key] = pref.value }
    }

    @Before
    fun setup() {
        repository = QuranDataRepository(fakeDao)
    }

    @Test
    fun `all 114 surahs are loaded with correct metadata`() {
        assertEquals(114, repository.surahs.size)
        assertEquals("Al-Fatihah", repository.surahs[0].englishName)
        assertEquals(7, repository.surahs[0].numberOfAyahs)
        assertEquals("An-Nas", repository.surahs[113].englishName)
        assertEquals(6, repository.surahs[113].numberOfAyahs)
    }

    @Test
    fun `surah al-fatihah has authentic text and tajweed spans`() {
        val ayahs = repository.getAyahsForSurah(1)
        assertEquals(7, ayahs.size)
        assertTrue(ayahs[0].arabicText.contains("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"))
        assertTrue(ayahs[0].tajweedRules.any { it.rule == TajweedRuleType.MADD })
    }

    @Test
    fun `memorization tracking and bookmarks update correctly`() = runBlocking {
        val surah = repository.getSurahByNumber(1)!!
        val ayah1 = repository.getAyahsForSurah(1)[0]

        assertEquals(MemorizationStatus.NOT_STARTED, repository.getMemorizationStatus(1, 1))

        repository.updateMemorizationStatus(1, 1, MemorizationStatus.MEMORIZING)
        assertEquals(MemorizationStatus.MEMORIZING, repository.getMemorizationStatus(1, 1))

        repository.updateMemorizationStatus(1, 1, MemorizationStatus.MEMORIZED)
        assertEquals(MemorizationStatus.MEMORIZED, repository.getMemorizationStatus(1, 1))

        repository.recordRepetition(1, 1)
        assertEquals(MemorizationStatus.MEMORIZED, repository.getMemorizationStatus(1, 1))

        assertFalse(repository.isBookmarked(1, 1))
        repository.toggleBookmark(surah, ayah1, "My favorite verse")
        assertTrue(repository.isBookmarked(1, 1))
        repository.toggleBookmark(surah, ayah1)
        assertFalse(repository.isBookmarked(1, 1))
    }
}
