package com.example.campuskit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campuskit.ui.navigation.MainNavigation
import com.example.campuskit.ui.theme.CampusKitTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusKitTheme {
                MainNavigation()
            }
        }
    }
}
