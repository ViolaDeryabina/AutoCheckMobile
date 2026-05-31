package com.example.autocheckmobile.presentation.viewModel

/**
 * Назначение: одноразовое событие для Snackbar-уведомления.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
data class ToastEvent(
    val message: String,
    val isError: Boolean = false,
    val id: Long = System.currentTimeMillis(),
)
