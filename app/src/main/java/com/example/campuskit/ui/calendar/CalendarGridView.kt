package com.example.campuskit.ui.calendar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.ui.theme.Black
import com.example.campuskit.ui.theme.CalendarGridEmpty
import com.example.campuskit.ui.theme.TextPrimary
import com.example.campuskit.ui.theme.TextSecondary
import com.example.campuskit.ui.theme.TextTertiary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.example.campuskit.ui.components.EmptyStateView
import androidx.compose.material.icons.outlined.CalendarMonth

@Composable
fun CalendarGridView(viewModel: CalendarViewModel) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val courses = viewModel.courses

    val allAttendedDates = remember(courses) {
        courses.flatMap { course ->
            course.datesAttended.map { date -> date to course.color }
        }.toMap()
    }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek
    val startOffset = (firstDayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
            .statusBarsPadding()
            .graphicsLayer {
                alpha = animProgress.value
                translationY = (1f - animProgress.value) * 20f
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        if (courses.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.CalendarMonth,
                message = "Your calendar is empty.\nAdd a course to start tracking your schedule."
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Keep Track Of Your\nCourse Calendar",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontStyle = FontStyle.Italic,
                lineHeight = 36.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Month navigation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CalendarGridEmpty)
                        .clickable { viewModel.navigateMonth(-1) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CalendarGridEmpty),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${
                        currentMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.ENGLISH
                        )
                    } \\ ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CalendarGridEmpty)
                        .clickable { viewModel.navigateMonth(1) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Day-of-week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                dayLabels.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar grid with weekly percentage
            val totalCells = startOffset + daysInMonth
            val weeks = (totalCells + 6) / 7

            for (week in 0 until weeks) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (dayInWeek in 0 until 7) {
                        val cellIndex = week * 7 + dayInWeek
                        val dayNumber = cellIndex - startOffset + 1

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (dayNumber in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayNumber)
                                val courseColor = allAttendedDates[date]
                                val isSelected = date == selectedDate

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .then(
                                            if (courseColor != null) {
                                                Modifier.background(courseColor)
                                            } else {
                                                Modifier.border(
                                                    width = 1.5.dp,
                                                    color = CalendarGridEmpty,
                                                    shape = CircleShape,
                                                )
                                            },
                                        )
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(
                                                    width = 2.dp,
                                                    color = TextPrimary,
                                                    shape = CircleShape,
                                                )
                                            } else {
                                                Modifier
                                            },
                                        )
                                        .clickable { viewModel.selectDate(date) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (courseColor != null) {
                                        Text(
                                            text = "$dayNumber",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Black,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    } else {
                                        Text(
                                            text = "×",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextTertiary,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Weekly percentage
                    val weekStart = week * 7 - startOffset + 1
                    val weekEnd = weekStart + 6
                    val daysInWeek =
                        (weekStart.coerceAtLeast(1)..weekEnd.coerceAtMost(daysInMonth)).toList()
                    val attendedInWeek = daysInWeek.count { day ->
                        allAttendedDates.containsKey(currentMonth.atDay(day))
                    }
                    val weekPercentage = if (daysInWeek.isNotEmpty()) {
                        (attendedInWeek * 100) / daysInWeek.size
                    } else {
                        0
                    }

                    Text(
                        text = "$weekPercentage%",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(48.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Course legend
            courses.forEach { course ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(course.color),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
