package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE id = :id LIMIT 1")
    suspend fun getBookmarkById(id: String): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    // Memorization
    @Query("SELECT * FROM memorization_records")
    fun getAllMemorizationRecords(): Flow<List<MemorizationRecordEntity>>

    @Query("SELECT * FROM memorization_records WHERE id = :id LIMIT 1")
    suspend fun getMemorizationRecord(id: String): MemorizationRecordEntity?

    @Query("SELECT * FROM memorization_records WHERE status = :status")
    fun getRecordsByStatus(status: String): Flow<List<MemorizationRecordEntity>>

    @Query("SELECT COUNT(*) FROM memorization_records WHERE status = 'MEMORIZED'")
    fun getMemorizedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMemorizationRecord(record: MemorizationRecordEntity)

    @Query("UPDATE memorization_records SET repetitionCount = repetitionCount + 1, lastReviewedTimestamp = :timestamp WHERE id = :id")
    suspend fun incrementRepetition(id: String, timestamp: Long = System.currentTimeMillis())

    // Preferences (e.g. Last Read, Daily Goal, Reciter, Theme)
    @Query("SELECT value FROM user_preferences WHERE key = :key LIMIT 1")
    suspend fun getPreference(key: String): String?

    @Query("SELECT value FROM user_preferences WHERE key = :key LIMIT 1")
    fun getPreferenceFlow(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPreference(pref: UserPreferenceEntity)
}
