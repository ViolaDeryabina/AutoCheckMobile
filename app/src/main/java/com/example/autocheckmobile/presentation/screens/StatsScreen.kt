package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.components.ButtonVariant
import com.example.autocheckmobile.presentation.components.MainCard
import com.example.autocheckmobile.presentation.components.PrimaryButton
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space12H
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.autocheckmobile.shared.DateFormatter
import com.example.netlib.data.dto.DailyStats
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.dto.sub.SubmissionItem

/**
 * Назначение: экран статистики эксперта — метрики, график, топ кандидатов.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun StatsScreen(
    stats: ReportsStatsData?,
    submissions: List<SubmissionItem>,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Log.i("[StatsScreen]", "Отрисовка — stats=${stats?.totalSubmissions}")
    val passRate = stats?.let {
        if (it.totalSubmissions == 0) 0 else (it.approvedCount * 100 / it.totalSubmissions)
    } ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.space16),
    ) {
        Text("Статистика", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistSemiBold32)
        Text("Метрики за 30 дней", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal16)
        Space16H()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Всего проверок", "${stats?.totalSubmissions ?: submissions.size}", Modifier.weight(1f))
            MetricCard("Средний балл", "%.1f".format(stats?.averageScore ?: 0.0), Modifier.weight(1f))
            MetricCard("Прохождение", "$passRate%", Modifier.weight(1f))
        }

        Space16H()
        MainCard(modifier = Modifier.fillMaxWidth()) {
            Text("Динамика (30 дней)", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
            Space12H()
            StatsChart(stats?.submissionsByDay.orEmpty(), Modifier.fillMaxWidth().height(180.dp))
        }

        Space16H()
        MainCard(modifier = Modifier.fillMaxWidth()) {
            Text("Топ кандидатов", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
            Space12H()
            stats?.topCandidates.orEmpty().take(10).forEach { candidate ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(candidate.fullName, color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistNormal14)
                    Text(
                        "%.0f / %d".format(candidate.averageScore, candidate.submissionsCount),
                        color = DesignTokens.Success,
                        style = CustomTheme.typography.geistBold14,
                    )
                }
            }
            if (stats?.topCandidates.isNullOrEmpty()) {
                Text("Нет данных", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
            }
        }

        Space16H()
        PrimaryButton(text = "Назад к дашборду", onClick = onBack, variant = ButtonVariant.Secondary)
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    MainCard(modifier = modifier) {
        Text(title, color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
        Text(value, color = DesignTokens.Primary, style = CustomTheme.typography.geistBold24)
    }
}

@Composable
private fun StatsChart(data: List<DailyStats>, modifier: Modifier = Modifier) {
    val points = data.ifEmpty { emptyList() }.map { it.count.toFloat() }.ifEmpty { listOf(2f, 4f, 3f, 6f, 5f, 7f, 4f) }
    val max = points.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    Canvas(modifier = modifier) {
        val stepX = size.width / (points.size - 1).coerceAtLeast(1)
        val path = Path()
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / max) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, DesignTokens.Primary, style = Stroke(width = 3f, cap = StrokeCap.Round))
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / max) * size.height
            drawCircle(DesignTokens.Success, 4f, Offset(x, y))
        }
    }
}
