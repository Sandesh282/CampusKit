package com.example.campuskit.ui.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.data.calendar.CalendarEvent
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CalendarDateHighlight
import com.example.campuskit.ui.theme.CardBackground
import com.example.campuskit.ui.theme.SquircleShape
import com.example.campuskit.ui.theme.SurfaceVariant
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private const val DAYS_RANGE = 365

@Composable
fun CalendarTimelineView(viewModel: CalendarViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val allEvents = viewModel.events

    // ±365 days for date strip
    val today = remember { LocalDate.now() }
    val dateRange = remember {
        val start = today.minusDays(DAYS_RANGE.toLong())
        generateSequence(start) { it.plusDays(1) }
            .take(DAYS_RANGE * 2 + 1)
            .toList()
    }
    val todayIndex = remember { dateRange.indexOf(today).coerceAtLeast(0) }

    // Group events by date for quick lookup
    val eventsByDate = remember(allEvents) {
        allEvents.groupBy { it.date }
    }

    // Build timeline items: every day gets a section, each with its events underneath
    // We use a sealed class approach: each LazyColumn item is either a DateHeader or an EventItem
    val timelineItems = remember(allEvents) {
        val items = mutableListOf<TimelineItem>()
        for (date in dateRange) {
            items.add(TimelineItem.DateHeader(date))
            val dayEvents = eventsByDate[date]?.sortedBy { it.startTime } ?: emptyList()
            if (dayEvents.isEmpty()) {
                items.add(TimelineItem.EmptyDay(date))
            } else {
                dayEvents.forEach { event ->
                    items.add(TimelineItem.EventCard(event))
                }
            }
        }
        items
    }

    // Map date → index in timelineItems for scroll sync
    val dateToTimelineIndex = remember(timelineItems) {
        val map = mutableMapOf<LocalDate, Int>()
        timelineItems.forEachIndexed { index, item ->
            if (item is TimelineItem.DateHeader && item.date !in map) {
                map[item.date] = index
            }
        }
        map
    }

    val dateStripState = rememberLazyListState(
        initialFirstVisibleItemIndex = (todayIndex - 2).coerceAtLeast(0),
    )
    // Start timeline at today's position
    val todayTimelineIndex = remember { dateToTimelineIndex[today] ?: 0 }
    val timelineListState = rememberLazyListState(
        initialFirstVisibleItemIndex = todayTimelineIndex,
    )

    var isAutoScrolling by remember { mutableStateOf(false) }

    // When selectedDate changes, scroll both strips
    LaunchedEffect(selectedDate) {
        val dateIndex = dateRange.indexOf(selectedDate)
        if (dateIndex >= 0) {
            isAutoScrolling = true
            dateStripState.animateScrollToItem(
                index = (dateIndex - 2).coerceAtLeast(0),
            )
            isAutoScrolling = false
        }

        val timelineIndex = dateToTimelineIndex[selectedDate]
        if (timelineIndex != null) {
            isAutoScrolling = true
            timelineListState.animateScrollToItem(timelineIndex)
            isAutoScrolling = false
        }
    }

    // When timeline is scrolled by user, update selected date
    LaunchedEffect(Unit) {
        snapshotFlow { timelineListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                if (!isAutoScrolling && index in timelineItems.indices) {
                    val visibleDate = when (val item = timelineItems[index]) {
                        is TimelineItem.DateHeader -> item.date
                        is TimelineItem.EventCard -> item.event.date
                        is TimelineItem.EmptyDay -> item.date
                    }
                    if (visibleDate != selectedDate) {
                        viewModel.selectDate(visibleDate)
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .statusBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { viewModel.toggleMode() }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back to grid",
                    tint = TextPrimary,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Calendar",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { }) {
                Icon(
                    Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = TextPrimary,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Month label
        Text(
            text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal date strip
        LazyRow(
            state = dateStripState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(dateRange, key = { it.toEpochDay() }) { date ->
                val isSelected = date == selectedDate
                val hasEvents = eventsByDate.containsKey(date)
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) CalendarDateHighlight else Color.Transparent,
                    animationSpec = spring(stiffness = 300f),
                    label = "dateItemBg",
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) TextPrimary else TextSecondary,
                    animationSpec = spring(stiffness = 300f),
                    label = "dateItemText",
                )

                Column(
                    modifier = Modifier
                        .width(56.dp)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable { viewModel.selectDate(date) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${date.dayOfMonth}",
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                    )
                    // Event dot indicator
                    if (hasEvents && !isSelected) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(CalendarDateHighlight),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Infinite vertical timeline
        LazyColumn(
            state = timelineListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            items(
                count = timelineItems.size,
                key = { index ->
                    when (val item = timelineItems[index]) {
                        is TimelineItem.DateHeader -> "header_${item.date}"
                        is TimelineItem.EventCard -> "event_${item.event.id}"
                        is TimelineItem.EmptyDay -> "empty_${item.date}"
                    }
                },
            ) { index ->
                when (val item = timelineItems[index]) {
                    is TimelineItem.DateHeader -> DateSectionHeader(date = item.date)
                    is TimelineItem.EventCard -> TimelineEventRow(event = item.event)
                    is TimelineItem.EmptyDay -> EmptyDayRow()
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

private sealed class TimelineItem {
    data class DateHeader(val date: LocalDate) : TimelineItem()
    data class EventCard(val event: CalendarEvent) : TimelineItem()
    data class EmptyDay(val date: LocalDate) : TimelineItem()
}

@Composable
private fun DateSectionHeader(date: LocalDate) {
    val isToday = date == LocalDate.now()
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val monthDay = "${date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)} ${date.dayOfMonth}"

    Column(modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.titleSmall,
                color = if (isToday) TextPrimary else TextSecondary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = monthDay,
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary,
            )
            if (isToday) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(CalendarDateHighlight)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(SurfaceVariant),
        )
    }
}

@Composable
private fun EmptyDayRow() {
    Text(
        text = "No events",
        style = MaterialTheme.typography.bodySmall,
        color = TextTertiary,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp),
    )
}

@Composable
private fun TimelineEventRow(event: CalendarEvent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
    ) {
        // Time gutter
        Column(
            modifier = Modifier
                .width(72.dp)
                .padding(top = 8.dp),
        ) {
            Text(
                text = formatTime(event.startTime.hour, event.startTime.minute),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
            )
        }

        // Connector line
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(SurfaceVariant),
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Event card
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(SquircleShape)
                .background(event.color.copy(alpha = 0.85f))
                .padding(16.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${formatTime(event.startTime.hour, event.startTime.minute)}-${formatTime(event.endTime.hour, event.endTime.minute)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Black.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                    )
                }

                if (event.assignee.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Assigned to",
                        style = MaterialTheme.typography.labelSmall,
                        color = Black.copy(alpha = 0.5f),
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Black.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = event.assignee.first().toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Black.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.assignee,
                            style = MaterialTheme.typography.bodySmall,
                            color = Black.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                if (event.attendeeCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.width(((event.attendeeCount.coerceAtMost(3) * 18) + 8).dp)) {
                            repeat(event.attendeeCount.coerceAtMost(3)) { i ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = (i * 16).dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Black.copy(alpha = 0.15f + i * 0.05f)),
                                )
                            }
                        }
                        if (event.attendeeCount > 3) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "+${event.attendeeCount - 3}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return "%d:%02d %s".format(h, minute, amPm)
}
