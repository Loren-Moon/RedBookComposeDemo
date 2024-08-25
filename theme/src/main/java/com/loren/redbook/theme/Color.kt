package com.loren.redbook.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val RedBookRed = Color(0xFFFF2E4D)
val WhiteWindow = Color(0xFFF5F6F7)
val WhiteBackground = Color(0xFFFFFFFF)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val BlackWindow = Color(0xFF111111)
val BlackBackground = Color(0xFF1F1D1D)

@Immutable
data class LorenColors(
    val theme: Color,
    val window: Color,
    val background: Color,
    val title: Color,
    val body: Color,
    val icon: Color,
    val divider: Color
)

val lightLorenColors = LorenColors(
    theme = RedBookRed,
    window = WhiteWindow,
    background = WhiteBackground,
    title = Color.Black,
    body = Color(0xFF666666),
    icon = Color.Black,
    divider = Color.LightGray
)

val darkLorenColors = LorenColors(
    theme = RedBookRed,
    window = BlackWindow,
    background = BlackBackground,
    title = Color.White,
    body = Color(0xFF666666),
    icon = Color.White,
    divider = Color.DarkGray
)

val LocalCustomColors = staticCompositionLocalOf { lightLorenColors }