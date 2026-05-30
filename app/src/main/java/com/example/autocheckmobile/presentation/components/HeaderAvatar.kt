package com.example.autocheckmobile.presentation.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autocheckmobile.R
import com.example.autocheckmobile.presentation.theme.CustomTheme
import com.example.autocheckmobile.presentation.theme.DarkBlue
import com.example.autocheckmobile.presentation.theme.Dimens
import com.example.autocheckmobile.presentation.theme.Error
import com.example.autocheckmobile.presentation.theme.LightBlue
import com.example.autocheckmobile.presentation.theme.Space12W

// Назначение:
// Автор: Дерябина В.Н.
// Дата создания: 30-05-2026

@Composable
fun HeaderAvatar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onClickSearch: () -> Unit = {},
    borderError: Boolean = true,
    isAvatar: Boolean = true,
    bitmap: ImageBitmap = ImageBitmap.imageResource(R.drawable.user)
) {
    Log.i("[HeaderAvatar]", "Создание - Отрисовка HeaderAvatar")
    Row(
        modifier = modifier
            .background(DarkBlue.copy(0.8f))
            .padding(Dimens.space16),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.header_one),
                contentDescription = "",
                tint = LightBlue,
                modifier = Modifier.clickable(
                    onClick = { onClick() }
                )
            )
            Space12W()
            Text(
                text = "AutoCheck",
                color = LightBlue,
                style = CustomTheme.typography.geistBold24
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.search),
                contentDescription = "",
                tint = LightBlue,
                modifier = Modifier.clickable(
                    onClick = { onClickSearch() }
                )
            )
            if (isAvatar) {
                Space12W()
                Image(
                    bitmap = bitmap,
                    contentDescription = "",
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, if (borderError) Error else LightBlue.copy(0.3f), CircleShape)
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Preview
@Composable
private fun HeaderAvatarPreview() {
    CustomTheme {
        HeaderAvatar(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

