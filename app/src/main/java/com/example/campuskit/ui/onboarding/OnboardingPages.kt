package com.example.campuskit.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.ui.theme.OnboardingAccent
import com.example.campuskit.ui.theme.OnboardingAccentGreen
import com.example.campuskit.ui.theme.OnboardingBackground
import com.example.campuskit.ui.theme.OnboardingCardBg
import com.example.campuskit.ui.theme.OnboardingCardShadow
import com.example.campuskit.ui.theme.OnboardingText
import com.example.campuskit.ui.theme.OnboardingTextSecondary
import com.example.campuskit.ui.theme.InterFontFamily
import com.example.campuskit.ui.theme.SquircleShape

// Data for each onboarding page
data class OnboardingPageData(
    val heading: String,
    val subtext: String,
    val ctaLabel: String,
)

val onboardingPages = listOf(
    OnboardingPageData(
        heading = "Welcome to IIITL",
        subtext = "Let's get you started with smarter campus management.",
        ctaLabel = "Get Started",
    ),
    OnboardingPageData(
        heading = "Plan your day\nwith clarity",
        subtext = "Track classes, assignments, and reminders — all in one timeline.",
        ctaLabel = "Continue",
    ),
    OnboardingPageData(
        heading = "Stay consistent.\nStay ahead.",
        subtext = "Monitor progress, manage goals, and build habits that last.",
        ctaLabel = "Continue",
    ),
    OnboardingPageData(
        heading = "Everything campus.\nOne place.",
        subtext = "Whether it's attendance, test scores, or upcoming events — CampusKit keeps you informed and in control.",
        ctaLabel = "Let's Go",
    ),
)

// Screen 1 — Welcome to IIITL (tilted calendar preview)
@Composable
fun WelcomePage() {
    OnboardingPageLayout(pageIndex = 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {
            // Tilted calendar preview card
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .rotate(-3f)
                    .shadow(24.dp, SquircleShape, ambientColor = OnboardingCardShadow, spotColor = OnboardingCardShadow)
                    .clip(SquircleShape)
                    .background(OnboardingCardBg)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Today", fontFamily = InterFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OnboardingText)
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = OnboardingAccent, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    MockTimelineItem("Research Plan", "9:20 — 10:45", OnboardingAccent.copy(alpha = 0.15f), OnboardingAccent)
                    Spacer(modifier = Modifier.height(10.dp))
                    MockTimelineItem("Team Meeting", "11:30 — 12:00", OnboardingAccentGreen.copy(alpha = 0.15f), OnboardingAccentGreen)
                    Spacer(modifier = Modifier.height(10.dp))
                    MockTimelineItem("Design Review", "12:20 — 14:30", Color(0xFFF5C28A).copy(alpha = 0.2f), Color(0xFFE8A250))
                }
            }
        }
    }
}

// Screen 2 — Daily Control (calendar timeline)
@Composable
fun DailyPage() {
    OnboardingPageLayout(pageIndex = 1) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(290.dp)
                    .shadow(20.dp, SquircleShape, ambientColor = OnboardingCardShadow, spotColor = OnboardingCardShadow)
                    .clip(SquircleShape)
                    .background(OnboardingCardBg)
                    .padding(20.dp)
            ) {
                Column {
                    // Day strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Mon" to "05", "Tue" to "06", "Wed" to "07", "Thu" to "08", "Fri" to "09").forEach { (day, date) ->
                            val isSelected = day == "Tue"
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) OnboardingAccent else Color.Transparent)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(day, fontSize = 11.sp, fontFamily = InterFontFamily, color = if (isSelected) Color.White else OnboardingTextSecondary, fontWeight = FontWeight.Medium)
                                Text(date, fontSize = 16.sp, fontFamily = InterFontFamily, color = if (isSelected) Color.White else OnboardingText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    MockTimelineItem("Sprint Planning", "10:00 — 11:00", OnboardingAccent.copy(alpha = 0.12f), OnboardingAccent)
                    Spacer(modifier = Modifier.height(8.dp))
                    MockTimelineItem("Code Review", "14:00 — 15:30", OnboardingAccentGreen.copy(alpha = 0.12f), OnboardingAccentGreen)
                }
            }
        }
    }
}

