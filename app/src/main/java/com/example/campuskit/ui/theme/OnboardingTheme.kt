package com.example.campuskit.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val OnboardingLightColorScheme = lightColorScheme(
    primary = OnboardingAccent,
    onPrimary = OnboardingCardBg,
    secondary = OnboardingAccentGreen,
    onSecondary = OnboardingCardBg,
    background = OnboardingBackground,
    onBackground = OnboardingText,
    surface = OnboardingCardBg,
    onSurface = OnboardingText,
    surfaceVariant = OnboardingBackground,
    onSurfaceVariant = OnboardingTextSecondary,
)

@Composable
fun OnboardingTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = OnboardingBackground.toArgb()
            window.navigationBarColor = OnboardingBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = OnboardingLightColorScheme,
        typography = CampusKitTypography,
        shapes = CampusKitShapes,
        content = content,
    )
}
