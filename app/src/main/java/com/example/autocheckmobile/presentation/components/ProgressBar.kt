package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

/**
 * Назначение: линейный индикатор прогресса с пороговой цветовой индикацией (>80% зелёный, 50–80% жёлтый, <50% красный).
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun ProgressBar(
    value: Float,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    val clamped = value.coerceIn(0f, 100f)
    val color = when {
        clamped > 80f -> DesignTokens.Success
        clamped >= 50f -> DesignTokens.Warning
        else -> DesignTokens.Error
    }
    Log.d("[ProgressBar]", "Отрисовка — value=$clamped")

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DesignTokens.Card),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clamped / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color),
            )
        }
        if (showLabel) {
            Text(
                text = "${clamped.toInt()}%",
                color = DesignTokens.TextMuted,
                style = CustomTheme.typography.geistSemiBold12,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ProgressBarPreview() {
    CustomTheme {
        ProgressBar(value = 72f)
    }
}
