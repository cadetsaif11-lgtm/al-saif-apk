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
import com.example.data.model.TajweedRuleType
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidPill
import com.example.ui.theme.AmiriFont
import com.example.ui.theme.CinzelFont
import com.example.ui.theme.LocalGlassTheme

data class TajweedExample(
    val arabic: String,
    val surahRef: String,
    val explanation: String
)

@Composable
fun TajweedScreen(
    modifier: Modifier = Modifier
) {
    val glass = LocalGlassTheme.current
    var expandedRule by remember { mutableStateOf<TajweedRuleType?>(TajweedRuleType.GHUNNAH) }

    val ruleExamples = remember {
        mapOf(
            TajweedRuleType.GHUNNAH to listOf(
                TajweedExample("إِنَّ اللَّهَ", "Surah Al-Baqarah", "Noon with Shaddah (نّ) held for 2 full beats from the nasal cavity."),
                TajweedExample("ثُمَّ كَلَّا سَوْفَ تَعْلَمُونَ", "Surah At-Takathur", "Meem with Shaddah (مّ) resonated with humming Ghunnah.")
            ),
            TajweedRuleType.QALQALAH to listOf(
                TajweedExample("قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Surah Al-Falaq: 1", "Major echo (Qalqalah Kubra) on pausing on Qaf (ق)."),
                TajweedExample("لَمْ يَلِدْ وَلَمْ يُولَدْ", "Surah Al-Ikhlas: 3", "Minor echo (Sughra) on internal Dal, and Major on final Dal.")
            ),
            TajweedRuleType.IKHFA to listOf(
                TajweedExample("مِن شَرِّ مَا خَلَقَ", "Surah Al-Falaq: 2", "Noon Saakin before Sheen (ش): Light nasal tone concealing the letter."),
                TajweedExample("كِتَابٌ كَرِيمٌ", "Surah An-Naml", "Tanween before Kaaf (ك): Smooth nasal transition.")
            ),
            TajweedRuleType.IDGHAM to listOf(
                TajweedExample("مَن يَقُولُ", "Surah Al-Baqarah", "Idgham with Ghunnah: Noon merges into Yaa (ي) with 2 counts."),
                TajweedExample("مِن رَّبِّهِمْ", "Surah Al-Baqarah", "Idgham without Ghunnah: Noon merges entirely into Raa (ر).")
            ),
            TajweedRuleType.IQLAB to listOf(
                TajweedExample("مِن بَعْدِ", "Surah Al-Baqarah", "Noon Saakin followed by Baa (ب) converts to a hidden Meem with Ghunnah."),
                TajweedExample("سَمِيعٌ بَصِيرٌ", "Surah Al-Hajj", "Tanween followed by Baa (ب) turns into Meem.")
            ),
            TajweedRuleType.MADD to listOf(
                TajweedExample("وَلَا الضَّالِّينَ", "Surah Al-Fatihah: 7", "Madd Lazim: Compulsory 6 full counts elongation."),
                TajweedExample("إِذَا جَاءَ نَصْرُ اللَّهِ", "Surah An-Nasr: 1", "Madd Muttasil: Connected Hamzah prolonged for 4 to 5 counts.")
            ),
            TajweedRuleType.TAFKHEEM to listOf(
                TajweedExample("خَالِدِينَ فِيهَا", "Surah Ali 'Imran", "Kha (خ) pronounced with full-mouth elevation."),
                TajweedExample("الصِّرَاطَ الْمُسْتَقِيمَ", "Surah Al-Fatihah: 6", "Sad (ص) and Taa (ط) elevated with deep resonance.")
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tajweed_screen_column"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = glass.accentGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TAJWEED RULES",
                        fontFamily = CinzelFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = glass.accentGold,
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = "Rules of recitation, letter articulation, and phonetic codes",
                    fontSize = 12.sp,
                    color = glass.textMuted
                )
            }
        }

        // Color Code Quick Reference Card
        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                borderColor = glass.accentTeal.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "COLOR-CODED TAJWEED KEY",
                    fontFamily = CinzelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = glass.accentTeal,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Tap on any verse in the reader to view these rules automatically highlighted:",
                    fontSize = 12.sp,
                    color = glass.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TajweedRuleType.values().take(4).forEach { rule ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(rule.color)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rule.name.take(5),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = rule.color
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TajweedRuleType.values().drop(4).forEach { rule ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(rule.color)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rule.name.take(5),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = rule.color
                            )
                        }
                    }
                }
            }
        }

        // Detailed Rules Accordion List
        items(TajweedRuleType.values(), key = { it.name }) { rule ->
            val isExpanded = expandedRule == rule

            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tajweed_rule_${rule.name}"),
                shape = RoundedCornerShape(20.dp),
                borderColor = if (isExpanded) rule.color.copy(alpha = 0.6f) else null,
                onClick = {
                    expandedRule = if (isExpanded) null else rule
                }
            ) {
                // Rule Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(rule.color)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = rule.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = glass.textPrimary
                            )
                            Text(
                                text = rule.arabicTitle,
                                fontFamily = AmiriFont,
                                fontSize = 18.sp,
                                color = rule.color
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = rule.color
                    )
                }

                // Expanded Rule Content
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                    ) {
                        // Description
                        Text(
                            text = rule.description,
                            fontSize = 13.sp,
                            color = glass.textSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Letters
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(glass.surfaceGlass)
                                .border(BorderStroke(0.8.dp, rule.color.copy(alpha = 0.4f)), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Letters & Formula:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glass.textMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = rule.letters,
                                    fontFamily = AmiriFont,
                                    fontSize = 20.sp,
                                    color = rule.color,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Quranic Examples
                        val examples = ruleExamples[rule] ?: emptyList()
                        if (examples.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Authentic Quranic Examples:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = glass.accentGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            examples.forEach { eg ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(glass.surfaceGlass.copy(alpha = 0.5f))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = eg.surahRef,
                                            fontSize = 11.sp,
                                            color = glass.textMuted
                                        )
                                        Text(
                                            text = eg.arabic,
                                            fontFamily = AmiriFont,
                                            fontSize = 20.sp,
                                            color = rule.color
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = eg.explanation,
                                        fontSize = 12.sp,
                                        color = glass.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
