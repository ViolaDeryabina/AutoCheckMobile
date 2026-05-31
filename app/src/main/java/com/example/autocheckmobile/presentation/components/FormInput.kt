package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens
import com.example.autocheckmobile.presentation.theme.Space4H

/**
 * Назначение: поле ввода с валидацией и отображением ошибки.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun FormInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    error: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Log.d("[FormInput]", "Отрисовка — label=$label")
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            color = DesignTokens.TextMuted,
            style = CustomTheme.typography.geistSemiBold12,
        )
        Space4H()
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (error != null) DesignTokens.Error else DesignTokens.Border,
                    shape = RoundedCornerShape(DesignTokens.RadiusSm),
                ),
            placeholder = {
                Text(text = placeholder, color = DesignTokens.TextMuted)
            },
            enabled = enabled,
            isError = error != null,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DesignTokens.TextPrimary,
                unfocusedTextColor = DesignTokens.TextPrimary,
                focusedContainerColor = DesignTokens.Card,
                unfocusedContainerColor = DesignTokens.Card,
                cursorColor = DesignTokens.Primary,
                focusedBorderColor = DesignTokens.Primary,
                unfocusedBorderColor = DesignTokens.Border,
            ),
            shape = RoundedCornerShape(DesignTokens.RadiusSm),
            singleLine = true,
        )
        if (error != null) {
            Text(
                text = error,
                color = Color(0xFFFECACA),
                style = CustomTheme.typography.geistNormal10,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun FormInputPreview() {
    CustomTheme {
        FormInput(label = "Email", value = "", onValueChange = {}, placeholder = "you@example.com")
    }
}
