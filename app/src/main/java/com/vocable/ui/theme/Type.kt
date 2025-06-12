package com.vocable.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vocable.R


val customFontFamily = try {
    FontFamily(
        Font(R.font.exo_semibold, FontWeight.SemiBold),
        Font(R.font.exo_medium, FontWeight.Normal),

    )
} catch (e: Exception) {
    e.printStackTrace()
    FontFamily.Default
}

val technoFont = try {
    FontFamily(
        Font(R.font.audiowide, FontWeight.Medium),
    )
} catch (e: Exception) {
    e.printStackTrace()
    FontFamily.Default
}

val Typography = Typography(

    titleLarge = TextStyle(
        fontFamily = technoFont,
        fontWeight = FontWeight.Medium,
        fontSize = 42.sp,
        letterSpacing = 0.5.sp
    ),

    titleMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        letterSpacing = 0.5.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        fontStyle = FontStyle.Italic
    ),

    bodyMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),

    labelSmall = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

)