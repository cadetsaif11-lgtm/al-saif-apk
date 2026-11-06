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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RevelationType
import com.example.data.model.Surah
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPill
import com.example.ui.theme.AmiriFont
import com.example.ui.theme.CinzelFont
import com.example.ui.theme.LocalGlassTheme
import com.example.ui.theme.ThemeMode

enum class SurahFilter(val label: String) {
    ALL("All 114"),
    MAKKI("Makki"),
    MADANI("Madani"),
    POPULAR("Core & Daily")
}

@Composable
fun HomeScreen(
    surahs: List<Surah>,
    lastReadSurah: Int,
    lastReadAyah: Int,
    memorizedCount: Int,
    currentThemeMode: ThemeMode,
    onThemeToggle: () -> Unit,
    onSelectSurah: (Surah) -> Unit,
    onStartHifzForSurah: (Surah) -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassTheme.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SurahFilter.ALL) }
    val focusManager = LocalFocusManager.current

    val lastSurah = remember(lastReadSurah, surahs) {
        surahs.find { it.number == lastReadSurah } ?: surahs.first()
    }

    val filteredSurahs = remember(searchQuery, selectedFilter, surahs) {
        surahs.filter { surah ->
            val matchesFilter = when (selectedFilter) {
                SurahFilter.ALL -> true
                SurahFilter.MAKKI -> surah.revelationType == RevelationType.MAKKI
                SurahFilter.MADANI -> surah.revelationType == RevelationType.MADANI
                SurahFilter.POPULAR -> listOf(1, 2, 18, 36, 55, 56, 67, 112, 113, 114).contains(surah.number)
            }
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                surah.englishName.lowercase().contains(q) ||
                        surah.englishMeaning.lowercase().contains(q) ||
                        surah.arabicName.contains(searchQuery.trim()) ||
                        surah.number.toString() == searchQuery.trim()
            }
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AL-SAIF",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = glass.accentGold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "QURAN",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            color = glass.textPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Recitation • Tajweed • Memorization",
                        fontSize = 12.sp,
                        color = glass.textMuted,
                        letterSpacing = 0.5.sp
                    )
                }

                // Theme Mode Switcher
                IconButton(
                    onClick = onThemeToggle,
                    modifier = Modifier
                        .testTag("theme_toggle_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(glass.surfaceGlass)
                        .border(BorderStroke(1.dp, glass.borderGlass), CircleShape)
                ) {
                    Icon(
                        imageVector = when (currentThemeMode) {
                            ThemeMode.OBSIDIAN_EMERALD -> Icons.Default.DarkMode
                            ThemeMode.MIDNIGHT_VELVET -> Icons.Default.Nightlight
                            ThemeMode.CELESTIAL_PEARL -> Icons.Default.LightMode
                        },
                        contentDescription = "Toggle Theme Mode",
                        tint = glass.accentGold
                    )
                }
            }
        }

        // Hero "Continue Reading" Liquid Glass Card
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("continue_reading_card"),
                shape = RoundedCornerShape(24.dp),
                borderColor = glass.accentGold.copy(alpha = 0.5f),
                onClick = { onSelectSurah(lastSurah) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = glass.accentGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LAST READ",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = glass.accentGold,
                            letterSpacing = 1.sp
                        )
                    }
                    LiquidPill(
                        text = "Ayah $lastReadAyah",
                        accentColor = glass.accentTeal
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lastSurah.englishName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = glass.textPrimary
                        )
                        Text(
                            text = "${lastSurah.englishMeaning} • ${lastSurah.numberOfAyahs} Verses",
                            fontSize = 13.sp,
                            color = glass.textSecondary
                        )
                    }

                    Text(
                        text = lastSurah.arabicName,
                        fontFamily = AmiriFont,
                        fontSize = 32.sp,
                        color = glass.accentGold,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Bar inside Hero Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(glass.surfaceGlass.copy(alpha = 0.6f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = glass.accentTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$memorizedCount Verses Memorized",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = glass.textPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Resume",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = glass.accentGold
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = glass.accentGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Search Bar with liquid glass aesthetic
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(glass.surfaceGlass)
                    .border(BorderStroke(1.dp, glass.borderGlass), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = glass.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search Surah name, number, or meaning...",
                                fontSize = 14.sp,
                                color = glass.textMuted
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_field")
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = glass.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Filter Pills
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SurahFilter.values()) { filter ->
                    val isSelected = filter == selectedFilter
                    val pillBg = if (isSelected) glass.accentGold else glass.surfaceGlass
                    val pillText = if (isSelected) Color(0xFF1E1502) else glass.textPrimary
                    val pillBorder = if (isSelected) glass.accentGold else glass.borderGlass

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillBg)
                            .border(BorderStroke(1.dp, pillBorder), RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = pillText
                        )
                    }
                }
            }
        }

        // Surah List Count Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SURAHS (${filteredSurahs.size})",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = glass.textMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Offline Available",
                    fontSize = 11.sp,
                    color = glass.accentTeal,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Surahs Items
        items(filteredSurahs, key = { it.number }) { surah ->
            SurahListItem(
                surah = surah,
                onClick = { onSelectSurah(surah) },
                onHifzClick = { onStartHifzForSurah(surah) }
            )
        }
    }
}

@Composable
fun SurahListItem(
    surah: Surah,
    onClick: () -> Unit,
    onHifzClick: () -> Unit
) {
    val glass = LocalGlassTheme.current

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("surah_item_${surah.number}"),
        shape = RoundedCornerShape(18.dp),
        elevation = 4.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number inside Liquid Circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(glass.surfaceGlass)
                    .border(BorderStroke(1.dp, glass.accentGold.copy(alpha = 0.4f)), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = surah.number.toString(),
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = glass.accentGold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Surah English Name & Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.englishName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = glass.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${surah.englishMeaning} • ${surah.numberOfAyahs} Ayahs",
                    fontSize = 12.sp,
                    color = glass.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = surah.revelationType.title.take(6),
                        fontSize = 10.sp,
                        color = glass.accentTeal,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Juz ${surah.juzStart}",
                        fontSize = 10.sp,
                        color = glass.textMuted
                    )
                }
            }

            // Arabic Surah Name
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = surah.arabicName,
                    fontFamily = AmiriFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = glass.accentGold,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Quick Hifz Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(glass.accentTeal.copy(alpha = 0.15f))
                        .border(BorderStroke(0.8.dp, glass.accentTeal.copy(alpha = 0.4f)), RoundedCornerShape(12.dp))
                        .clickable(onClick = onHifzClick)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Memorize",
                        tint = glass.accentTeal,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Hifz",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = glass.accentTeal
                    )
                }
            }
        }
    }
}
