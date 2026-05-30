package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.autocheckmobile.presentation.components.BottomBar
import com.example.autocheckmobile.presentation.components.HeaderAvatar
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DarkBlue
import com.example.autocheckmobile.presentation.theme.Dimens

// Назначение:
// Автор: Дерябина В.Н.
// Дата создания: 31-05-2026

@Composable
fun TestCandidates(modifier: Modifier = Modifier) {
    Log.i("[TestCandidates]", "Создание - Отрисовка TestCandidates")
    Scaffold(
        topBar = {
            HeaderAvatar(
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            Box(

            ) {
                BottomBar(
                    modifier = Modifier.fillMaxWidth(),
                    selected = 1
                )

            }
        },
        containerColor = DarkBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimens.space16)
        ) {

        }
    }
}

@Preview
@Composable
private fun TestCandidatesPreview() {
    CustomTheme {
        TestCandidates()
    }
}