// Screen 3 — Tasks & Habits
@Composable
fun HabitsPage() {
    OnboardingPageLayout(pageIndex = 2) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .shadow(20.dp, SquircleShape, ambientColor = OnboardingCardShadow, spotColor = OnboardingCardShadow)
                    .clip(SquircleShape)
                    .background(OnboardingCardBg)
                    .padding(20.dp)
            ) {
                Column {
                    Text("This Week", fontFamily = InterFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnboardingText)
                    Spacer(modifier = Modifier.height(16.dp))
                    MockChecklistItem("Complete Assignment 3", true)
                    Spacer(modifier = Modifier.height(12.dp))
                    MockChecklistItem("Read Chapter 7", true)
                    Spacer(modifier = Modifier.height(12.dp))
                    MockChecklistItem("Submit Lab Report", false)
                    Spacer(modifier = Modifier.height(12.dp))
                    MockChecklistItem("Revise Lecture Notes", false)
                    Spacer(modifier = Modifier.height(16.dp))
                    // Progress bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = OnboardingAccentGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("50% completed", fontFamily = InterFontFamily, fontSize = 13.sp, color = OnboardingAccentGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Screen 4 — Campus Control (stacked cards)
@Composable
fun CampusControlPage() {
    OnboardingPageLayout(pageIndex = 3) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            contentAlignment = Alignment.Center
        ) {
            // Bottom card — Events
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .offset(y = 40.dp)
                    .rotate(2f)
                    .shadow(12.dp, SquircleShape, ambientColor = OnboardingCardShadow)
                    .clip(SquircleShape)
                    .background(OnboardingCardBg)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Event, contentDescription = null, tint = Color(0xFFE88CDA), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Upcoming Events", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = OnboardingText)
                        Text("3 this week", fontFamily = InterFontFamily, fontSize = 12.sp, color = OnboardingTextSecondary)
                    }
                }
            }

            // Middle card — Scores
            Box(
                modifier = Modifier
                    .width(270.dp)
                    .offset(y = 0.dp)
                    .rotate(-1f)
                    .shadow(16.dp, SquircleShape, ambientColor = OnboardingCardShadow)
                    .clip(SquircleShape)
                    .background(OnboardingCardBg)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.School, contentDescription = null, tint = OnboardingAccent, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Test Scores", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = OnboardingText)
                        Text("GPA: 8.7", fontFamily = InterFontFamily, fontSize = 12.sp, color = OnboardingTextSecondary)
                    }
                }
            }

            // Top card — Attendance
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .offset(y = (-40).dp)
                    .shadow(24.dp, SquircleShape, ambientColor = OnboardingCardShadow)
                    .clip(SquircleShape)
                    .background(OnboardingCardBg)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = OnboardingAccentGreen, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Attendance", fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = OnboardingText)
                        Text("87% — 3 safe bunks left", fontFamily = InterFontFamily, fontSize = 12.sp, color = OnboardingAccentGreen)
                    }
                }
            }
        }
    }
}

// Layout wrapper shared across all pages
@Composable
private fun OnboardingPageLayout(
    pageIndex: Int,
    previewContent: @Composable () -> Unit,
) {
    val data = onboardingPages[pageIndex]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBackground)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Preview card area
        previewContent()

        Spacer(modifier = Modifier.height(40.dp))

        // Heading
        Text(
            text = data.heading,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = OnboardingText,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtext
        Text(
            text = data.subtext,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = OnboardingTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.fillMaxWidth(0.85f),
        )
    }
}

// Mock timeline event row
@Composable
private fun MockTimelineItem(title: String, time: String, bgColor: Color, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 32.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = OnboardingText)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = OnboardingTextSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(time, fontFamily = InterFontFamily, fontSize = 11.sp, color = OnboardingTextSecondary)
            }
        }
    }
}

// Mock checklist item
@Composable
private fun MockChecklistItem(text: String, isChecked: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = if (isChecked) OnboardingAccentGreen else OnboardingTextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontFamily = InterFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isChecked) OnboardingTextSecondary else OnboardingText,
        )
    }
}
