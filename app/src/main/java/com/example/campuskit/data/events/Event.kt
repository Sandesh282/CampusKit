package com.example.campuskit.data.events

/**
 * Domain model representing a campus event.
 *
 * @param id unique identifier
 * @param title event title
 * @param dateTime human-readable date/time string
 * @param venue event location
 * @param posterUrl optional poster image URL
 * @param organizer club or committee name
 * @param description optional event description
 */
/**
 * Domain model representing a campus event.
 */
data class Event(
    val id: String,
    val title: String,
    val dateTime: String,
    val venue: String,
    val posterUrl: String,
    val organizer: String,
    val description: String = "",
)

/** Provides hardcoded campus events for first-launch demo and seeding. */
object MockEvents {
    fun getEvents(): List<Event> = listOf(
        Event(
            id = "1",
            title = "Hackathon 2026",
            dateTime = "Feb 15, 2026 · 9:00 AM",
            venue = "Computer Center Lab 1",
            posterUrl = "",
            organizer = "Coding Club",
            description = "36-hour hackathon. Build something amazing. Prizes worth ₹50K.",
        ),
        Event(
            id = "2",
            title = "Aarohan – Cultural Fest",
            dateTime = "Feb 22, 2026 · 5:00 PM",
            venue = "Open Air Theatre",
            posterUrl = "",
            organizer = "Cultural Senate",
            description = "Annual cultural fest with live performances, dance, and music.",
        ),
        Event(
            id = "3",
            title = "Guest Lecture: AI in Healthcare",
            dateTime = "Feb 18, 2026 · 2:00 PM",
            venue = "Seminar Hall",
            posterUrl = "",
            organizer = "IEEE Student Chapter",
            description = "Dr. Priya Sharma from AIIMS discusses AI applications in diagnostics.",
        ),
        Event(
            id = "4",
            title = "Inter-Hostel Cricket Tournament",
            dateTime = "Feb 20, 2026 · 4:00 PM",
            venue = "Cricket Ground",
            posterUrl = "",
            organizer = "Sports Committee",
            description = "T20 format. Register your hostel team by Feb 18.",
        ),
        Event(
            id = "5",
            title = "Workshop: Intro to Jetpack Compose",
            dateTime = "Feb 25, 2026 · 10:00 AM",
            venue = "LHC Room 204",
            posterUrl = "",
            organizer = "App Development Wing",
            description = "Hands-on workshop for building modern Android UIs.",
        ),
        Event(
            id = "6",
            title = "Open Mic Night",
            dateTime = "Mar 1, 2026 · 7:00 PM",
            venue = "Hostel Common Room",
            posterUrl = "",
            organizer = "Literary Club",
            description = "Poetry, standup, storytelling. All are welcome.",
        ),
    )
}
