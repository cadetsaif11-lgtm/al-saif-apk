package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ReciterCategory(val label: String) {
    ALL("All Reciters"),
    HARAMAIN_MAKKAH("Masjid Al-Haram (Makkah)"),
    HARAMAIN_MADINAH("Masjid An-Nabawi (Madinah)"),
    CLASSIC_MASTERS("World Renowned Masters")
}

data class Reciter(
    val id: String,
    val name: String,
    val mosqueTitle: String,
    val style: String,
    val folder: String,
    val category: ReciterCategory = ReciterCategory.HARAMAIN_MAKKAH
)

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurah: Int = 1,
    val currentAyah: Int = 1,
    val currentAyahText: String = "",
    val currentLoopIndex: Int = 1,
    val targetLoops: Int = 1, // 1 = play once, 3, 5, 10, or 999 for continuous
    val playbackSpeed: Float = 1.0f,
    val currentReciter: Reciter = Reciters.list[0],
    val errorMessage: String? = null
)

object Reciters {
    val list = listOf(
        // Haramain - Makkah Mukarramah
        Reciter(
            id = "dossary",
            name = "Sheikh Yasser Al-Dosari",
            mosqueTitle = "Imam of Masjid Al-Haram, Makkah",
            style = "Emotional, Dynamic & Powerful Haramain Recitation",
            folder = "Yasser_Ad-Dussary_128kbps",
            category = ReciterCategory.HARAMAIN_MAKKAH
        ),
        Reciter(
            id = "sudais",
            name = "Sheikh Abdur-Rahman As-Sudais",
            mosqueTitle = "Chief Imam of Masjid Al-Haram, Makkah",
            style = "Iconic, Revered & Melodic Makkah Taraweeh Voice (192 kbps)",
            folder = "Abdurrahmaan_As-Sudais_192kbps",
            category = ReciterCategory.HARAMAIN_MAKKAH
        ),
        Reciter(
            id = "muaiqly",
            name = "Sheikh Maher Al-Muaiqly",
            mosqueTitle = "Imam of Masjid Al-Haram, Makkah",
            style = "Heartwarming, Smooth & Serene Haramain Pace",
            folder = "MaherAlMuaiqly128kbps",
            category = ReciterCategory.HARAMAIN_MAKKAH
        ),
        Reciter(
            id = "shuraim",
            name = "Sheikh Saud Ash-Shuraim",
            mosqueTitle = "Former Senior Imam of Masjid Al-Haram",
            style = "Majestic, Precise & Resonant Classical Style",
            folder = "Saood_ash-Shuraym_128kbps",
            category = ReciterCategory.HARAMAIN_MAKKAH
        ),
        Reciter(
            id = "juhany",
            name = "Sheikh Abdullah Awad Al-Juhany",
            mosqueTitle = "Imam of Masjid Al-Haram, Makkah",
            style = "Soulful Makkah Taraweeh Live Atmosphere",
            folder = "Abdullaah_3awwaad_Al-Juhaynee_128kbps",
            category = ReciterCategory.HARAMAIN_MAKKAH
        ),
        Reciter(
            id = "alijaber",
            name = "Sheikh Ali Jaber (Rahimahullah)",
            mosqueTitle = "Historic Legendary Imam of Masjid Al-Haram",
            style = "Unforgettable Golden Era Taraweeh Atmosphere",
            folder = "Ali_Jaber_64kbps",
            category = ReciterCategory.HARAMAIN_MAKKAH
        ),

        // Haramain - Madinah Munawwarah
        Reciter(
            id = "hudhaify",
            name = "Sheikh Ali Al-Hudhaify",
            mosqueTitle = "Chief Imam of Masjid An-Nabawi, Madinah",
            style = "Exemplary Classical Tajweed & Distinct Articulation",
            folder = "Hudhaify_128kbps",
            category = ReciterCategory.HARAMAIN_MADINAH
        ),
        Reciter(
            id = "ayyoub",
            name = "Sheikh Muhammad Ayyub (Rahimahullah)",
            mosqueTitle = "Legendary Imam of Masjid An-Nabawi, Madinah",
            style = "Pristine Hijazi Cadence with Crystal Resonance",
            folder = "Muhammad_Ayyoub_128kbps",
            category = ReciterCategory.HARAMAIN_MADINAH
        ),
        Reciter(
            id = "budair",
            name = "Sheikh Salah Al-Budair",
            mosqueTitle = "Imam of Masjid An-Nabawi, Madinah",
            style = "Gentle, Clean & Melodic Madinah Recitation",
            folder = "Salah_Al_Budair_128kbps",
            category = ReciterCategory.HARAMAIN_MADINAH
        ),

        // Renowned Masters & Studio HD Reciters
        Reciter(
            id = "basfar",
            name = "Sheikh Abdullah Basfar",
            mosqueTitle = "Crystal-Clear Studio HD (192 kbps)",
            style = "Ultra-Crisp Studio Master Recording with Pristine Clarity",
            folder = "Abdullah_Basfar_192kbps",
            category = ReciterCategory.CLASSIC_MASTERS
        ),
        Reciter(
            id = "alafasy",
            name = "Sheikh Mishary Rashid Alafasy",
            mosqueTitle = "Grand Mosque of Kuwait",
            style = "Crystal Clear, Melodic Murattal (Ideal for Beginners)",
            folder = "Alafasy_128kbps",
            category = ReciterCategory.CLASSIC_MASTERS
        ),
        Reciter(
            id = "husary",
            name = "Sheikh Mahmoud Khalil Al-Husary",
            mosqueTitle = "Master of Tajweed, Egypt",
            style = "Measured Mu'allim Cadence for Hifz & Pronunciation",
            folder = "Husary_128kbps",
            category = ReciterCategory.CLASSIC_MASTERS
        ),
        Reciter(
            id = "abdulbasit",
            name = "Sheikh Abdul Basit Abdul Samad",
            mosqueTitle = "Legendary World Icon",
            style = "Timeless Golden-Age Egyptian Murattal Resonance (192 kbps)",
            folder = "Abdul_Basit_Murattal_192kbps",
            category = ReciterCategory.CLASSIC_MASTERS
        ),
        Reciter(
            id = "ghamadi",
            name = "Sheikh Saad Al-Ghamdi",
            mosqueTitle = "Saudi Arabia",
            style = "Gentle, Emotional & Soothing Recitation",
            folder = "Ghamadi_40kbps",
            category = ReciterCategory.CLASSIC_MASTERS
        )
    )
}

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _state = MutableStateFlow(AudioPlaybackState())
    val state: StateFlow<AudioPlaybackState> = _state.asStateFlow()

    var onAyahCompletedListener: ((surah: Int, ayah: Int) -> Unit)? = null

    fun playAyah(
        surahNumber: Int,
        ayahNumber: Int,
        arabicSnippet: String = "",
        targetLoops: Int? = null
    ) {
        val loops = targetLoops ?: _state.value.targetLoops
        _state.value = _state.value.copy(
            isLoading = true,
            currentSurah = surahNumber,
            currentAyah = ayahNumber,
            currentAyahText = arabicSnippet,
            targetLoops = loops,
            currentLoopIndex = 1,
            errorMessage = null
        )

        val surahStr = surahNumber.toString().padStart(3, '0')
        val ayahStr = ayahNumber.toString().padStart(3, '0')
        val reciterFolder = _state.value.currentReciter.folder
        val audioUrl = "https://everyayah.com/data/$reciterFolder/$surahStr$ayahStr.mp3"

        prepareAndPlay(audioUrl)
    }

    private fun prepareAndPlay(url: String) {
        scope.launch(Dispatchers.IO) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    // Critical Fix: Use CONTENT_TYPE_MUSIC so Android HAL does NOT apply voice-filtering/AGC/noise-gate
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setVolume(1.0f, 1.0f) // Ensure full clean audio gain
                    setDataSource(url)
                    setOnPreparedListener { mp ->
                        mp.start()
                        // Only apply playback parameters if non-standard to preserve pristine acoustic quality
                        if (_state.value.playbackSpeed != 1.0f) {
                            applyPlaybackSpeedInternal(mp, _state.value.playbackSpeed)
                        }
                        _state.value = _state.value.copy(
                            isPlaying = true,
                            isLoading = false
                        )
                    }
                    setOnCompletionListener {
                        handleAyahCompletion()
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AudioPlayerManager", "MediaPlayer error: what=$what, extra=$extra")
                        _state.value = _state.value.copy(
                            isPlaying = false,
                            isLoading = false,
                            errorMessage = "Unable to stream recitation. Check internet connection."
                        )
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Exception preparing audio", e)
                _state.value = _state.value.copy(
                    isPlaying = false,
                    isLoading = false,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }

    private fun applyPlaybackSpeedInternal(mp: MediaPlayer, speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = mp.playbackParams
                    .setSpeed(speed)
                    .setPitch(1.0f)
                    .setAudioFallbackMode(PlaybackParams.AUDIO_FALLBACK_MODE_DEFAULT)
                mp.playbackParams = params
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Error applying speed", e)
            }
        }
    }

    private fun handleAyahCompletion() {
        val currentLoop = _state.value.currentLoopIndex
        val targetLoops = _state.value.targetLoops

        if (currentLoop < targetLoops) {
            // Repeat this verse for memorization
            _state.value = _state.value.copy(currentLoopIndex = currentLoop + 1)
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
        } else {
            // Verse repetition done, notify listener for auto-advance or stop
            _state.value = _state.value.copy(isPlaying = false, currentLoopIndex = 1)
            onAyahCompletedListener?.invoke(_state.value.currentSurah, _state.value.currentAyah)
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            _state.value = _state.value.copy(isPlaying = false)
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error pausing", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
            _state.value = _state.value.copy(isPlaying = true)
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error resuming", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            _state.value = _state.value.copy(isPlaying = false, isLoading = false)
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error stopping", e)
        }
    }

    fun setTargetLoops(loops: Int) {
        _state.value = _state.value.copy(targetLoops = loops)
    }

    fun setPlaybackSpeed(speed: Float) {
        _state.value = _state.value.copy(playbackSpeed = speed)
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                applyPlaybackSpeedInternal(mp, speed)
            }
        }
    }

    fun setReciter(reciter: Reciter) {
        val wasPlaying = _state.value.isPlaying
        _state.value = _state.value.copy(currentReciter = reciter)
        if (wasPlaying) {
            playAyah(_state.value.currentSurah, _state.value.currentAyah)
        }
    }

    fun release() {
        scope.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
