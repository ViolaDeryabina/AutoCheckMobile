package com.example.autocheckmobile.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircleWithGradient(
    modifier: Modifier = Modifier,
    borderWidth: Float = 5f,
    startColor: Color = Color(0xFF4D8EFF),
    endColor: Color = Color(0xFF45DFA4),
    textColor: Color = Color.White,
    text: String = ""
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Круг с градиентом
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2
            val borderOffset = borderWidth.dp.toPx() / 2

            val brush = Brush.linearGradient(
                colors = listOf(endColor, startColor),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )

            drawCircle(
                brush = brush,
                radius = radius - borderOffset,
                center = Offset(centerX, centerY),
                style = Stroke(
                    width = borderWidth.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        // Текст по центру
        Text(
            text = text,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CircleWithGradientPreview() {
    CircleWithGradient(
        modifier = Modifier.size(80.dp),
        startColor = Color(0xFF4D8EFF),
        endColor = Color(0xFF45DFA4),
        textColor = Color.Black,
        text = "42"
    )
}