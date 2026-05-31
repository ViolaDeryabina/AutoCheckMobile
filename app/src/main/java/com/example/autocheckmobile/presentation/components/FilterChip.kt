package com.example.autocheckmobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

/**
 * Назначение: фильтр-чип для списков и поиска.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) DesignTokens.Primary else DesignTokens.Border
    val textColor = if (selected) DesignTokens.Primary else DesignTokens.TextMuted
    val background = if (selected) DesignTokens.Primary.copy(alpha = 0.1f) else DesignTokens.Card

    Text(
        text = label,
        color = textColor,
        style = CustomTheme.typography.geistSemiBold12,
        modifier = modifier
            .background(background, RoundedCornerShape(DesignTokens.RadiusPill))
            .border(1.dp, borderColor, RoundedCornerShape(DesignTokens.RadiusPill))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Preview
@Composable
private fun FilterChipPreview() {
    CustomTheme {
        FilterChip(label = "Все", selected = true, onClick = {})
    }
}
