package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.R
import com.example.autocheckmobile.presentation.theme.Back
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.LightBlue
import com.example.autocheckmobile.presentation.theme.SlateBlue
import com.example.autocheckmobile.presentation.theme.White

// Назначение:
// Автор: Дерябина В.Н.
// Дата создания: 30-05-2026

data class BottomBarData(
    val icon: Int,
    val title: String,
    val onClick: () -> Unit = {}
)

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    data: List<BottomBarData> = listOf(
        BottomBarData(
            icon = R.drawable.dashboard,
            title = "Дашборд"
        ),
        BottomBarData(
            icon = R.drawable.dashboard,
            title = "Дашборд"
        ),
        BottomBarData(
            icon = R.drawable.dashboard,
            title = "Дашборд"
        ),
    ),
    selected: Int = 0
) {
    Log.i("[BottomBar]", "Создание - Отрисовка BottomBar")
    Row(
        modifier = modifier
            .background(Back, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .border(1.dp, White.copy(0.08f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        data.forEachIndexed { index, data ->
            if (index == selected) {
                Column(
                    modifier = Modifier
                        .background(SlateBlue.copy(0.5f), RoundedCornerShape(12.dp))
                        .padding(7.2.dp)
                        .clickable(
                            onClick = data.onClick
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(data.icon),
                        contentDescription = "",
                        tint = LightBlue
                    )
                    Text(
                        text = data.title,
                        color = LightBlue,
                        style = CustomTheme.typography.geistSemiBold12
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(7.2.dp)
                        .clickable(
                            onClick = data.onClick
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(data.icon),
                        contentDescription = "",
                        tint = LightBlue.copy(0.6f),

                        )
                    Text(
                        text = data.title,
                        color = LightBlue.copy(0.6f),
                        style = CustomTheme.typography.geistSemiBold12
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BottomBarPreview() {
    CustomTheme {
        BottomBar(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

