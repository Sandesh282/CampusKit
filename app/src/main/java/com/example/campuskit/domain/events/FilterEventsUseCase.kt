package com.example.campuskit.domain.events

import com.example.campuskit.data.events.Event
import com.example.campuskit.data.events.EventsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FilterEventsUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    operator fun invoke(query: String = ""): Flow<List<Event>> {
        // Basic filtering logic that can be expanded later
        return repository.getEvents()
    }
}
