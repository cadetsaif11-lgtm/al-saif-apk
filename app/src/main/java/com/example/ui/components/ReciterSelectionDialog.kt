package com.example.ui.components

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.Reciter
import com.example.audio.ReciterCategory
import com.example.audio.Reciters
import com.example.ui.theme.CinzelFont
import com.example.ui.theme.LocalGlassTheme

@Composable
fun ReciterSelectionDialog(
    currentReciter: Reciter,
    onDismiss: () -> Unit,
    onSelectReciter: (Reciter) -> Unit
) {
    val glass = LocalGlassTheme.current
    var selectedCategory by remember { mutableStateOf<ReciterCategory?>(null) } // null = ALL

    val filteredReciters = remember(selectedCategory) {
        if (selectedCategory == null) {
            Reciters.list
        } else {
            Reciters.list.filter { it.category == selectedCategory }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(26.dp))
                .border(BorderStroke(1.2.dp, glass.borderGlass), RoundedCornerShape(26.dp))
                .testTag("reciter_selection_dialog"),
            color = glass.surfaceGlass,
            tonalElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(glass.accentGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = glass.accentGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "HARAMAIN & MASTER RECITERS",
                            fontFamily = CinzelFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = glass.accentGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Imams of Makkah, Madinah & World Reciters",
                            fontSize = 11.sp,
                            color = glass.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Tabs Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAllSelected = selectedCategory == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAllSelected) glass.accentGold else glass.cardBackground)
                                .clickable { selectedCategory = null }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "All (${Reciters.list.size})",
                                fontSize = 11.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllSelected) Color(0xFF1E1502) else glass.textPrimary
                            )
                        }
                    }

                    items(ReciterCategory.values().drop(1)) { category ->
                        val isSelected = selectedCategory == category
                        val label = when (category) {
                            ReciterCategory.HARAMAIN_MAKKAH -> "Makkah Imams"
                            ReciterCategory.HARAMAIN_MADINAH -> "Madinah Imams"
                            ReciterCategory.CLASSIC_MASTERS -> "Classic Masters"
                            ReciterCategory.ALL -> "All"
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) glass.accentTeal else glass.cardBackground)
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF003831) else glass.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = glass.borderGlass, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Reciters List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredReciters, key = { it.id }) { reciter ->
                        val isSelected = reciter.id == currentReciter.id

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) glass.accentGold.copy(alpha = 0.16f) else glass.cardBackground)
                                .border(
                                    BorderStroke(
                                        if (isSelected) 1.5.dp else 0.8.dp,
                                        if (isSelected) glass.accentGold else glass.borderGlass
                                    ),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    onSelectReciter(reciter)
                                    onDismiss()
                                }
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Radio check or indicator
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) glass.accentGold else Color.Transparent)
                                        .border(
                                            BorderStroke(1.2.dp, if (isSelected) glass.accentGold else glass.textMuted),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF1E1502),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reciter.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) glass.accentGold else glass.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = reciter.mosqueTitle,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = glass.accentTeal
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = reciter.style,
                                        fontSize = 11.sp,
                                        color = glass.textSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Done Action
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = glass.accentGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = "Confirm Selection",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1502),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
