package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

/**
 * Назначение: бейдж статуса проверки с цветовой индикацией (pending/running/passed/failed/error).
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier,
) {
    Log.d("[StatusBadge]", "Отрисовка — status=$status")
    val normalized = when (status.lowercase()) {
        "done" -> "passed"
        else -> status.lowercase()
    }
    val (background, foreground) = when (normalized) {
        "pending" -> DesignTokens.Secondary.copy(alpha = 0.2f) to DesignTokens.Secondary
        "running" -> DesignTokens.Primary.copy(alpha = 0.2f) to DesignTokens.Primary
        "passed" -> DesignTokens.Success.copy(alpha = 0.15f) to DesignTokens.Success
        "failed", "error" -> DesignTokens.Error.copy(alpha = 0.2f) to Color(0xFFFECACA)
        else -> DesignTokens.Secondary.copy(alpha = 0.2f) to DesignTokens.Secondary
    }

    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(DesignTokens.RadiusPill))
            .border(1.dp, Color.Transparent, RoundedCornerShape(DesignTokens.RadiusPill))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = status,
            color = foreground,
            style = CustomTheme.typography.geistSemiBold12,
        )
    }
}

@Preview
@Composable
private fun StatusBadgePreview() {
    CustomTheme {
        StatusBadge(status = "running")
    }
}
