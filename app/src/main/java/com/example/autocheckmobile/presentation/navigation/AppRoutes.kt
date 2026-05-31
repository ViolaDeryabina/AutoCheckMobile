package com.example.autocheckmobile.presentation.navigation

/**
 * Назначение: маршруты навигации приложения.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
object AppRoutes {
    const val AUTH = "auth"
    const val MAIN = "main"
    const val SUBMISSION_DETAILS = "submission/{submissionId}"

    fun submissionDetails(submissionId: Int) = "submission/$submissionId"
}
