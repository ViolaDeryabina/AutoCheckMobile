package com.example.autocheckmobile.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.R
import com.example.autocheckmobile.presentation.components.BottomBar
import com.example.autocheckmobile.presentation.components.HeaderAvatar
import com.example.autocheckmobile.presentation.components.MainCard
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DarkBlue
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.LightBlue
import com.example.autocheckmobile.presentation.theme.LightGrayBlue
import com.example.autocheckmobile.presentation.theme.MintGreen
import com.example.autocheckmobile.presentation.theme.PaleBlue
import com.example.autocheckmobile.presentation.theme.Space24H

// Назначение:
// Автор: Дерябина В.Н.
// Дата создания: 31-05-2026

@Composable
fun DashBoard(modifier: Modifier = Modifier) {
    Log.i("[DashBoard]", "Создание - Отрисовка DashBoard")
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
                    modifier = Modifier.fillMaxWidth()
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
            Text(
                text = "Панель управления",
                color = PaleBlue,
                style = CustomTheme.typography.geistSemiBold32
            )
            Text(
                text = "Добро пожаловать, администратор. Система работает в штатном режиме.",
                color = LightGrayBlue,
                style = CustomTheme.typography.geistNormal16
            )
            Spacer(modifier = Modifier.height(40.28.dp))
            MainCard(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column() {
                            Text(
                                text = "ВСЕГО ТЕСТОВ",
                                color = LightGrayBlue,
                                style = CustomTheme.typography.geistSemiBold12
                            )
                            Text(
                                text = "1,284",
                                color = LightGrayBlue,
                                style = CustomTheme.typography.geistBold48
                            )
                            Space24H()
                            Row() {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.up_line),
                                    contentDescription = "",
                                    tint = MintGreen

                                )
                                Text(
                                    text = "+12.5% за неделю",
                                    color = MintGreen,
                                    style = CustomTheme.typography.geistSemiBold12
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    LightBlue.copy(0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.file),
                                contentDescription = "",
                                tint = LightBlue

                            )
                        }
                    }

                }
            )
            Space24H()
            MainCard(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column() {
                            Text(
                                text = "УСПЕШНОСТЬ",
                                color = LightGrayBlue,
                                style = CustomTheme.typography.geistSemiBold12
                            )
                            Text(
                                text = "+12.5% за неделю",
                                color = MintGreen,
                                style = CustomTheme.typography.geistSemiBold12
                            )
                        }
                    }

                }
            )

        }
    }
}

@Preview
@Composable
private fun DashBoardPreview() {
    CustomTheme {
        DashBoard()
    }
}

