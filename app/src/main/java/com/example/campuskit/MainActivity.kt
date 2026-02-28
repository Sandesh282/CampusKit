package com.example.campuskit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.campuskit.ui.navigation.MainNavigation
import com.example.campuskit.ui.onboarding.OnboardingScreen
import com.example.campuskit.ui.theme.CampusKitTheme
import com.example.campuskit.ui.theme.OnboardingBackground
import com.example.campuskit.ui.theme.OnboardingTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("campuskit_prefs", 0) }
    var showOnboarding by remember { mutableStateOf(!prefs.getBoolean("onboarding_completed", false)) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main app always renders underneath
        CampusKitTheme {
            MainNavigation()
        }

        // Onboarding overlays on top with animated exit
        AnimatedVisibility(
            visible = showOnboarding,
            exit = fadeOut(tween(350, easing = FastOutSlowInEasing)) +
                   slideOutVertically(tween(350, easing = FastOutSlowInEasing)) { it / 3 },
            enter = fadeIn(tween(0)),
        ) {
            OnboardingTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OnboardingBackground)
                ) {
                    OnboardingScreen(
                        onComplete = {
                            prefs.edit().putBoolean("onboarding_completed", true).apply()
                            showOnboarding = false
                        },
                    )
                }
            }
        }
    }
}
