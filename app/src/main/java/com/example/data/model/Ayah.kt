package com.example.data.model

data class AyahWord(
    val wordIndex: Int,
    val arabic: String,
    val translation: String,
    val transliteration: String
)

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val arabicText: String,
    val translationSahih: String,
    val translationUrdu: String = "",
    val translationBangla: String = "",
    val transliteration: String = "",
    val words: List<AyahWord> = emptyList(),
    val tajweedRules: List<TajweedSpan> = emptyList(),
    val audioAyahUrl: String = ""
) {
    val identifier: String get() = "$surahNumber:$ayahNumber"
}

enum class MemorizationStatus(val label: String) {
    NOT_STARTED("Not Started"),
    MEMORIZING("Memorizing"),
    MEMORIZED("Memorized"),
    NEEDS_REVIEW("Needs Review")
}
