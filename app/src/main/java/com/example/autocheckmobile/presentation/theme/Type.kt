package com.example.autocheckmobile.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.autocheckmobile.R

val geist = FontFamily(
    Font(R.font.geist_bold, weight = FontWeight.Bold),
    Font(R.font.geist_semibold, weight = FontWeight.SemiBold),
    Font(R.font.geist, weight = FontWeight.Normal)


)

@Immutable
data class CustomTypography(
    val geistBold24: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.8.sp,
        letterSpacing = (-0.6).sp
    ),
    val geistSemiBold32: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 38.4.sp,
        letterSpacing = (-0.48).sp
    ),
    val geistNormal16: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 25.6.sp,
        letterSpacing = 0.sp
    ),
    val geistSemiBold12: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        letterSpacing = (0.6).sp
    ),
    val geistBold48: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 52.8.sp,
        letterSpacing = (-0.96).sp
    ),
    val geistNormal14: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = (0).sp
    ),
    val geistBold14: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = (0).sp
    ),
    val geistNormal10: TextStyle = TextStyle(
        fontFamily = geist,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 15.sp,
        letterSpacing = (0).sp
    ),
)

val LocalCustomTypography = staticCompositionLocalOf {
    CustomTypography()
}