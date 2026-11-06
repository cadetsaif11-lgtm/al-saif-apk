package com.example.data.model

enum class RevelationType(val title: String) {
    MAKKI("Makki (Meccan)"),
    MADANI("Madani (Medinan)")
}

data class Surah(
    val number: Int,
    val arabicName: String,
    val englishName: String,
    val englishMeaning: String,
    val revelationType: RevelationType,
    val numberOfAyahs: Int,
    val juzStart: Int,
    val revelationOrder: Int = 1
)
