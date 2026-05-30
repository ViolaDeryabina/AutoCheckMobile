package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.presentation.theme.Back
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DarkBlue
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.White

// Назначение: Создание компонента карточки
// Автор: Дерябина В.Н.
// Дата создания: 30-05-2026

@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},

    ) {
    Log.i("[MainCard]", "Создание - Отрисовка MainCard")
    Column(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        DarkBlue.copy(0.9f),
                        Back.copy(0.7f),
                    )
                ),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, White.copy(0.08f), RoundedCornerShape(12.dp))
            .padding(Dimens.space24)
    ) {
        content()
    }
}

@Preview
@Composable
private fun MainCardPreview() {
    CustomTheme {
        MainCard(
            modifier = Modifier.fillMaxWidth(),
            content = {
                Text(
                    "12345"
                )
            }
        )
    }
}

