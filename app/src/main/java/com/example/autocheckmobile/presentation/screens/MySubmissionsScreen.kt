package com.example.autocheckmobile.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import com.example.autocheckmobile.presentation.components.PrimaryButton
import com.example.autocheckmobile.presentation.components.SubmissionCard
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.autocheckmobile.shared.DateFormatter
import com.example.netlib.data.dto.sub.SubmissionItem

private const val PAGE_SIZE = 20

/**
 * Назначение: список отправок кандидата с пагинацией «Загрузить ещё».
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun MySubmissionsScreen(
    submissions: List<SubmissionItem>,
    assignmentTitle: (Int) -> String,
    onSelectSubmission: (SubmissionItem) -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visibleCount by remember { mutableIntStateOf(PAGE_SIZE) }
    val visibleItems = submissions.take(visibleCount)
    val listState = rememberLazyListState()
    val reachedEnd by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= visibleItems.lastIndex.coerceAtLeast(0)
        }
    }

    LaunchedEffect(reachedEnd, submissions.size) {
        if (reachedEnd && visibleCount < submissions.size) {
            visibleCount = (visibleCount + PAGE_SIZE).coerceAtMost(submissions.size)
        }
    }

    if (submissions.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.space24),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Нет отправок",
                color = DesignTokens.TextPrimary,
                style = CustomTheme.typography.geistSemiBold32,
            )
            Space16H()
            Text(
                text = "Отправьте решение тестового задания",
                color = DesignTokens.TextMuted,
                style = CustomTheme.typography.geistNormal16,
            )
            Space16H()
            PrimaryButton(text = "Отправить задание", onClick = onUploadClick)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.space16),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(Dimens.space12),
    ) {
        item {
            Text(
                text = "Мои задания",
                color = DesignTokens.TextPrimary,
                style = CustomTheme.typography.geistSemiBold32,
            )
            Space16H()
        }
        items(visibleItems, key = { it.id }) { submission ->
            SubmissionCard(
                assignmentTitle = assignmentTitle(submission.assignmentId),
                uploadedAt = DateFormatter.formatDateOnly(submission.createdAt),
                status = submission.status,
                score = submission.finalScore.takeIf { submission.status == "done" && it > 0 },
                onClick = { onSelectSubmission(submission) },
            )
        }
        if (visibleCount < submissions.size) {
            item {
                PrimaryButton(
                    text = "Загрузить ещё",
                    onClick = { visibleCount = (visibleCount + PAGE_SIZE).coerceAtMost(submissions.size) },
                )
            }
        }
    }
}
