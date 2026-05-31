package com.example.autocheckmobile.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val AutoCheckColorScheme = darkColorScheme(
    primary = DesignTokens.Primary,
    onPrimary = Color.White,
    secondary = DesignTokens.Secondary,
    onSecondary = Color.White,
    background = DesignTokens.Background,
    onBackground = DesignTokens.TextPrimary,
    surface = DesignTokens.Surface,
    onSurface = DesignTokens.TextPrimary,
    error = DesignTokens.Error,
    onError = Color.White,
    outline = DesignTokens.Border,
    surfaceVariant = DesignTokens.Card,
    onSurfaceVariant = DesignTokens.TextMuted,
)

@Composable
fun CustomTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AutoCheckColorScheme,
        content = {
            CompositionLocalProvider(
                LocalCustomTypography provides CustomTypography(),
                content = content,
            )
        },
    )
}

object CustomTheme {
    val typography: CustomTypography
        @Composable
        get() = LocalCustomTypography.current
}
