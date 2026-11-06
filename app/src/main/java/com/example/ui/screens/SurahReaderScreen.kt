package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlaybackState
import com.example.audio.Reciter
import com.example.audio.Reciters
import com.example.data.model.*
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPill
import com.example.ui.components.LiquidTajweedBadge
import com.example.ui.theme.AmiriFont
import com.example.ui.theme.CinzelFont
import com.example.ui.theme.LocalGlassTheme

enum class TranslationType(val label: String) {
    SAHIH_INTERNATIONAL("English (Sahih)"),
    URDU("Urdu (Jalandhari)"),
    NONE("Hide Translation")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderScreen(
    surah: Surah,
    ayahs: List<Ayah>,
    audioState: AudioPlaybackState,
    bookmarkedAyahNumbers: Set<Int>,
    memorizationStatuses: Map<Int, MemorizationStatus>,
    onBack: () -> Unit,
    onPlayAyah: (Ayah, Int?) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onSetReciter: (Reciter) -> Unit,
    onToggleBookmark: (Ayah) -> Unit,
    onCycleMemorizationStatus: (Ayah) -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassTheme.current
    val listState = rememberLazyListState()

    var activeTranslation by remember { mutableStateOf(TranslationType.SAHIH_INTERNATIONAL) }
    var showTransliteration by remember { mutableStateOf(false) }
    var tajweedModeActive by remember { mutableStateOf(true) }
    var memorizationMaskActive by remember { mutableStateOf(false) }
    var revealedAyahs by remember { mutableStateOf(setOf<Int>()) }

    var selectedTajweedSpan by remember { mutableStateOf<TajweedSpan?>(null) }
    var showReciterDialog by remember { mutableStateOf(false) }
    var arabicFontSizeSp by remember { mutableStateOf(26) }

    // Tajweed Rule Bottom Sheet Modal
    selectedTajweedSpan?.let { span ->
        ModalBottomSheet(
            onDismissRequest = { selectedTajweedSpan = null },
            containerColor = glass.surfaceGlass,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(glass.borderGlass)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidTajweedBadge(
                        ruleTitle = span.rule.title,
                        ruleColor = span.rule.color
                    )
                    Text(
                        text = span.rule.arabicTitle,
                        fontFamily = AmiriFont,
                        fontSize = 24.sp,
                        color = span.rule.color
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Rule Definition:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = glass.textSecondary
                )
                Text(
                    text = span.rule.description,
                    fontSize = 14.sp,
                    color = glass.textPrimary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Rule Letters:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = glass.textSecondary
                )
                Text(
                    text = span.rule.letters,
                    fontFamily = AmiriFont,
                    fontSize = 18.sp,
                    color = glass.accentGold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Pronunciation Tip:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = glass.textSecondary
                )
                Text(
                    text = span.phoneticTip,
                    fontSize = 14.sp,
                    color = glass.accentTeal,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Reciter Dialog
    if (showReciterDialog) {
        com.example.ui.components.ReciterSelectionDialog(
            currentReciter = audioState.currentReciter,
            onDismiss = { showReciterDialog = false },
            onSelectReciter = onSetReciter
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("surah_reader_column"),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = if (audioState.isPlaying || audioState.isLoading) 130.dp else 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Navigation & Action Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .testTag("back_button")
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(glass.surfaceGlass)
                            .border(BorderStroke(1.dp, glass.borderGlass), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = glass.accentGold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = surah.englishName,
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = glass.textPrimary
                        )
                        Text(
                            text = "${surah.revelationType.title.take(6)} • ${surah.numberOfAyahs} Ayahs",
                            fontSize = 11.sp,
                            color = glass.textMuted
                        )
                    }

                    // Reciter selector button
                    IconButton(
                        onClick = { showReciterDialog = true },
                        modifier = Modifier
                            .testTag("reciter_dialog_button")
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(glass.surfaceGlass)
                            .border(BorderStroke(1.dp, glass.borderGlass), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Reciter",
                            tint = glass.accentTeal
                        )
                    }
                }
            }

            // Quick Toolbar: Translation, Transliteration, Tajweed, Mask
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(glass.surfaceGlass)
                        .border(BorderStroke(1.dp, glass.borderGlass), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Translation Toggle
                    TextButton(
                        onClick = {
                            activeTranslation = when (activeTranslation) {
                                TranslationType.SAHIH_INTERNATIONAL -> TranslationType.URDU
                                TranslationType.URDU -> TranslationType.NONE
                                TranslationType.NONE -> TranslationType.SAHIH_INTERNATIONAL
                            }
                        },
                        modifier = Modifier.testTag("translation_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = glass.accentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = activeTranslation.label.take(8),
                            fontSize = 11.sp,
                            color = glass.accentGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Tajweed Mode Toggle
                    IconButton(
                        onClick = { tajweedModeActive = !tajweedModeActive },
                        modifier = Modifier
                            .testTag("tajweed_mode_toggle")
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tajweedModeActive) glass.accentTeal.copy(alpha = 0.2f) else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Tajweed Mode",
                            tint = if (tajweedModeActive) glass.accentTeal else glass.textMuted
                        )
                    }

                    // Memorization Mask Mode Toggle
                    IconButton(
                        onClick = {
                            memorizationMaskActive = !memorizationMaskActive
                            if (!memorizationMaskActive) revealedAyahs = emptySet()
                        },
                        modifier = Modifier
                            .testTag("memorize_mask_toggle")
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (memorizationMaskActive) glass.accentGold.copy(alpha = 0.2f) else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = if (memorizationMaskActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Hifz Mask Mode",
                            tint = if (memorizationMaskActive) glass.accentGold else glass.textMuted
                        )
                    }

                    // Font size adjustments
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (arabicFontSizeSp > 20) arabicFontSizeSp -= 2 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A-", color = glass.textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { if (arabicFontSizeSp < 40) arabicFontSizeSp += 2 },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("A+", color = glass.accentGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Surah Ornate Card & Bismillah
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    borderColor = glass.accentGold.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = surah.arabicName,
                            fontFamily = AmiriFont,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = glass.accentGold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = surah.englishMeaning,
                            fontSize = 14.sp,
                            color = glass.textSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bismillah banner (All Surahs except Surah At-Tawbah #9)
                        if (surah.number != 9) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(glass.surfaceGlass.copy(alpha = 0.7f))
                                    .border(BorderStroke(1.dp, glass.borderGlass), RoundedCornerShape(16.dp))
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    fontFamily = AmiriFont,
                                    fontSize = 24.sp,
                                    color = glass.textPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Verses List
            items(ayahs, key = { "${it.surahNumber}:${it.ayahNumber}" }) { ayah ->
                val isCurrentlyPlaying = audioState.currentSurah == ayah.surahNumber &&
                        audioState.currentAyah == ayah.ayahNumber &&
                        audioState.isPlaying

                val isBookmarked = bookmarkedAyahNumbers.contains(ayah.ayahNumber)
                val hifzStatus = memorizationStatuses[ayah.ayahNumber] ?: MemorizationStatus.NOT_STARTED
                val isMasked = memorizationMaskActive && !revealedAyahs.contains(ayah.ayahNumber)

                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ayah_card_${ayah.ayahNumber}"),
                    shape = RoundedCornerShape(20.dp),
                    borderColor = if (isCurrentlyPlaying) glass.accentTeal else null,
                    backgroundColor = if (isCurrentlyPlaying) glass.accentTeal.copy(alpha = 0.15f) else null
                ) {
                    // Ayah Header: Number + Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Ornate Ayah Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(glass.surfaceGlass)
                                .border(BorderStroke(1.dp, glass.accentGold.copy(alpha = 0.6f)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ayah.ayahNumber.toString(),
                                fontFamily = CinzelFont,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = glass.accentGold
                            )
                        }

                        // Right action items: Memorization Status, Bookmark, Audio Play
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Memorization Status Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        when (hifzStatus) {
                                            MemorizationStatus.MEMORIZED -> Color(0xFF10B981).copy(alpha = 0.2f)
                                            MemorizationStatus.MEMORIZING -> glass.accentGold.copy(alpha = 0.2f)
                                            MemorizationStatus.NEEDS_REVIEW -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                            MemorizationStatus.NOT_STARTED -> glass.surfaceGlass
                                        }
                                    )
                                    .border(
                                        BorderStroke(
                                            0.8.dp,
                                            when (hifzStatus) {
                                                MemorizationStatus.MEMORIZED -> Color(0xFF10B981)
                                                MemorizationStatus.MEMORIZING -> glass.accentGold
                                                MemorizationStatus.NEEDS_REVIEW -> Color(0xFFEF4444)
                                                MemorizationStatus.NOT_STARTED -> glass.borderGlass
                                            }
                                        ),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onCycleMemorizationStatus(ayah) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = hifzStatus.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (hifzStatus) {
                                        MemorizationStatus.MEMORIZED -> Color(0xFF10B981)
                                        MemorizationStatus.MEMORIZING -> glass.accentGold
                                        MemorizationStatus.NEEDS_REVIEW -> Color(0xFFEF4444)
                                        MemorizationStatus.NOT_STARTED -> glass.textMuted
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Bookmark button
                            IconButton(
                                onClick = { onToggleBookmark(ayah) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) glass.accentGold else glass.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Play Verse Button
                            IconButton(
                                onClick = {
                                    if (isCurrentlyPlaying) onPauseAudio() else onPlayAyah(ayah, 1)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrentlyPlaying) glass.accentTeal else glass.surfaceGlass)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Verse",
                                    tint = if (isCurrentlyPlaying) Color.Black else glass.accentGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Arabic Verse Text (Amiri font) with Tap-to-Reveal in Mask Mode
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = memorizationMaskActive) {
                                revealedAyahs = if (revealedAyahs.contains(ayah.ayahNumber)) {
                                    revealedAyahs - ayah.ayahNumber
                                } else {
                                    revealedAyahs + ayah.ayahNumber
                                }
                            }
                    ) {
                        Text(
                            text = ayah.arabicText,
                            fontFamily = AmiriFont,
                            fontSize = arabicFontSizeSp.sp,
                            lineHeight = (arabicFontSizeSp * 1.8).sp,
                            color = glass.textPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isMasked) Modifier.blur(14.dp) else Modifier)
                        )

                        // Mask Overlay Prompt
                        if (isMasked) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(glass.surfaceGlass.copy(alpha = 0.85f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = null,
                                        tint = glass.accentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tap to Reveal (Hifz Test)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = glass.accentGold
                                    )
                                }
                            }
                        }
                    }

                    // Transliteration
                    if (showTransliteration && ayah.transliteration.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = ayah.transliteration,
                            fontSize = 13.sp,
                            color = glass.accentTeal,
                            lineHeight = 18.sp
                        )
                    }

                    // Translation Text
                    when (activeTranslation) {
                        TranslationType.SAHIH_INTERNATIONAL -> {
                            if (ayah.translationSahih.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = ayah.translationSahih,
                                    fontSize = 14.sp,
                                    color = glass.textSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                        TranslationType.URDU -> {
                            if (ayah.translationUrdu.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = ayah.translationUrdu,
                                    fontSize = 15.sp,
                                    color = glass.textSecondary,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        TranslationType.NONE -> { /* No translation displayed */ }
                    }

                    // Tajweed Badges for this verse
                    if (tajweedModeActive && ayah.tajweedRules.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tajweed:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = glass.textMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ayah.tajweedRules.forEach { span ->
                                    LiquidTajweedBadge(
                                        ruleTitle = span.rule.title.split(" ")[0],
                                        ruleColor = span.rule.color,
                                        onClick = { selectedTajweedSpan = span }
                                    )
                                }
                            }
                        }
                    }

                    // Quick Hifz Repetition Loop for this verse
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hifz Repeat Loop:",
                            fontSize = 10.sp,
                            color = glass.textMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        listOf(3, 5, 10).forEach { loopCount ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(glass.surfaceGlass)
                                    .border(BorderStroke(0.6.dp, glass.borderGlass), RoundedCornerShape(8.dp))
                                    .clickable { onPlayAyah(ayah, loopCount) }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${loopCount}x",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glass.accentGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Persistent Floating Liquid Glass Audio Player
        if (audioState.isPlaying || audioState.isLoading) {
            LiquidGlassCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("floating_audio_player"),
                shape = RoundedCornerShape(24.dp),
                elevation = 16.dp,
                borderColor = glass.accentTeal.copy(alpha = 0.6f),
                backgroundColor = glass.surfaceGlass.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Surah ${surah.englishName} : Ayah ${audioState.currentAyah}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = glass.textPrimary
                            )
                            if (audioState.targetLoops > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                LiquidPill(
                                    text = "Loop ${audioState.currentLoopIndex}/${audioState.targetLoops}",
                                    accentColor = glass.accentGold
                                )
                            }
                        }
                        Text(
                            text = audioState.currentReciter.name,
                            fontSize = 11.sp,
                            color = glass.accentTeal
                        )
                    }

                    // Speed Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(glass.cardBackground)
                            .clickable {
                                val nextSpeed = when (audioState.playbackSpeed) {
                                    1.0f -> 1.25f
                                    1.25f -> 0.75f
                                    else -> 1.0f
                                }
                                onSetPlaybackSpeed(nextSpeed)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${audioState.playbackSpeed}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = glass.accentGold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Play / Pause Button
                    IconButton(
                        onClick = {
                            if (audioState.isPlaying) onPauseAudio() else onResumeAudio()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(glass.accentGold)
                    ) {
                        if (audioState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
