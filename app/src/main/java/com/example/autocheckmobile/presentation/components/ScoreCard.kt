package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Space8H

/**
 * Назначение: карточка итогового балла с цветовой индикацией и статусом проверки.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun ScoreCard(
    title: String,
    candidate: String,
    score: Int?,
    status: String,
    modifier: Modifier = Modifier,
) {
    Log.d("[ScoreCard]", "Отрисовка — score=$score status=$status")
    val scoreColor = when {
        score == null -> DesignTokens.TextMuted
        score >= 80 -> DesignTokens.Success
        score >= 50 -> DesignTokens.Warning
        else -> DesignTokens.Error
    }

    MainCard(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.material3.Text(
                text = title,
                color = DesignTokens.TextPrimary,
                style = CustomTheme.typography.geistBold14,
                modifier = Modifier.weight(1f),
            )
            StatusBadge(status = status)
        }
        Space8H()
        androidx.compose.material3.Text(
            text = candidate,
            color = DesignTokens.TextMuted,
            style = CustomTheme.typography.geistNormal14,
        )
        Space8H()
        androidx.compose.material3.Text(
            text = score?.toString() ?: "—",
            color = scoreColor,
            style = CustomTheme.typography.geistBold48,
        )
    }
}

@Preview
@Composable
private fun ScoreCardPreview() {
    CustomTheme {
        Column {
            ScoreCard(
                title = "iOS Engineer Test",
                candidate = "Jordan Devereaux",
                score = 92,
                status = "done",
            )
        }
    }
}
