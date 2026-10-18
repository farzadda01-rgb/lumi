package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LumiLightColorScheme = lightColorScheme(
    primary = LumiPlum,
    onPrimary = LumiSurfaceWhite,
    primaryContainer = LumiLavenderSubtle,
    onPrimaryContainer = LumiPlumDark,

    secondary = LumiLavender,
    onSecondary = LumiPlumDark,
    secondaryContainer = LumiLavenderSubtle,
    onSecondaryContainer = LumiPlumDark,

    tertiary = LumiCoral,
    onTertiary = LumiCharcoal,
    tertiaryContainer = LumiCoralLight,
    onTertiaryContainer = LumiCharcoal,

    background = LumiWarmIvory,
    onBackground = LumiCharcoal,

    surface = LumiSurfaceWhite,
    onSurface = LumiCharcoal,
    surfaceVariant = LumiWarmGray,
    onSurfaceVariant = LumiMutedGray,

    outline = LumiBorderDivider,
    outlineVariant = LumiLavenderLight,

    error = LumiError,
    onError = LumiSurfaceWhite,
    errorContainer = LumiErrorContainer,
    onErrorContainer = LumiError
)

private val LumiDarkColorScheme = darkColorScheme(
    primary = LumiLavender,
    onPrimary = LumiPlumDark,
    primaryContainer = LumiPlum,
    onPrimaryContainer = LumiSurfaceWhite,

    secondary = LumiLavenderLight,
    onSecondary = LumiPlumDark,
    secondaryContainer = LumiPlumDark,
    onSecondaryContainer = LumiLavenderLight,

    tertiary = LumiCoral,
    onTertiary = LumiCharcoal,
    tertiaryContainer = LumiCoralDark,
    onTertiaryContainer = LumiSurfaceWhite,

    background = LumiCharcoal,
    onBackground = LumiWarmIvory,

    surface = Color(0xFF1E1A22),
    onSurface = LumiWarmIvory,
    surfaceVariant = Color(0xFF2C2632),
    onSurfaceVariant = LumiLavenderLight,

    outline = Color(0xFF3E3646),
    outlineVariant = Color(0xFF50465B),

    error = LumiError,
    onError = LumiSurfaceWhite,
    errorContainer = LumiErrorContainer,
    onErrorContainer = LumiError
)

@Composable
fun LumiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LumiDarkColorScheme else LumiLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LumiTypography,
        content = content
    )
}
