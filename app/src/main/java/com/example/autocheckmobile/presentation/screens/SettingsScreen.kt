package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.autocheckmobile.presentation.components.PrimaryButton
import com.example.autocheckmobile.presentation.components.ButtonVariant
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space16H
import com.example.autocheckmobile.presentation.viewModel.UserSession

/**
 * Назначение: экран профиля и выхода из системы.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun SettingsScreen(
    session: UserSession,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Log.i("[SettingsScreen]", "Отрисовка — user=${session.email}")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.space24),
    ) {
        Text(
            text = "Настройки",
            color = DesignTokens.TextPrimary,
            style = CustomTheme.typography.geistSemiBold32,
        )
        Space16H()
        Text(text = session.fullName, color = DesignTokens.TextPrimary, style = CustomTheme.typography.geistBold14)
        Text(text = session.email, color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
        Text(text = "Роль: ${session.role}", color = DesignTokens.TextMuted, style = CustomTheme.typography.geistNormal14)
        Space16H()
        PrimaryButton(text = "Выйти", onClick = onLogout, variant = ButtonVariant.Danger)
    }
}
