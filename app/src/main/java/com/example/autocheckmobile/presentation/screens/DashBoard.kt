package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.components.CircleWithGradient
import com.example.autocheckmobile.presentation.components.MainCard
import com.example.autocheckmobile.presentation.components.StatusBadge
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.MintGreen
import com.example.autocheckmobile.presentation.theme.Space12H
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.autocheckmobile.presentation.theme.Space24H
import com.example.netlib.data.dto.DailyStats
import com.example.netlib.data.dto.ReportsStatsData
import com.example.netlib.data.dto.sub.SubmissionItem

/**
 * Назначение: главный дашборд эксперта — метрики, срочные проверки, график активности.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun DashBoard(
    userName: String,
    stats: ReportsStatsData?,
    submissions: List<SubmissionItem>,
    modifier: Modifier = Modifier,
) {
    Log.i("[DashBoard]", "Отрисовка — user=$userName")
    val pendingUrgent = submissions.filter { it.status == "pending" || it.status == "running" }.take(3)
    val totalTests = stats?.totalSubmissions ?: submissions.size
    val passRate = stats?.let {
        if (it.totalSubmissions == 0) 0 else (it.approvedCount * 100 / it.totalSubmissions)
    } ?: 90

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.space16),
    ) {
        Text(
            text = "Панель управления",
            color = DesignTokens.TextPrimary,
            style = CustomTheme.typography.geistSemiBold32,
        )
        Text(
            text = "Добро пожаловать, $userName. Система работает в штатном режиме.",
            color = DesignTokens.TextMuted,
            style = CustomTheme.typography.geistNormal16,
        )
        Space24H()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainCard(modifier = Modifier.weight(1f)) {
                Text("ВСЕГО ТЕСТОВ", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
                Text(
                    text = "%,d".format(totalTests),
                    color = DesignTokens.TextPrimary,
                    style = CustomTheme.typography.geistBold48,
                )
                Text(text = "+12.5% за неделю", color = MintGreen, style = CustomTheme.typography.geistSemiBold12)
            }
            MainCard(modifier = Modifier.weight(1f)) {
                Text("УСПЕШНОСТЬ", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleWithGradient(
                        modifier = Modifier.size(72.dp),
                        text = "$passRate%",
                    )
                }
                Text(text = "4% выше среднего", color = MintGreen, style = CustomTheme.typography.geistSemiBold12)
            }
        }

        Space16H()
        MainCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("ОЖИДАЮТ ПРОВЕРКИ", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
                Text(
                    text = "СРОЧНО",
                    color = DesignTokens.Error,
                    style = CustomTheme.typography.geistSemiBold12,
                    modifier = Modifier
                        .background(DesignTokens.Error.copy(alpha = 0.15f), RoundedCornerShape(DesignTokens.RadiusPill))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
            Space12H()
            pendingUrgent.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Проверка #${item.id}",
                        color = DesignTokens.TextPrimary,
                        style = CustomTheme.typography.geistNormal14,
                    )
                    StatusBadge(status = item.status)
                }
            }
            if (pendingUrgent.isEmpty()) {
                Text("Нет срочных задач", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
            }
        }

        Space16H()
        MainCard(modifier = Modifier.fillMaxWidth()) {
            Text("АКТИВНОСТЬ ТЕСТИРОВАНИЯ", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
            Space12H()
            ActivityChart(data = stats?.submissionsByDay.orEmpty(), modifier = Modifier.fillMaxWidth().height(160.dp))
        }
    }
}

@Composable
private fun ActivityChart(data: List<DailyStats>, modifier: Modifier = Modifier) {
    val points = if (data.isEmpty()) {
        listOf(2f, 5f, 3f, 8f, 6f, 4f, 7f)
    } else {
        data.map { it.count.toFloat() }
    }
    val max = points.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Canvas(modifier = modifier) {
        val stepX = size.width / (points.size - 1).coerceAtLeast(1)
        val path = Path()
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / max) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = DesignTokens.Primary,
            style = Stroke(width = 3f, cap = StrokeCap.Round),
        )
        points.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / max) * size.height
            drawCircle(color = DesignTokens.Success, radius = 4f, center = Offset(x, y))
        }
    }
}
