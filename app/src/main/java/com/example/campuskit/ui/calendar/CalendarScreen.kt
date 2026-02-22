package com.example.campuskit.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.TextPrimary

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val calendarMode by viewModel.calendarMode.collectAsState()
    var showAddEventScreen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = calendarMode,
            animationSpec = spring(stiffness = 200f),
            label = "calendarMode",
        ) { mode ->
            when (mode) {
                CalendarMode.GRID -> CalendarGridView(viewModel = viewModel)
                CalendarMode.TIMELINE -> CalendarTimelineView(
                    viewModel = viewModel,
                    onAddEventClick = { showAddEventScreen = true }
                )
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

        AnimatedVisibility(
            visible = showAddEventScreen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.fillMaxSize()
        ) {
            AddEventScreen(
                onDismiss = { showAddEventScreen = false },
                onSave = { event -> 
                    viewModel.addEvent(event)
                    showAddEventScreen = false 
                }
            )
        }
    }
}
