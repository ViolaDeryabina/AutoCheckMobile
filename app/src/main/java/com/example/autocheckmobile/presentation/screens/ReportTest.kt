package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.components.ButtonVariant
import com.example.autocheckmobile.presentation.components.CheckerSummaryCard
import com.example.autocheckmobile.presentation.components.CircleWithGradient
import com.example.autocheckmobile.presentation.components.CodeReviewBlock
import com.example.autocheckmobile.presentation.components.MainCard
import com.example.autocheckmobile.presentation.components.PrimaryButton
import com.example.autocheckmobile.presentation.components.ProgressBar
import com.example.autocheckmobile.presentation.components.ResultRow
import com.example.autocheckmobile.presentation.components.StatusBadge
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space12H
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.autocheckmobile.presentation.viewModel.SubmissionDetailsState
import com.example.autocheckmobile.shared.CheckerMapper
import com.example.autocheckmobile.shared.DateFormatter

/**
 * Назначение: карточка результата проверки — балл, чекеры, AI-анализ, code review, экспертные действия.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportTest(
    state: SubmissionDetailsState,
    assignmentTitle: String,
    candidateLabel: String,
    canSetVerdict: Boolean,
    onLoadAiReview: () -> Unit,
    onRerun: () -> Unit,
    onDownloadReport: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val submission = state.submission
    var verdictDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var verdictComment by rememberSaveable { mutableStateOf("") }
    Log.i("[ReportTest]", "Отрисовка — submissionId=${submission?.id}")

    if (state.isLoading && submission == null) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(color = DesignTokens.Primary)
            Text("Проверка выполняется...", color = DesignTokens.TextMuted, modifier = Modifier.padding(top = 12.dp))
        }
        return
    }

    if (submission == null) {
        Column(modifier = modifier.padding(Dimens.space16)) {
            Text(state.errorMessage ?: "Проверка не найдена", color = DesignTokens.Error)
            Space16H()
            PrimaryButton(text = "Назад", onClick = onBack, variant = ButtonVariant.Secondary)
        }
        return
    }

    val summaryCards = CheckerMapper.summaryCards(state.results)
    val (codeFile, codeSnippet) = CheckerMapper.codeReviewSnippet(state.results)
    val (buildPct, memoryPct, cpuPct) = CheckerMapper.performanceMetrics(state.results)

    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.space16),
    ) {
        Text(text = candidateLabel, color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistSemiBold32)
        Text(
            text = "$assignmentTitle · ${DateFormatter.formatDateOnly(submission.createdAt)}",
            color = DesignTokens.TextMuted,
            style = CustomTheme.typography.geistNormal14,
        )
        Space16H()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            PrimaryButton(
                text = "ПЕРЕЗАПУСТИТЬ",
                onClick = onRerun,
                modifier = Modifier.weight(1f),
                loading = state.isActionLoading,
                enabled = canSetVerdict,
                fillMaxWidth = false,
            )
            PrimaryButton(
                text = "ЭКСПОРТ",
                onClick = onDownloadReport,
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Secondary,
                loading = state.isActionLoading,
                fillMaxWidth = false,
            )
        }

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
                    modifier = Modifier.size(110.dp),
                    text = submission.finalScore?.toString() ?: "—",
                )
            }
        }

        if (summaryCards.isNotEmpty()) {
            Space16H()
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                summaryCards.forEach { card ->
                    CheckerSummaryCard(data = card, modifier = Modifier.fillMaxWidth(0.48f))
                }
            }
        }

        Space16H()
        MainCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DesignTokens.Primary.copy(alpha = 0.45f), RoundedCornerShape(DesignTokens.RadiusMd)),
        ) {
            Text("Производительность", color = DesignTokens.Primary, style = CustomTheme.typography.geistBold14)
            Space12H()
            MetricRow("Build Time", "${buildPct.toInt()}%", buildPct)
            MetricRow("Peak Memory", "${memoryPct.toInt()}%", memoryPct)
            MetricRow("CPU Load", "Peak ${cpuPct.toInt()}%", cpuPct)
            Text("● Live monitoring active", color = DesignTokens.Success, style = CustomTheme.typography.geistNormal10)
        }

        Space16H()
        Text("Code Review", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
        Space12H()
        CodeReviewBlock(fileName = codeFile, codeSnippet = codeSnippet)

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

        if (canSetVerdict) {
            Space16H()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(text = "Принять", onClick = { verdictDialog = "approved" }, modifier = Modifier.weight(1f))
                PrimaryButton(
                    text = "Отклонить",
                    onClick = { verdictDialog = "rejected" },
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Danger,
                )
            }
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

    if (verdictDialog != null) {
        AlertDialog(
            onDismissRequest = { verdictDialog = null },
            title = { Text(if (verdictDialog == "approved") "Принять кандидата" else "Отклонить кандидата") },
            text = {
                OutlinedTextField(
                    value = verdictComment,
                    onValueChange = { verdictComment = it },
                    label = { Text("Комментарий") },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (verdictDialog == "approved") onApprove() else onReject()
                    verdictDialog = null
                    verdictComment = ""
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { verdictDialog = null }) { Text("Отмена") }
            },
        )
    }
}

@Composable
private fun MetricRow(label: String, value: String, progress: Float) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
        Text(value, color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistNormal14)
    }
}
