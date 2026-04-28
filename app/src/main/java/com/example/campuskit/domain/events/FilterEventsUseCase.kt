package com.example.campuskit.domain.events

import com.example.campuskit.data.events.Event
import com.example.campuskit.data.events.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case that returns campus events from the repository, optionally
 * filtered by a search [query].
 *
 * Matches against [Event.title], [Event.organizer], [Event.venue],
 * and [Event.description] (case-insensitive).
 */
class FilterEventsUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    operator fun invoke(query: String = ""): Flow<List<Event>> {
        return if (query.isBlank()) {
            repository.getEvents()
        } else {
            repository.getEvents().map { events ->
                events.filter { event ->
                    val q = query.trim().lowercase()
                    event.title.lowercase().contains(q) ||
                        event.organizer.lowercase().contains(q) ||
                        event.venue.lowercase().contains(q) ||
                        event.description.lowercase().contains(q)
                }
            }
        }
    }
}
