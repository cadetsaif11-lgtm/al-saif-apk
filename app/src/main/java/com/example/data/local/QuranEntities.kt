package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String, // "surah:ayah"
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val arabicSnippet: String,
    val translationSnippet: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memorization_records")
data class MemorizationRecordEntity(
    @PrimaryKey
    val id: String, // "surah:ayah"
    val surahNumber: Int,
    val ayahNumber: Int,
    val status: String, // "NOT_STARTED", "MEMORIZING", "MEMORIZED", "NEEDS_REVIEW"
    val repetitionCount: Int = 0,
    val lastReviewedTimestamp: Long = System.currentTimeMillis(),
    val retentionScore: Int = 0 // 0 to 100
)

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
