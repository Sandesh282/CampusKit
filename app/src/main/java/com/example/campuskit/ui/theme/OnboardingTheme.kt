package com.example.campuskit.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
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
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = OnboardingLightColorScheme,
        typography = CampusKitTypography,
        shapes = CampusKitShapes,
        content = content,
    )
}
