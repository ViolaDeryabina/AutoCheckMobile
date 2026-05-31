package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.components.CircleWithGradient
import com.example.autocheckmobile.presentation.components.MainCard
import com.example.autocheckmobile.presentation.components.PrimaryButton
import com.example.autocheckmobile.presentation.components.ProgressBar
import com.example.autocheckmobile.presentation.components.ResultRow
import com.example.autocheckmobile.presentation.components.StatusBadge
import com.example.autocheckmobile.presentation.components.ButtonVariant
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space12H
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.autocheckmobile.presentation.viewModel.SubmissionDetailsState
import com.example.autocheckmobile.shared.DateFormatter

/**
 * Назначение: карточка результата проверки — балл, чекеры, AI-анализ, хронология.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun ReportTest(
    state: SubmissionDetailsState,
    assignmentTitle: String,
    onLoadAiReview: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val submission = state.submission
    Log.i("[ReportTest]", "Отрисовка — submissionId=${submission?.id}")

    if (state.isLoading && submission == null) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(color = DesignTokens.Primary)
        }
        return
    }

    if (submission == null) {
        Column(modifier = modifier.padding(Dimens.space16)) {
            Text("Проверка не найдена", color = DesignTokens.TextMuted)
            PrimaryButton(text = "Назад", onClick = onBack, variant = ButtonVariant.Secondary)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.space16),
    ) {
        Text(
            text = assignmentTitle,
            color = DesignTokens.TextPrimary,
            style = CustomTheme.typography.geistSemiBold32,
        )
        Text(
            text = "Кандидат #${submission.candidateId} · ${DateFormatter.formatDateOnly(submission.createdAt)}",
            color = DesignTokens.TextMuted,
            style = CustomTheme.typography.geistNormal14,
        )
        Space16H()

        MainCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("ИТОГОВЫЙ БАЛЛ", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
                    StatusBadge(status = submission.status)
                }
                CircleWithGradient(
                    modifier = Modifier.size(100.dp),
                    text = submission.finalScore?.toString() ?: "—",
                )
            }
        }

        Space16H()
        Text("Детализация проверок", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
        Space12H()
        state.results.forEach { result ->
            ResultRow(
                checkerName = result.name,
                score = result.score,
                maxScore = result.maxScore,
                details = result.details.orEmpty(),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Space16H()
        MainCard(modifier = Modifier.fillMaxWidth()) {
            Text("AI-анализ", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
            Space12H()
            when {
                state.aiReview != null -> {
                    Text(state.aiReview.review, color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
                    state.aiReview.suggestions.orEmpty().forEach { tip ->
                        Text("• $tip", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
                    }
                }
                state.aiUnavailable -> {
                    Text(
                        text = "AI-анализ недоступен",
                        color = DesignTokens.Warning,
                        style = CustomTheme.typography.geistNormal14,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DesignTokens.Warning.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                    )
                }
                else -> PrimaryButton(text = "Загрузить AI-анализ", onClick = onLoadAiReview, variant = ButtonVariant.Secondary)
            }
        }

        Space16H()
        MainCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DesignTokens.Primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        ) {
            Text("Производительность", color = DesignTokens.Primary, style = CustomTheme.typography.geistBold14)
            Space12H()
            MetricRow("Build Time", "42.5s", 85f)
            MetricRow("Peak Memory", "124MB", 70f)
            MetricRow("CPU Load", "Peak 88%", 88f)
            Text("● Live monitoring active", color = DesignTokens.Success, style = CustomTheme.typography.geistNormal10)
        }

        Space16H()
        MainCard(modifier = Modifier.fillMaxWidth()) {
            Text("Хронология", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
            TimelineRow("Загрузка", DateFormatter.formatUtcToMsk(submission.createdAt))
            TimelineRow("Обновление", DateFormatter.formatUtcToMsk(submission.updatedAt))
            TimelineRow("Статус", submission.status)
            TimelineRow("Вердикт", submission.verdict ?: "pending")
        }

        Space16H()
        PrimaryButton(text = "Назад", onClick = onBack, variant = ButtonVariant.Secondary)
    }
}

@Composable
private fun MetricRow(label: String, value: String, progress: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
        Text(value, color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
    }
    ProgressBar(value = progress, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun TimelineRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
        Text(value, color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistNormal14)
    }
}
