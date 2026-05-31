package com.example.autocheckmobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

/**
 * Назначение: аватар кандидата с инициалами.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun CandidateAvatar(
    name: String,
    modifier: Modifier = Modifier,
    highlightError: Boolean = false,
) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "?" }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(DesignTokens.Primary.copy(alpha = 0.25f))
            .border(
                width = 1.dp,
                color = if (highlightError) DesignTokens.Error else DesignTokens.Primary.copy(alpha = 0.5f),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = DesignTokens.TextPrimary,
            style = CustomTheme.typography.geistBold14,
        )
    }
}
