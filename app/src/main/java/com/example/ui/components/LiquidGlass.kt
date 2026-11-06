package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalGlassTheme

@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val glass = LocalGlassTheme.current
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            glass.backgroundStart,
            glass.backgroundEnd
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Subtle ambient atmospheric glow top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glass.accentTeal.copy(alpha = if (glass.isDark) 0.15f else 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
        // Subtle ambient warm gold glow bottom-left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            glass.accentGold.copy(alpha = if (glass.isDark) 0.12f else 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    elevation: Dp = 8.dp,
    borderColor: Color? = null,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val glass = LocalGlassTheme.current
    val actualBorder = borderColor ?: glass.borderGlass
    val actualBg = backgroundColor ?: glass.cardBackground

    val glassBrush = Brush.verticalGradient(
        colors = listOf(
            actualBg,
            actualBg.copy(alpha = (actualBg.alpha * 0.7f).coerceIn(0f, 1f))
        )
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            actualBorder,
            actualBorder.copy(alpha = 0.1f),
            actualBorder.copy(alpha = 0.4f)
        )
    )

    val baseModifier = modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.4f),
            spotColor = glass.accentTeal.copy(alpha = 0.2f)
        )
        .clip(shape)
        .background(glassBrush)
        .border(BorderStroke(1.dp, borderBrush), shape)

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(onClick = onClick)
    } else {
        baseModifier
    }

    Column(
        modifier = finalModifier.padding(16.dp),
        content = content
    )
}

@Composable
fun LiquidPill(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalGlassTheme.current.accentGold,
    textColor: Color = LocalGlassTheme.current.textPrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(
                BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LiquidTajweedBadge(
    ruleTitle: String,
    ruleColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(clickModifier)
            .clip(RoundedCornerShape(20.dp))
            .background(ruleColor.copy(alpha = 0.16f))
            .border(BorderStroke(1.dp, ruleColor.copy(alpha = 0.5f)), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(ruleColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = ruleTitle,
            color = ruleColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
