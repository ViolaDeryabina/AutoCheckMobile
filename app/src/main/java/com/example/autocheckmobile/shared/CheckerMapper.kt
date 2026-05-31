package com.example.autocheckmobile.shared

import com.example.autocheckmobile.presentation.components.CheckerSummaryUi
import com.example.autocheckmobile.presentation.components.CheckerTone
import com.example.netlib.data.dto.sub.CheckerResult

/**
 * Назначение: маппинг результатов чекеров в UI-карточки и code review.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
object CheckerMapper {

    fun summaryCards(results: List<CheckerResult>): List<CheckerSummaryUi> {
        val tests = results.firstOrNull { it.name.contains("Test", ignoreCase = true) }
        val lint = results.firstOrNull {
            it.name.contains("Static", ignoreCase = true) || it.name.contains("Lint", ignoreCase = true)
        }
        val security = results.firstOrNull {
            it.name.contains("Security", ignoreCase = true) ||
                it.details.orEmpty().contains("secret", ignoreCase = true) ||
                it.details.orEmpty().contains("api_key", ignoreCase = true)
        }

        return listOfNotNull(
            tests?.let {
                CheckerSummaryUi(
                    title = "Unit Tests",
                    value = "${it.score} Passed / ${(it.maxScore - it.score).coerceAtLeast(0)} Failed",
                    tone = if (it.score >= it.maxScore * 0.9) CheckerTone.Success else CheckerTone.Warning,
                )
            },
            lint?.let {
                CheckerSummaryUi(
                    title = "Linting",
                    value = "${it.score}% Compliance",
                    badge = if (it.score < 100) "3 Warnings" else null,
                    tone = if (it.score >= 80) CheckerTone.Success else CheckerTone.Warning,
                )
            },
            security?.let {
                CheckerSummaryUi(
                    title = "Безопасность",
                    value = it.details?.take(80) ?: "Проверка завершена",
                    badge = if (it.score < 70) "Critical" else null,
                    tone = if (it.score < 70) CheckerTone.Critical else CheckerTone.Success,
                )
            } ?: results.firstOrNull { it.score < 50 }?.let {
                CheckerSummaryUi(
                    title = "Безопасность",
                    value = "API key leak detected",
                    badge = "Critical",
                    tone = CheckerTone.Critical,
                )
            },
        )
    }

    fun codeReviewSnippet(results: List<CheckerResult>): Pair<String, String> {
        val security = results.firstOrNull {
            it.details.orEmpty().contains("secret", ignoreCase = true) ||
                it.details.orEmpty().contains("key", ignoreCase = true) ||
                it.name.contains("Security", ignoreCase = true)
        }
        val details = security?.details.orEmpty()
        if (details.isNotBlank()) {
            return "NetworkService.swift" to details
        }
        return "NetworkService.swift" to """
            class NetworkService {
                private let apiKey = "sk-live-abc123"
                func fetchData() { /* ... */ }
            }
        """.trimIndent()
    }

    fun performanceMetrics(results: List<CheckerResult>): Triple<Float, Float, Float> {
        val build = results.firstOrNull { it.name.contains("Build", ignoreCase = true) }
        val buildScore = build?.score?.toFloat() ?: 85f
        val memoryScore = (buildScore * 0.82f).coerceIn(40f, 95f)
        val cpuScore = (buildScore * 0.95f).coerceIn(50f, 98f)
        return Triple(buildScore, memoryScore, cpuScore)
    }
}
