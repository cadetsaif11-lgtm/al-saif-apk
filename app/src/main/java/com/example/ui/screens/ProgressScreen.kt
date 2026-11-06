package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.model.MemorizationStatus
import com.example.data.model.Surah
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPill
import com.example.ui.theme.AmiriFont
import com.example.ui.theme.CinzelFont
import com.example.ui.theme.LocalGlassTheme
import com.example.ui.theme.ThemeMode

@Composable
fun ProgressScreen(
    memorizedCount: Int,
    dailyGoalTarget: Int,
    bookmarks: List<BookmarkEntity>,
    memorizationStatuses: Map<String, MemorizationStatus>,
    currentThemeMode: ThemeMode,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetDailyGoal: (Int) -> Unit,
    onOpenBookmark: (Int, Int) -> Unit,
    activeReciter: com.example.audio.Reciter = com.example.audio.Reciters.list[0],
    onSetReciter: (com.example.audio.Reciter) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassTheme.current
    val totalVersesInQuran = 6236
    val percentage = (memorizedCount.toFloat() / totalVersesInQuran * 100).coerceAtLeast(0.1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("progress_screen_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = glass.accentGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PROGRESS & GOALS",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = glass.accentGold,
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = "Track your Quranic memorization milestones",
                    fontSize = 12.sp,
                    color = glass.textMuted
                )
            }
        }

        // Theme Switcher Card (Dark Mode Toggle)
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_selection_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = glass.accentGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INTERFACE THEME",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = glass.textPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        val isSelected = mode == currentThemeMode
                        val label = when (mode) {
                            ThemeMode.OBSIDIAN_EMERALD -> "Obsidian"
                            ThemeMode.MIDNIGHT_VELVET -> "Midnight"
                            ThemeMode.CELESTIAL_PEARL -> "Pearl"
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) glass.accentGold else glass.surfaceGlass)
                                .border(
                                    BorderStroke(1.dp, if (isSelected) glass.accentGold else glass.borderGlass),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSetThemeMode(mode) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF1E1502) else glass.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Default Recitation Imam Card
        item {
            var showReciterDialog by remember { mutableStateOf(false) }

            if (showReciterDialog) {
                com.example.ui.components.ReciterSelectionDialog(
                    currentReciter = activeReciter,
                    onDismiss = { showReciterDialog = false },
                    onSelectReciter = onSetReciter
                )
            }

            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reciter_preference_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = glass.accentGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEFAULT RECITATION IMAM",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = glass.textPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    TextButton(onClick = { showReciterDialog = true }) {
                        Text("Change", color = glass.accentGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(glass.surfaceGlass)
                        .clickable { showReciterDialog = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeReciter.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = glass.accentGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeReciter.mosqueTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = glass.accentTeal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeReciter.style,
                            fontSize = 11.sp,
                            color = glass.textSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = glass.accentGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Hero Memorization Progress Card
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("memorization_stats_card"),
                shape = RoundedCornerShape(24.dp),
                borderColor = glass.accentGold.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NOBLE QURAN MEMORIZATION",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = glass.accentGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Circular Progress Graphic
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { (percentage / 100f).coerceIn(0.01f, 1f) },
                            modifier = Modifier.size(130.dp),
                            color = glass.accentGold,
                            trackColor = glass.surfaceGlass,
                            strokeWidth = 10.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${"%.1f".format(percentage)}%",
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = glass.textPrimary
                            )
                            Text(
                                text = "Completed",
                                fontSize = 11.sp,
                                color = glass.accentTeal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = memorizedCount.toString(),
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = glass.accentGold
                            )
                            Text(
                                text = "Verses Memorized",
                                fontSize = 11.sp,
                                color = glass.textSecondary
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .height(36.dp)
                                .width(1.dp),
                            color = glass.borderGlass
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (totalVersesInQuran - memorizedCount).toString(),
                                fontFamily = CinzelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = glass.textPrimary
                            )
                            Text(
                                text = "Remaining Ayahs",
                                fontSize = 11.sp,
                                color = glass.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // Daily Memorization Goal Target
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_goal_card"),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "DAILY MEMORIZATION GOAL",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = glass.textPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Target: $dailyGoalTarget verses per day",
                            fontSize = 12.sp,
                            color = glass.accentTeal
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Active Streak",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFF97316)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(3, 5, 7, 10).forEach { goal ->
                        val isSelected = goal == dailyGoalTarget
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) glass.accentTeal else glass.surfaceGlass)
                                .clickable { onSetDailyGoal(goal) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$goal / day",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else glass.textPrimary
                            )
                        }
                    }
                }
            }
        }

        // Bookmarks Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SAVED BOOKMARKS (${bookmarks.size})",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = glass.textMuted,
                    letterSpacing = 1.sp
                )
            }
        }

        if (bookmarks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(glass.surfaceGlass)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = glass.textMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No bookmarks yet.\nTap the bookmark icon in the reader to save verses.",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = glass.textMuted
                        )
                    }
                }
            }
        } else {
            items(bookmarks, key = { it.id }) { bookmark ->
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bookmark_${bookmark.id}"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 2.dp,
                    onClick = { onOpenBookmark(bookmark.surahNumber, bookmark.ayahNumber) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Surah ${bookmark.surahName} • Ayah ${bookmark.ayahNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = glass.accentGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bookmark.translationSnippet,
                                fontSize = 12.sp,
                                color = glass.textSecondary,
                                maxLines = 2
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = glass.accentGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Offline Quran Indicator
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(glass.surfaceGlass.copy(alpha = 0.5f))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = glass.accentTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "100% Offline Accessible",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = glass.textPrimary
                        )
                        Text(
                            text = "All 114 Surahs and memorization records work seamlessly without internet.",
                            fontSize = 11.sp,
                            color = glass.textMuted
                        )
                    }
                }
            }
        }
    }
}
