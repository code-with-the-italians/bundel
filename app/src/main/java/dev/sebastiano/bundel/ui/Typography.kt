package dev.sebastiano.bundel.ui

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sebastiano.bundel.R

private val podkovaBold = Font(R.font.podkova_bold, weight = FontWeight.Bold)
private val podkovaExtraBold = Font(R.font.podkova_extrabold, weight = FontWeight.ExtraBold)
private val podkovaMedium = Font(R.font.podkova_medium, weight = FontWeight.Medium)
private val podkovaRegular = Font(R.font.podkova_regular, weight = FontWeight.Normal)
private val podkovaSemiBold = Font(R.font.podkova_semibold, weight = FontWeight.SemiBold)
private val podkova = FontFamily(
    listOf(
        podkovaBold,
        podkovaExtraBold,
        podkovaMedium,
        podkovaRegular,
        podkovaSemiBold
    )
)

private val interBold = Font(R.font.inter_bold, weight = FontWeight.Bold)
private val interMedium = Font(R.font.inter_medium, weight = FontWeight.Medium)
private val interRegular = Font(R.font.inter_regular, weight = FontWeight.Normal)
private val inter = FontFamily(
    listOf(
        interBold,
        interMedium,
        interRegular,
    )
)

internal val bundelTypography = Typography(
    h1 = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.Light,
        fontSize = 96.sp
    ),
    h2 = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.Normal,
        fontSize = 60.sp
    ),
    h3 = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.SemiBold,
        fontSize = 48.sp
    ),
    h4 = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp
    ),
    h5 = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)

internal val BundelYouTypography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.W400,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.W400,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.W400,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.W400,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.W400,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = podkova,
        fontWeight = FontWeight.W400,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.W400,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = inter,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
