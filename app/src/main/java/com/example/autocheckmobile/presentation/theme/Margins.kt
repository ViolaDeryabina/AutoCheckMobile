package com.example.autocheckmobile.presentation.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Dimens {
    val space4 = 4.dp
    val space8 = 8.dp
    val space12 = 12.dp
    val space16 = 16.dp
    val space24 = 24.dp
    val space32 = 32.dp
}

// ----- Вертикальные отступы (height) -----
@Composable
fun Space4H() = Spacer(modifier = Modifier.height(Dimens.space4))

@Composable
fun Space8H() = Spacer(modifier = Modifier.height(Dimens.space8))

@Composable
fun Space12H() = Spacer(modifier = Modifier.height(Dimens.space12))

@Composable
fun Space16H() = Spacer(modifier = Modifier.height(Dimens.space16))

@Composable
fun Space24H() = Spacer(modifier = Modifier.height(Dimens.space24))

@Composable
fun Space32H() = Spacer(modifier = Modifier.height(Dimens.space32))

// ----- Горизонтальные отступы (width) -----
@Composable
fun Space4W() = Spacer(modifier = Modifier.width(Dimens.space4))

@Composable
fun Space8W() = Spacer(modifier = Modifier.width(Dimens.space8))

@Composable
fun Space12W() = Spacer(modifier = Modifier.width(Dimens.space12))

@Composable
fun Space16W() = Spacer(modifier = Modifier.width(Dimens.space16))

@Composable
fun Space24W() = Spacer(modifier = Modifier.width(Dimens.space24))

@Composable
fun Space32W() = Spacer(modifier = Modifier.width(Dimens.space32))

