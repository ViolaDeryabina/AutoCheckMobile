package com.example.autocheckmobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

data class CheckerSummaryUi(
    val title: String,
    val value: String,
    val badge: String? = null,
    val tone: CheckerTone = CheckerTone.Neutral,
)

enum class CheckerTone { Success, Warning, Critical, Neutral }

/**
 * Назначение: компактная карточка результата чекера (Unit Tests / Linting / Security).
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun CheckerSummaryCard(
    data: CheckerSummaryUi,
    modifier: Modifier = Modifier,
) {
    val borderColor = when (data.tone) {
        CheckerTone.Success -> DesignTokens.Success.copy(alpha = 0.4f)
        CheckerTone.Warning -> DesignTokens.Warning.copy(alpha = 0.4f)
        CheckerTone.Critical -> DesignTokens.Error.copy(alpha = 0.55f)
        CheckerTone.Neutral -> DesignTokens.Border
    }
    val background = when (data.tone) {
        CheckerTone.Success -> DesignTokens.Success.copy(alpha = 0.08f)
        CheckerTone.Warning -> DesignTokens.Warning.copy(alpha = 0.08f)
        CheckerTone.Critical -> DesignTokens.Error.copy(alpha = 0.12f)
        CheckerTone.Neutral -> DesignTokens.Card
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(DesignTokens.RadiusMd))
            .border(1.dp, borderColor, RoundedCornerShape(DesignTokens.RadiusMd))
            .padding(12.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = data.title,
                color = DesignTokens.TextMuted,
                style = CustomTheme.typography.geistSemiBold12,
                modifier = Modifier.weight(1f),
            )
            if (data.badge != null) {
                Text(
                    text = data.badge,
                    color = when (data.tone) {
                        CheckerTone.Critical -> Color(0xFFFECACA)
                        CheckerTone.Warning -> DesignTokens.Warning
                        else -> DesignTokens.TextMuted
                    },
                    style = CustomTheme.typography.geistSemiBold12,
                    modifier = Modifier
                        .background(
                            if (data.tone == CheckerTone.Critical) DesignTokens.Error.copy(alpha = 0.2f)
                            else DesignTokens.Card,
                            RoundedCornerShape(DesignTokens.RadiusPill),
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
        Text(
            text = data.value,
            color = DesignTokens.TextPrimary,
            style = CustomTheme.typography.geistBold14,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
