package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.data.model.Ayah
import com.example.data.model.MemorizationStatus
import com.example.data.model.Surah
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPill
import com.example.ui.theme.AmiriFont
import com.example.ui.theme.CinzelFont
import com.example.ui.theme.LocalGlassTheme

enum class HifzMode(val label: String) {
    FLASHCARD("Card Recall"),
    LOOP_PLAYER("Audio Loop"),
    RECALL_QUIZ("Quiz Recall")
}

@Composable
fun MemorizeStudioScreen(
    surahs: List<Surah>,
    selectedSurah: Surah,
    ayahs: List<Ayah>,
    audioState: AudioPlaybackState,
    memorizationStatuses: Map<Int, MemorizationStatus>,
    onSelectSurah: (Surah) -> Unit,
    onPlayAyahWithLoop: (Ayah, Int) -> Unit,
    onPauseAudio: () -> Unit,
    onUpdateStatus: (Ayah, MemorizationStatus) -> Unit,
    onSetReciter: (com.example.audio.Reciter) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassTheme.current
    var currentHifzMode by remember { mutableStateOf(HifzMode.FLASHCARD) }
    var currentAyahIndex by remember { mutableStateOf(0) }
    var isRevealed by remember { mutableStateOf(false) }
    var targetLoops by remember { mutableStateOf(5) }
    var showReciterDialog by remember { mutableStateOf(false) }

    // Quiz State
    var quizSelectedOption by remember { mutableStateOf<Int?>(null) }
    var quizAnswerChecked by remember { mutableStateOf(false) }

    val currentAyah = ayahs.getOrNull(currentAyahIndex) ?: ayahs.firstOrNull()

    if (showReciterDialog) {
        com.example.ui.components.ReciterSelectionDialog(
            currentReciter = audioState.currentReciter,
            onDismiss = { showReciterDialog = false },
            onSelectReciter = onSetReciter
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("memorize_studio_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = glass.accentGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HIFZ STUDIO",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = glass.accentGold,
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = "Spaced repetition & verse memorization training",
                    fontSize = 12.sp,
                    color = glass.textMuted
                )
            }
        }

        // Surah Selector Carousel
        item {
            Column {
                Text(
                    text = "SELECT SURAH",
                    fontFamily = CinzelFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = glass.textMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Show core surahs or all
                    items(surahs.take(20)) { surah ->
                        val isSelected = surah.number == selectedSurah.number
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) glass.accentGold else glass.surfaceGlass)
                                .border(
                                    BorderStroke(1.dp, if (isSelected) glass.accentGold else glass.borderGlass),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onSelectSurah(surah)
                                    currentAyahIndex = 0
                                    isRevealed = false
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${surah.number}. ${surah.englishName}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF1E1502) else glass.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Mode Switcher Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(glass.surfaceGlass)
                    .border(BorderStroke(1.dp, glass.borderGlass), RoundedCornerShape(18.dp))
                    .padding(4.dp)
            ) {
                HifzMode.values().forEach { mode ->
                    val isSelected = mode == currentHifzMode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) glass.accentTeal.copy(alpha = 0.25f) else Color.Transparent)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (isSelected) glass.accentTeal else Color.Transparent
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                currentHifzMode = mode
                                isRevealed = false
                                quizSelectedOption = null
                                quizAnswerChecked = false
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) glass.accentTeal else glass.textSecondary
                        )
                    }
                }
            }
        }

        if (currentAyah != null) {
            // Verse Navigation Bar (Ayah X of Total)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (currentAyahIndex > 0) {
                                currentAyahIndex--
                                isRevealed = false
                                quizSelectedOption = null
                                quizAnswerChecked = false
                            }
                        },
                        enabled = currentAyahIndex > 0,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(glass.surfaceGlass)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Ayah",
                            tint = if (currentAyahIndex > 0) glass.accentGold else glass.textMuted
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Surah ${selectedSurah.englishName}",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = glass.textPrimary
                        )
                        Text(
                            text = "Ayah ${currentAyah.ayahNumber} of ${ayahs.size}",
                            fontSize = 12.sp,
                            color = glass.accentGold
                        )
                    }

                    IconButton(
                        onClick = {
                            if (currentAyahIndex < ayahs.size - 1) {
                                currentAyahIndex++
                                isRevealed = false
                                quizSelectedOption = null
                                quizAnswerChecked = false
                            }
                        },
                        enabled = currentAyahIndex < ayahs.size - 1,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(glass.surfaceGlass)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Ayah",
                            tint = if (currentAyahIndex < ayahs.size - 1) glass.accentGold else glass.textMuted
                        )
                    }
                }
            }

            // Mode Content:
            when (currentHifzMode) {
                HifzMode.FLASHCARD -> {
                    item {
                        LiquidGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hifz_flashcard"),
                            shape = RoundedCornerShape(26.dp),
                            borderColor = glass.accentGold.copy(alpha = 0.5f),
                            onClick = { isRevealed = !isRevealed }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LiquidPill(
                                    text = if (isRevealed) "Revealed (Tap to Hide)" else "Hidden (Tap to Reveal)",
                                    accentColor = if (isRevealed) glass.accentTeal else glass.accentGold
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Arabic Verse Display (Blurred if not revealed)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentAyah.arabicText,
                                        fontFamily = AmiriFont,
                                        fontSize = 28.sp,
                                        lineHeight = 50.sp,
                                        color = glass.textPrimary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(if (!isRevealed) Modifier.blur(16.dp) else Modifier)
                                    )

                                    if (!isRevealed) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(glass.surfaceGlass.copy(alpha = 0.9f))
                                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = glass.accentGold,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Recite from Memory\nTap Card to Check",
                                                textAlign = TextAlign.Center,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = glass.accentGold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Translation & Transliteration
                                Text(
                                    text = currentAyah.translationSahih,
                                    fontSize = 14.sp,
                                    color = glass.textSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                }

                HifzMode.LOOP_PLAYER -> {
                    item {
                        LiquidGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hifz_loop_player"),
                            shape = RoundedCornerShape(26.dp),
                            borderColor = glass.accentTeal.copy(alpha = 0.5f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "AUDIO REPETITION LOOP",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = glass.accentTeal,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Active Haramain/Master Reciter Selector Button
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(glass.surfaceGlass)
                                        .border(BorderStroke(0.8.dp, glass.accentGold.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
                                        .clickable { showReciterDialog = true }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = glass.accentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = audioState.currentReciter.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = glass.accentGold
                                        )
                                        Text(
                                            text = audioState.currentReciter.mosqueTitle,
                                            fontSize = 10.sp,
                                            color = glass.accentTeal
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Change",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = glass.textMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = currentAyah.arabicText,
                                    fontFamily = AmiriFont,
                                    fontSize = 26.sp,
                                    lineHeight = 46.sp,
                                    color = glass.textPrimary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Repetition Selector (3x, 5x, 10x, 20x)
                                Text(
                                    text = "Select Repetition Target:",
                                    fontSize = 12.sp,
                                    color = glass.textMuted
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(3, 5, 10, 20).forEach { loop ->
                                        val isTarget = loop == targetLoops
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isTarget) glass.accentGold else glass.surfaceGlass)
                                                .clickable { targetLoops = loop }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "${loop}x",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isTarget) Color(0xFF1E1502) else glass.textPrimary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // Big Play Loop Button
                                Button(
                                    onClick = {
                                        if (audioState.isPlaying) {
                                            onPauseAudio()
                                        } else {
                                            onPlayAyahWithLoop(currentAyah, targetLoops)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = glass.accentGold),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("start_loop_button")
                                ) {
                                    Icon(
                                        imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.Repeat,
                                        contentDescription = null,
                                        tint = Color(0xFF1E1502)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (audioState.isPlaying) {
                                            "Pause Repetition (Loop ${audioState.currentLoopIndex}/$targetLoops)"
                                        } else {
                                            "Start $targetLoops-Loop Repetition"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E1502)
                                    )
                                }
                            }
                        }
                    }
                }

                HifzMode.RECALL_QUIZ -> {
                    item {
                        LiquidGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hifz_recall_quiz"),
                            shape = RoundedCornerShape(26.dp),
                            borderColor = glass.accentGold.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "WHAT AYAH COMES NEXT?",
                                    fontFamily = CinzelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = glass.accentGold,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Current Verse (${currentAyah.ayahNumber}):",
                                    fontSize = 12.sp,
                                    color = glass.textMuted
                                )
                                Text(
                                    text = currentAyah.arabicText,
                                    fontFamily = AmiriFont,
                                    fontSize = 22.sp,
                                    color = glass.textPrimary,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                val nextAyah = ayahs.getOrNull(currentAyahIndex + 1)
                                if (nextAyah != null) {
                                    val otherAyah = ayahs.getOrNull((currentAyahIndex + 3) % ayahs.size) ?: currentAyah
                                    val options = remember(currentAyahIndex) {
                                        listOf(nextAyah, otherAyah).shuffled()
                                    }

                                    Text(
                                        text = "Select Next Verse (${currentAyah.ayahNumber + 1}):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = glass.accentTeal
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    options.forEachIndexed { idx, optAyah ->
                                        val isChosen = quizSelectedOption == idx
                                        val isCorrect = optAyah.ayahNumber == nextAyah.ayahNumber
                                        val optBg = when {
                                            quizAnswerChecked && isChosen && isCorrect -> Color(0xFF10B981).copy(alpha = 0.3f)
                                            quizAnswerChecked && isChosen && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.3f)
                                            isChosen -> glass.accentGold.copy(alpha = 0.2f)
                                            else -> glass.surfaceGlass
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(optBg)
                                                .border(BorderStroke(1.dp, glass.borderGlass), RoundedCornerShape(14.dp))
                                                .clickable {
                                                    quizSelectedOption = idx
                                                    quizAnswerChecked = true
                                                }
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                text = optAyah.arabicText,
                                                fontFamily = AmiriFont,
                                                fontSize = 18.sp,
                                                color = glass.textPrimary,
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    if (quizAnswerChecked) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        val isWinner = options.getOrNull(quizSelectedOption ?: -1)?.ayahNumber == nextAyah.ayahNumber
                                        Text(
                                            text = if (isWinner) "Masha'Allah! Correct answer!" else "Keep practicing! Review this Surah.",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isWinner) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "This is the final verse of Surah ${selectedSurah.englishName}!",
                                        fontSize = 13.sp,
                                        color = glass.accentTeal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Memorization Status Controller for Current Ayah
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "UPDATE MEMORIZATION STATUS",
                        fontFamily = CinzelFont,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = glass.textMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val currentStatus = memorizationStatuses[currentAyah.ayahNumber] ?: MemorizationStatus.NOT_STARTED

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MemorizationStatus.values().forEach { status ->
                            val isSelected = status == currentStatus
                            val statusColor = when (status) {
                                MemorizationStatus.MEMORIZED -> Color(0xFF10B981)
                                MemorizationStatus.MEMORIZING -> glass.accentGold
                                MemorizationStatus.NEEDS_REVIEW -> Color(0xFFEF4444)
                                MemorizationStatus.NOT_STARTED -> glass.textMuted
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) statusColor.copy(alpha = 0.25f) else glass.surfaceGlass)
                                    .border(
                                        BorderStroke(1.dp, if (isSelected) statusColor else glass.borderGlass),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onUpdateStatus(currentAyah, status) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = status.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) statusColor else glass.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
