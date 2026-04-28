package com.example.campuskit.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CampusKitDarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Black,
    primaryContainer = AccentBlue.copy(alpha = 0.15f),
    onPrimaryContainer = AccentBlue,
    secondary = AccentPurple,
    onSecondary = Black,
    secondaryContainer = AccentPurple.copy(alpha = 0.15f),
    onSecondaryContainer = AccentPurple,
    tertiary = AccentTeal,
    onTertiary = Black,
    background = Black,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceVariant,
    outlineVariant = CardBackground,
)

@Composable
fun CampusKitTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = CampusKitDarkColorScheme,
        typography = CampusKitTypography,
        shapes = CampusKitShapes,
        content = content,
    )
}
