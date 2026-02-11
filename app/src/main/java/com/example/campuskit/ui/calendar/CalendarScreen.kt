package com.example.campuskit.ui.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.TextPrimary

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val calendarMode by viewModel.calendarMode.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = calendarMode,
            animationSpec = spring(stiffness = 200f),
            label = "calendarMode",
        ) { mode ->
            when (mode) {
                CalendarMode.GRID -> CalendarGridView(viewModel = viewModel)
                CalendarMode.TIMELINE -> CalendarTimelineView(viewModel = viewModel)
            }
        }

        FloatingActionButton(
            onClick = { viewModel.toggleMode() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp),
            containerColor = if (calendarMode == CalendarMode.GRID) AccentBlue else Black.copy(alpha = 0.8f),
            contentColor = TextPrimary,
            shape = CircleShape,
        ) {
            Icon(
                if (calendarMode == CalendarMode.GRID) {
                    Icons.AutoMirrored.Filled.ArrowForward
                } else {
                    Icons.AutoMirrored.Filled.ArrowBack
                },
                contentDescription = "Toggle calendar mode",
            )
        }
    }
}
