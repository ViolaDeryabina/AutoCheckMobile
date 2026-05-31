package com.example.autocheckmobile.shared

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val mskFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    .withZone(ZoneId.of("Europe/Moscow"))

/**
 * Назначение: форматирование UTC-дат в MSK (UTC+3) для отображения в UI.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
object DateFormatter {
    fun formatUtcToMsk(iso: String?): String {
        if (iso.isNullOrBlank()) return "—"
        return runCatching {
            mskFormatter.format(Instant.parse(iso))
        }.getOrDefault(iso)
    }

    fun formatDateOnly(iso: String?): String {
        if (iso.isNullOrBlank()) return "—"
        return formatUtcToMsk(iso).substringBefore(" ")
    }
}
