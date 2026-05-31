package com.example.autocheckmobile.presentation.components

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.autocheckmobile.presentation.viewModel.ToastEvent

/**
 * Назначение: глобальный Snackbar для success/error уведомлений.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun ToastHost(
    toast: ToastEvent?,
    snackbarHostState: SnackbarHostState,
    onConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(toast?.id) {
        val event = toast ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(event.message)
        onConsumed()
    }
    SnackbarHost(hostState = snackbarHostState, modifier = modifier)
}
