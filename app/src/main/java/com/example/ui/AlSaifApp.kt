package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.screens.*
import com.example.ui.theme.AlSaifQuranTheme
import com.example.ui.theme.LocalGlassTheme

enum class AppDestination(val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    QURAN("Quran", Icons.Default.MenuBook, Icons.Outlined.MenuBook),
    MEMORIZE("Memorize", Icons.Default.Psychology, Icons.Outlined.Psychology),
    TAJWEED("Tajweed", Icons.Default.AutoStories, Icons.Outlined.AutoStories),
    PROGRESS("Progress", Icons.Default.TrendingUp, Icons.Outlined.TrendingUp)
}

enum class ScreenState {
    MAIN_TABS,
    SURAH_READER
}

@Composable
fun AlSaifApp(
    viewModel: AlSaifViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf(AppDestination.QURAN) }
    var screenState by remember { mutableStateOf(ScreenState.MAIN_TABS) }

    AlSaifQuranTheme(themeMode = uiState.themeMode) {
        val glass = LocalGlassTheme.current

        LiquidGlassSurface(modifier = modifier) {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (screenState == ScreenState.MAIN_TABS) {
                        LiquidGlassBottomBar(
                            currentDestination = currentDestination,
                            onDestinationSelected = { currentDestination = it }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (screenState) {
                        ScreenState.MAIN_TABS -> {
                            when (currentDestination) {
                                AppDestination.QURAN -> {
                                    HomeScreen(
                                        surahs = uiState.surahs,
                                        lastReadSurah = uiState.lastReadSurah,
                                        lastReadAyah = uiState.lastReadAyah,
                                        memorizedCount = uiState.memorizedCount,
                                        currentThemeMode = uiState.themeMode,
                                        onThemeToggle = { viewModel.cycleThemeMode() },
                                        onSelectSurah = { surah ->
                                            viewModel.selectSurah(surah)
                                            screenState = ScreenState.SURAH_READER
                                        },
                                        onStartHifzForSurah = { surah ->
                                            viewModel.selectSurah(surah)
                                            currentDestination = AppDestination.MEMORIZE
                                        }
                                    )
                                }
                                AppDestination.MEMORIZE -> {
                                    MemorizeStudioScreen(
                                        surahs = uiState.surahs,
                                        selectedSurah = uiState.selectedSurah ?: uiState.surahs.first(),
                                        ayahs = uiState.currentAyahs,
                                        audioState = audioState,
                                        memorizationStatuses = uiState.currentSurahMemorizationStatuses,
                                        onSelectSurah = { surah -> viewModel.selectSurah(surah) },
                                        onPlayAyahWithLoop = { ayah, loops ->
                                            viewModel.playAyahWithLoops(ayah, loops)
                                        },
                                        onPauseAudio = { viewModel.pauseAudio() },
                                        onUpdateStatus = { ayah, status ->
                                            viewModel.setMemorizationStatus(ayah, status)
                                        },
                                        onSetReciter = { viewModel.setReciter(it) }
                                    )
                                }
                                AppDestination.TAJWEED -> {
                                    TajweedScreen()
                                }
                                AppDestination.PROGRESS -> {
                                    ProgressScreen(
                                        memorizedCount = uiState.memorizedCount,
                                        dailyGoalTarget = uiState.dailyGoalTarget,
                                        bookmarks = uiState.bookmarks,
                                        memorizationStatuses = emptyMap(),
                                        currentThemeMode = uiState.themeMode,
                                        onSetThemeMode = { viewModel.setThemeMode(it) },
                                        onSetDailyGoal = { viewModel.setDailyGoal(it) },
                                        onOpenBookmark = { surahNum, ayahNum ->
                                            val surah = uiState.surahs.find { it.number == surahNum }
                                            if (surah != null) {
                                                viewModel.selectSurah(surah, ayahNum)
                                                screenState = ScreenState.SURAH_READER
                                            }
                                        },
                                        activeReciter = audioState.currentReciter,
                                        onSetReciter = { viewModel.setReciter(it) }
                                    )
                                }
                            }
                        }

                        ScreenState.SURAH_READER -> {
                            BackHandler {
                                screenState = ScreenState.MAIN_TABS
                            }

                            val activeSurah = uiState.selectedSurah ?: uiState.surahs.first()
                            SurahReaderScreen(
                                surah = activeSurah,
                                ayahs = uiState.currentAyahs,
                                audioState = audioState,
                                bookmarkedAyahNumbers = uiState.currentSurahBookmarkedAyahs,
                                memorizationStatuses = uiState.currentSurahMemorizationStatuses,
                                onBack = { screenState = ScreenState.MAIN_TABS },
                                onPlayAyah = { ayah, loops ->
                                    viewModel.playAyahWithLoops(ayah, loops ?: 1)
                                },
                                onPauseAudio = { viewModel.pauseAudio() },
                                onResumeAudio = { viewModel.resumeAudio() },
                                onSetPlaybackSpeed = { viewModel.setPlaybackSpeed(it) },
                                onSetReciter = { viewModel.setReciter(it) },
                                onToggleBookmark = { viewModel.toggleBookmark(it) },
                                onCycleMemorizationStatus = { viewModel.cycleMemorizationStatus(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidGlassBottomBar(
    currentDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassTheme.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(glass.surfaceGlass.copy(alpha = 0.92f))
            .border(BorderStroke(1.dp, glass.borderGlass), RoundedCornerShape(26.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("liquid_bottom_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestination.values().forEach { destination ->
                val isSelected = destination == currentDestination
                val icon = if (isSelected) destination.activeIcon else destination.inactiveIcon

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onDestinationSelected(destination) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) glass.accentGold.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = destination.label,
                            tint = if (isSelected) glass.accentGold else glass.textMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = destination.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) glass.accentGold else glass.textMuted
                    )
                }
            }
        }
    }
}
