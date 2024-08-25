package com.loren.redbook.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

enum class AppThemeType {
    FOLLOW_SYSTEM, Light, Dark;

    companion object {
        fun formatTheme(theme: Int? = 1): AppThemeType {
            entries.forEach {
                if (it.ordinal == theme) {
                    return it
                }
            }
            return Light
        }

        @Composable
        fun isDark(themeType: AppThemeType): Boolean {
            return when (themeType) {
                FOLLOW_SYSTEM -> isSystemInDarkTheme()
                Light -> false
                Dark -> true
            }
        }
    }
}

@Composable
fun RedBookTheme(themeType: AppThemeType = AppThemeType.Light, content: @Composable () -> Unit) {
    val colors =
        if (AppThemeType.isDark(themeType = themeType)) darkLorenColors else lightLorenColors

    CompositionLocalProvider(
        LocalCustomColors provides colors,
        LocalTextStyles provides RedBookTheme.textStyle
    ) {
        MaterialTheme(content = content)
    }
}

object RedBookTheme {
    val colors: LorenColors
        @Composable
        get() = LocalCustomColors.current
    val textStyle: LorenTextStyle
        @Composable
        get() = LocalTextStyles.current
}