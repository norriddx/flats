package com.example.flats.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.flats.R
import androidx.compose.material3.Typography

val RalewayFamily = FontFamily(
    Font(R.font.raleway_regular, FontWeight.Normal),
    Font(R.font.raleway_bold, FontWeight.Bold),
    Font(R.font.raleway_medium, FontWeight.Medium)
)

val RGStandardFamily = FontFamily(
    Font(R.font.rg_standard_bold, FontWeight.Bold)
)

val Typography = Typography(
    // h1
    headlineLarge = TextStyle(
        fontFamily    = RGStandardFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 40.sp,
        letterSpacing = 0.02.em
    ),
    // h2
    headlineMedium = TextStyle(
        fontFamily    = RGStandardFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 24.sp,
        lineHeight    = 28.sp,
        letterSpacing = 0.02.em
    ),
    // h3
    headlineSmall = TextStyle(
        fontFamily    = RGStandardFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 20.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.02.em
    ),
    // p
    bodyLarge = TextStyle(
        fontFamily    = RalewayFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.01.em,
        fontFeatureSettings = "lnum"
    ),
    // small text
    bodyMedium = TextStyle(
        fontFamily    = RalewayFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.01.em,
        fontFeatureSettings = "lnum"
    ),
    // button
    labelLarge = TextStyle(
        fontFamily    = RalewayFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 16.sp,
        lineHeight    = 19.sp,
        letterSpacing = 0.01.em,
        fontFeatureSettings = "lnum"
    )
)

// p bold
val BodyLargeBold = TextStyle(
    fontFamily    = RGStandardFamily,
    fontWeight    = FontWeight.Bold,
    fontSize      = 16.sp,
    lineHeight    = 20.sp,
    letterSpacing = 0.02.em
)