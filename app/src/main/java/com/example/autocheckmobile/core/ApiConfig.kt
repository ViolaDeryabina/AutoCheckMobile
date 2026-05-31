package com.example.autocheckmobile.core

/**
 * Назначение: единая конфигурация API (синхронизирована с net-lib Retrofit baseUrl).
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
object ApiConfig {
    const val BASE_URL = "http://10.61.60.79:8000/api/"

    fun submissionEventsUrl(submissionId: Int): String =
        "${BASE_URL}v1/submissions/$submissionId/events"
}
