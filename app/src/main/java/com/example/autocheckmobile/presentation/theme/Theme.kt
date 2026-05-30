package com.example.autocheckmobile.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun CustomTheme(
    content: @Composable () -> Unit
) {
    val customTypography = CustomTypography()

    CompositionLocalProvider(
        LocalCustomTypography provides customTypography,
        content = content
    )
}

object CustomTheme {
    val typography: CustomTypography
        @Composable
        get() = LocalCustomTypography.current
}