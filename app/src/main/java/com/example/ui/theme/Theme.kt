package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    OBSIDIAN_EMERALD, // Deep Emerald & Gold Glass (Default Dark)
    MIDNIGHT_VELVET,  // OLED Pure Black with Cyan / Emerald Neon
    CELESTIAL_PEARL   // Elegant Light Glass with Warm Amber Gold
}

data class GlassThemeTokens(
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val surfaceGlass: Color,
    val borderGlass: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val accentGold: Color,
    val accentTeal: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val LocalGlassTheme = staticCompositionLocalOf {
    GlassThemeTokens(
        backgroundStart = DeepEmerald,
        backgroundEnd = Color(0xFF020C0A),
        surfaceGlass = Color(0xCC08221D),
        borderGlass = EmeraldCardBorder,
        cardBackground = Color(0x660A2F27),
        cardBorder = Color(0x3334D399),
        accentGold = GoldAccent,
        accentTeal = TealAccent,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        textMuted = TextMuted,
        isDark = true
    )
}

private val ObsidianEmeraldColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color(0xFF1E1502),
    primaryContainer = Color(0xFF382907),
    onPrimaryContainer = GoldLight,
    secondary = TealAccent,
    onSecondary = Color(0xFF003831),
    background = DeepEmerald,
    onBackground = TextPrimary,
    surface = DarkEmeraldSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF0F362E),
    onSurfaceVariant = TextSecondary
)

private val MidnightVelvetColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    background = Color(0xFF030708),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF080E11),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF10191E),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val CelestialPearlColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun AlSaifQuranTheme(
    themeMode: ThemeMode = ThemeMode.OBSIDIAN_EMERALD,
    content: @Composable () -> Unit
) {
    val (colorScheme, glassTokens) = when (themeMode) {
        ThemeMode.OBSIDIAN_EMERALD -> {
            ObsidianEmeraldColorScheme to GlassThemeTokens(
                backgroundStart = DeepEmerald,
                backgroundEnd = Color(0xFF020E0C),
                surfaceGlass = Color(0xDD08221D),
                borderGlass = Color(0x4034D399),
                cardBackground = Color(0x550E362D),
                cardBorder = Color(0x3334D399),
                accentGold = GoldAccent,
                accentTeal = TealAccent,
                textPrimary = TextPrimary,
                textSecondary = TextSecondary,
                textMuted = TextMuted,
                isDark = true
            )
        }
        ThemeMode.MIDNIGHT_VELVET -> {
            MidnightVelvetColorScheme to GlassThemeTokens(
                backgroundStart = Color(0xFF020405),
                backgroundEnd = Color(0xFF060B0E),
                surfaceGlass = Color(0xDD081116),
                borderGlass = Color(0x4038BDF8),
                cardBackground = Color(0x660F1E26),
                cardBorder = Color(0x3338BDF8),
                accentGold = GoldAccent,
                accentTeal = CyanAccent,
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFFCBD5E1),
                textMuted = Color(0xFF64748B),
                isDark = true
            )
        }
        ThemeMode.CELESTIAL_PEARL -> {
            CelestialPearlColorScheme to GlassThemeTokens(
                backgroundStart = Color(0xFFF4F7F6),
                backgroundEnd = Color(0xFFE2E8F0),
                surfaceGlass = Color(0xE6FFFFFF),
                borderGlass = Color(0x40CBD5E1),
                cardBackground = Color(0xCCFFFFFF),
                cardBorder = Color(0x3394A3B8),
                accentGold = GoldDark,
                accentTeal = Color(0xFF0D9488),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF334155),
                textMuted = Color(0xFF64748B),
                isDark = false
            )
        }
    }

    CompositionLocalProvider(LocalGlassTheme provides glassTokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
