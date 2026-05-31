package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DesignTokens

enum class ButtonVariant { Primary, Secondary, Danger }

/**
 * Назначение: кнопка с состояниями primary/secondary/danger/disabled/loading.
 * Дата создания: 31-05-2026
 * Автор создания: Команда AutoCheck
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    fillMaxWidth: Boolean = true,
) {
    Log.d("[PrimaryButton]", "Отрисовка — text=$text loading=$loading")
    val colors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = DesignTokens.Primary,
            contentColor = Color.White,
            disabledContainerColor = DesignTokens.Primary.copy(alpha = 0.5f),
        )
        ButtonVariant.Secondary -> ButtonDefaults.outlinedButtonColors(
            contentColor = DesignTokens.TextMuted,
        )
        ButtonVariant.Danger -> ButtonDefaults.buttonColors(
            containerColor = DesignTokens.Error.copy(alpha = 0.16f),
            contentColor = Color(0xFFFECACA),
        )
    }

    if (variant == ButtonVariant.Secondary) {
        OutlinedButton(
            onClick = onClick,
            modifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier,
            enabled = enabled && !loading,
            colors = colors,
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(DesignTokens.Border),
            ),
        ) {
            ButtonContent(text, loading)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier,
            enabled = enabled && !loading,
            colors = colors,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(DesignTokens.RadiusSm),
        ) {
            ButtonContent(text, loading)
        }
    }
}

@Composable
private fun ButtonContent(text: String, loading: Boolean) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            color = Color.White,
            strokeWidth = 2.dp,
        )
        Text(
            text = "  $text",
            style = CustomTheme.typography.geistBold14,
        )
    } else {
        Text(text = text, style = CustomTheme.typography.geistBold14)
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    CustomTheme {
        PrimaryButton(text = "Войти", onClick = {})
    }
}
