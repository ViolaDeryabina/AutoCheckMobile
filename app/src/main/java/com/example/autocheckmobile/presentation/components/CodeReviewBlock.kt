package com.example.autocheckmobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

private val secretKeywords = listOf("api_key", "apikey", "secret", "password", "token", "Bearer")

/**
 * Назначение: блок code review с подсветкой потенциальной утечки секрета.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun CodeReviewBlock(
    fileName: String,
    codeSnippet: String,
    modifier: Modifier = Modifier,
) {
    val lines = codeSnippet.lines().ifEmpty { listOf("// Нет фрагмента кода") }
    val leakLineIndex = lines.indexOfFirst { line ->
        secretKeywords.any { keyword -> line.contains(keyword, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DesignTokens.Border, RoundedCornerShape(DesignTokens.RadiusMd))
            .background(Color(0xFF0B1222), RoundedCornerShape(DesignTokens.RadiusMd))
            .padding(12.dp),
    ) {
        Text(
            text = fileName,
            color = DesignTokens.Primary,
            style = CustomTheme.typography.geistSemiBold12,
        )
        lines.forEachIndexed { index, line ->
            val isLeak = index == leakLineIndex
            Box(modifier = Modifier.fillMaxWidth()) {
                if (isLeak) {
                    Text(
                        text = "УТЕЧКА СЕКРЕТА",
                        color = Color(0xFFFECACA),
                        style = CustomTheme.typography.geistSemiBold12,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.TopEnd)
                            .background(DesignTokens.Error.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                Text(
                    text = line,
                    color = if (isLeak) Color(0xFFFECACA) else DesignTokens.TextMuted,
                    fontFamily = FontFamily.Monospace,
                    style = CustomTheme.typography.geistNormal10,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (isLeak) 18.dp else 2.dp, bottom = 2.dp)
                        .then(
                            if (isLeak) {
                                Modifier.background(DesignTokens.Error.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            } else {
                                Modifier
                            },
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
        }
    }
}
