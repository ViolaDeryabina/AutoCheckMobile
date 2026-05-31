package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Space4H

/**
 * Назначение: карточка отправки задания в списке «Мои задания».
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun SubmissionCard(
    assignmentTitle: String,
    uploadedAt: String,
    status: String,
    score: Int?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Log.d("[SubmissionCard]", "Отрисовка — assignment=$assignmentTitle")
    MainCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignmentTitle,
                    color = DesignTokens.TextPrimary,
                    style = CustomTheme.typography.geistBold14,
                )
                Space4H()
                Text(
                    text = uploadedAt,
                    color = DesignTokens.TextMuted,
                    style = CustomTheme.typography.geistNormal10,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = status)
                Space4H()
                Text(
                    text = score?.toString() ?: "—",
                    color = if (score != null) DesignTokens.Success else DesignTokens.TextMuted,
                    style = CustomTheme.typography.geistBold24,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = DesignTokens.TextMuted,
            )
        }
    }
}

@Preview
@Composable
private fun SubmissionCardPreview() {
    CustomTheme {
        SubmissionCard(
            assignmentTitle = "Senior iOS Engineer",
            uploadedAt = "31.05.2026",
            status = "running",
            score = null,
        )
    }
}
