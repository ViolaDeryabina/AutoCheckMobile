package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.components.FilterChip
import com.example.autocheckmobile.presentation.components.MainCard
import com.example.autocheckmobile.presentation.components.StatusBadge
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space12H
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.autocheckmobile.shared.DateFormatter
import com.example.netlib.data.dto.sub.SubmissionItem

/**
 * Назначение: поиск и фильтрация проверок (экран «Тесты»).
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun TestCandidates(
    submissions: List<SubmissionItem>,
    assignmentTitle: (Int) -> String,
    onSelectSubmission: (SubmissionItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("all") }
    Log.i("[TestCandidates]", "Отрисовка — submissions=${submissions.size}")

    val filtered = submissions.filter { item ->
        val matchesQuery = query.isBlank() ||
            assignmentTitle(item.assignmentId).contains(query, ignoreCase = true) ||
            item.id.toString().contains(query)
        val matchesFilter = when (filter) {
            "done" -> item.status == "done"
            "running" -> item.status == "running" || item.status == "pending"
            else -> true
        }
        matchesQuery && matchesFilter
    }

    val passPct = if (submissions.isEmpty()) 0.0 else {
        submissions.count { it.status == "done" } * 100.0 / submissions.size
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.space16),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainCard(modifier = Modifier.weight(1f)) {
                Text("Всего тестов", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
                Text(text = "${submissions.size}", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold24)
            }
            MainCard(modifier = Modifier.weight(1f)) {
                Text("Процент прохождения", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistSemiBold12)
                Text(text = "%.1f%%".format(passPct), color = DesignTokens.Success, style = CustomTheme.typography.geistBold24)
            }
        }

        Space16H()
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск кандидата или теста...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DesignTokens.TextPrimary,
                unfocusedTextColor = DesignTokens.TextPrimary,
                focusedContainerColor = DesignTokens.Card,
                unfocusedContainerColor = DesignTokens.Card,
            ),
        )

        Space12H()
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(label = "Все", selected = filter == "all", onClick = { filter = "all" })
            FilterChip(label = "AI тикеты", selected = filter == "running", onClick = { filter = "running" })
            FilterChip(label = "Завершенные", selected = filter == "done", onClick = { filter = "done" })
        }

        Space16H()
        Text("Недавние результаты", color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
        Space12H()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.id }) { item ->
                MainCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable { onSelectSubmission(item) },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Кандидат #${item.candidateId}",
                                color = DesignTokens.TextPrimary,
                                style = CustomTheme.typography.geistBold14,
                            )
                            Text(
                                text = assignmentTitle(item.assignmentId),
                                color = DesignTokens.TextMuted,
                                style = CustomTheme.typography.geistNormal14,
                            )
                            Text(
                                text = DateFormatter.formatDateOnly(item.createdAt),
                                color = DesignTokens.TextMuted,
                                style = CustomTheme.typography.geistNormal10,
                            )
                        }
                        StatusBadge(status = item.status)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = DesignTokens.TextMuted,
                            modifier = Modifier.padding(start = Dimens.space8),
                        )
                    }
                }
            }
        }
    }
}
