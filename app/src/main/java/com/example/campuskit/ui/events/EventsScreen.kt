package com.example.campuskit.ui.events

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campuskit.data.events.Event
import com.example.campuskit.ui.theme.AccentBlue
import com.example.campuskit.ui.theme.AccentPurple
import com.example.campuskit.ui.theme.AccentTeal
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.SquircleShape
import com.example.campuskit.ui.theme.StatusGreen
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary

@Composable
fun EventsScreen(viewModel: EventsViewModel = hiltViewModel()) {
    val events by viewModel.events.collectAsState()
    val remindedEvents by viewModel.remindedEvents.collectAsState()

    Scaffold(containerColor = Black) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Events",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "What's happening on campus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (events.isEmpty()) {
                item {
                    EmptyEventsPlaceholder()
                }
            } else {
                itemsIndexed(events, key = { _, event -> event.id }) { index, event ->
                    AnimatedEventCard(
                        event = event,
                        index = index,
                        isReminded = event.id in remindedEvents,
                        onToggleReminder = { viewModel.toggleReminder(event.id) },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun EmptyEventsPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Outlined.EventBusy,
            contentDescription = null,
            tint = TextTertiary.copy(alpha = 0.4f),
            modifier = Modifier.size(72.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No events yet",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Campus events will appear here\nwhen they're posted",
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AnimatedEventCard(
    event: Event,
    index: Int,
    isReminded: Boolean,
    onToggleReminder: () -> Unit,
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(event.id) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = index * 80,
            ),
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = animProgress.value
                translationY = (1f - animProgress.value) * 40f
            },
    ) {
        EventCard(
            event = event,
            isReminded = isReminded,
            onToggleReminder = onToggleReminder,
        )
    }
}

@Composable
private fun EventCard(
    event: Event,
    isReminded: Boolean,
    onToggleReminder: () -> Unit,
) {
    val context = LocalContext.current

    val orgColors = listOf(AccentBlue, AccentPurple, AccentTeal, StatusGreen)
    val orgColor = orgColors[event.id.hashCode().mod(orgColors.size)]

    Card(
        shape = SquircleShape,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Organizer badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(orgColor),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.organizer,
                    style = MaterialTheme.typography.labelMedium,
                    color = orgColor,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 3,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date and venue
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.venue,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Remind Me button
            TextButton(
                onClick = {
                    onToggleReminder()
                    val msg = if (!isReminded) "Reminder set for ${event.title}" else "Reminder removed"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    if (isReminded) Icons.Filled.NotificationsActive else Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    tint = if (isReminded) StatusGreen else AccentBlue,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isReminded) "Reminded ✓" else "Remind Me",
                    color = if (isReminded) StatusGreen else AccentBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}
