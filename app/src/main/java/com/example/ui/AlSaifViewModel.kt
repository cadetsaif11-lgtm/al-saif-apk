package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlaybackState
import com.example.audio.AudioPlayerManager
import com.example.audio.Reciter
import com.example.data.local.BookmarkEntity
import com.example.data.local.QuranDatabase
import com.example.data.model.*
import com.example.data.repository.QuranDataRepository
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AlSaifUiState(
    val surahs: List<Surah> = emptyList(),
    val selectedSurah: Surah? = null,
    val currentAyahs: List<Ayah> = emptyList(),
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val memorizedCount: Int = 0,
    val dailyGoalTarget: Int = 5,
    val bookmarks: List<BookmarkEntity> = emptyList(),
    val currentSurahMemorizationStatuses: Map<Int, MemorizationStatus> = emptyMap(),
    val currentSurahBookmarkedAyahs: Set<Int> = emptySet(),
    val themeMode: ThemeMode = ThemeMode.OBSIDIAN_EMERALD
)

class AlSaifViewModel(application: Application) : AndroidViewModel(application) {

    private val database = QuranDatabase.getDatabase(application)
    private val repository = QuranDataRepository(database.quranDao())
    val audioManager = AudioPlayerManager(application)

    private val _uiState = MutableStateFlow(
        AlSaifUiState(
            surahs = repository.surahs,
            selectedSurah = repository.surahs.firstOrNull(),
            currentAyahs = repository.getAyahsForSurah(1)
        )
    )
    val uiState: StateFlow<AlSaifUiState> = _uiState.asStateFlow()

    val audioState: StateFlow<AudioPlaybackState> = audioManager.state

    init {
        // Observe Bookmarks
        viewModelScope.launch {
            repository.getAllBookmarks().collect { list ->
                _uiState.update { state ->
                    val currentSurahNum = state.selectedSurah?.number ?: 1
                    val currentBookmarks = list
                        .filter { it.surahNumber == currentSurahNum }
                        .map { it.ayahNumber }
                        .toSet()
                    state.copy(
                        bookmarks = list,
                        currentSurahBookmarkedAyahs = currentBookmarks
                    )
                }
            }
        }

        // Observe Memorized Count
        viewModelScope.launch {
            repository.getMemorizedCount().collect { count ->
                _uiState.update { it.copy(memorizedCount = count) }
            }
        }

        // Observe Memorization Records for current Surah
        viewModelScope.launch {
            repository.getAllMemorizationRecords().collect { records ->
                updateCurrentSurahStatuses(records)
            }
        }

        // Load Last Read & Preferences
        viewModelScope.launch {
            val (lastSurah, lastAyah) = repository.getLastRead()
            val target = repository.getDailyGoalTarget()
            _uiState.update {
                it.copy(
                    lastReadSurah = lastSurah,
                    lastReadAyah = lastAyah,
                    dailyGoalTarget = target
                )
            }
        }

        // Handle auto-advance on verse completion
        audioManager.onAyahCompletedListener = { surah, ayah ->
            viewModelScope.launch {
                val nextAyahNum = ayah + 1
                val ayahs = _uiState.value.currentAyahs
                val nextAyah = ayahs.find { it.ayahNumber == nextAyahNum }
                if (nextAyah != null) {
                    audioManager.playAyah(surah, nextAyahNum, nextAyah.arabicText, 1)
                    repository.setLastRead(surah, nextAyahNum)
                }
            }
        }
    }

    private fun updateCurrentSurahStatuses(records: List<com.example.data.local.MemorizationRecordEntity>) {
        val currentSurahNum = _uiState.value.selectedSurah?.number ?: 1
        val map = records
            .filter { it.surahNumber == currentSurahNum }
            .associate {
                it.ayahNumber to when (it.status) {
                    "MEMORIZED" -> MemorizationStatus.MEMORIZED
                    "MEMORIZING" -> MemorizationStatus.MEMORIZING
                    "NEEDS_REVIEW" -> MemorizationStatus.NEEDS_REVIEW
                    else -> MemorizationStatus.NOT_STARTED
                }
            }
        _uiState.update { it.copy(currentSurahMemorizationStatuses = map) }
    }

    fun selectSurah(surah: Surah, targetAyah: Int = 1) {
        val ayahs = repository.getAyahsForSurah(surah.number)
        _uiState.update { state ->
            val bookmarks = state.bookmarks
                .filter { it.surahNumber == surah.number }
                .map { it.ayahNumber }
                .toSet()
            state.copy(
                selectedSurah = surah,
                currentAyahs = ayahs,
                currentSurahBookmarkedAyahs = bookmarks,
                lastReadSurah = surah.number,
                lastReadAyah = targetAyah
            )
        }
        viewModelScope.launch {
            repository.setLastRead(surah.number, targetAyah)
        }
    }

    fun toggleBookmark(ayah: Ayah) {
        viewModelScope.launch {
            val surah = _uiState.value.selectedSurah ?: return@launch
            repository.toggleBookmark(surah, ayah)
        }
    }

    fun cycleMemorizationStatus(ayah: Ayah) {
        viewModelScope.launch {
            val current = _uiState.value.currentSurahMemorizationStatuses[ayah.ayahNumber]
                ?: MemorizationStatus.NOT_STARTED
            val next = when (current) {
                MemorizationStatus.NOT_STARTED -> MemorizationStatus.MEMORIZING
                MemorizationStatus.MEMORIZING -> MemorizationStatus.MEMORIZED
                MemorizationStatus.MEMORIZED -> MemorizationStatus.NEEDS_REVIEW
                MemorizationStatus.NEEDS_REVIEW -> MemorizationStatus.NOT_STARTED
            }
            repository.updateMemorizationStatus(ayah.surahNumber, ayah.ayahNumber, next)
        }
    }

    fun setMemorizationStatus(ayah: Ayah, status: MemorizationStatus) {
        viewModelScope.launch {
            repository.updateMemorizationStatus(ayah.surahNumber, ayah.ayahNumber, status)
        }
    }

    fun playAyahWithLoops(ayah: Ayah, loops: Int) {
        audioManager.playAyah(ayah.surahNumber, ayah.ayahNumber, ayah.arabicText, loops)
        viewModelScope.launch {
            repository.setLastRead(ayah.surahNumber, ayah.ayahNumber)
            repository.recordRepetition(ayah.surahNumber, ayah.ayahNumber)
        }
    }

    fun pauseAudio() {
        audioManager.pause()
    }

    fun resumeAudio() {
        audioManager.resume()
    }

    fun setPlaybackSpeed(speed: Float) {
        audioManager.setPlaybackSpeed(speed)
    }

    fun setReciter(reciter: Reciter) {
        audioManager.setReciter(reciter)
    }

    fun setDailyGoal(target: Int) {
        _uiState.update { it.copy(dailyGoalTarget = target) }
        viewModelScope.launch {
            repository.setDailyGoalTarget(target)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun cycleThemeMode() {
        val next = when (_uiState.value.themeMode) {
            ThemeMode.OBSIDIAN_EMERALD -> ThemeMode.MIDNIGHT_VELVET
            ThemeMode.MIDNIGHT_VELVET -> ThemeMode.CELESTIAL_PEARL
            ThemeMode.CELESTIAL_PEARL -> ThemeMode.OBSIDIAN_EMERALD
        }
        setThemeMode(next)
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
