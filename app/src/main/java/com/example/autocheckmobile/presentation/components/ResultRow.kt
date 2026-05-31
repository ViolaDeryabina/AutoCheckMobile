package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

/**
 * Назначение: строка результата чекера с раскрывающимися деталями.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun ResultRow(
    checkerName: String,
    score: Int,
    maxScore: Int,
    details: String,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Log.d("[ResultRow]", "Отрисовка — checker=$checkerName")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DesignTokens.Border, RoundedCornerShape(DesignTokens.RadiusSm))
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = checkerName,
                color = DesignTokens.TextPrimary,
                style = CustomTheme.typography.geistNormal14,
                modifier = Modifier.weight(1f),
            )
            StatusBadge(status = if (score >= maxScore * 0.8) "passed" else if (score > 0) "running" else "failed")
            Text(
                text = "$score",
                color = DesignTokens.TextPrimary,
                style = CustomTheme.typography.geistBold14,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = DesignTokens.TextMuted,
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = details.ifBlank { "Нет деталей" },
                color = DesignTokens.TextMuted,
                style = CustomTheme.typography.geistNormal10,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .border(1.dp, DesignTokens.Border, RoundedCornerShape(DesignTokens.RadiusSm))
                    .padding(8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ResultRowPreview() {
    CustomTheme {
        ResultRow(
            checkerName = "StaticAnalysis",
            score = 85,
            maxScore = 100,
            details = "3 warnings found",
        )
    }
}
