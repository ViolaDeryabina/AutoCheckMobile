package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.components.FormInput
import com.example.autocheckmobile.presentation.components.PrimaryButton
import com.example.autocheckmobile.presentation.components.ButtonVariant
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Space16H

/**
 * Назначение: экран авторизации и регистрации кандидата.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun AuthScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (fullName: String, email: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mode by rememberSaveable { mutableStateOf("login") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Log.i("[AuthScreen]", "Отрисовка — mode=$mode")

    val emailValid = email.contains("@") && email.contains(".")
    val canSubmit = emailValid && password.length >= 6 && (mode == "login" || fullName.trim().length > 2)

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.space24),
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = if (mode == "login") "Вход в систему" else "Регистрация кандидата",
            color = DesignTokens.TextPrimary,
            style = CustomTheme.typography.geistSemiBold32,
        )
        Space16H()
        Text(
            text = "JWT-аутентификация AutoCheckMobile",
            color = DesignTokens.TextMuted,
            style = CustomTheme.typography.geistNormal16,
        )
        Space16H()

        if (mode == "register") {
            FormInput(
                label = "ФИО",
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Иван Иванов",
            )
            Space16H()
        }

        FormInput(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "you@example.com",
            keyboardType = KeyboardType.Email,
            error = if (email.isNotEmpty() && !emailValid) "Некорректный email" else null,
        )
        Space16H()
        FormInput(
            label = "Пароль",
            value = password,
            onValueChange = { password = it },
            placeholder = "Минимум 6 символов",
            isPassword = true,
            error = if (password.isNotEmpty() && password.length < 6) "Минимум 6 символов" else null,
        )

        if (errorMessage != null) {
            Space16H()
            Text(text = errorMessage, color = DesignTokens.Error, style = CustomTheme.typography.geistNormal14)
        }

        Space16H()
        PrimaryButton(
            text = if (mode == "login") "Войти" else "Создать аккаунт",
            onClick = {
                if (mode == "login") onLogin(email.trim(), password)
                else onRegister(fullName.trim(), email.trim(), password)
            },
            enabled = canSubmit,
            loading = isLoading,
        )
        Space16H()
        PrimaryButton(
            text = if (mode == "login") "Нужна регистрация" else "Есть аккаунт",
            onClick = { mode = if (mode == "login") "register" else "login" },
            variant = ButtonVariant.Secondary,
            enabled = !isLoading,
        )
    }
}
