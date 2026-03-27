package com.example.campuskit.data.events

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor(
    private val eventDao: EventDao,
) {

    /** Returns all events as domain models. */
    fun getEvents(): Flow<List<Event>> = eventDao.getAllEvents().map { entities ->
        entities.map { it.toDomain() }
    }

    /** Returns the set of event IDs with reminders enabled. */
    fun getRemindedEventIds(): Flow<List<String>> = eventDao.getRemindedEventIds()

    /** Toggles the reminder flag for a given event. */
    suspend fun toggleReminder(eventId: String) {
        eventDao.toggleReminder(eventId)
    }

    /** Seeds mock events into the database if it's empty (first launch). */
    suspend fun seedIfEmpty() {
        if (eventDao.getCount() == 0) {
            val seedEntities = MockEvents.getEvents().map { it.toEntity() }
            eventDao.insertAll(seedEntities)
        }
    }
}

/** Maps [EventEntity] → domain [Event]. */
private fun EventEntity.toDomain() = Event(
    id = id,
    title = title,
    dateTime = dateTime,
    venue = venue,
    posterUrl = posterUrl,
    organizer = organizer,
    description = description,
)

/** Maps domain [Event] → [EventEntity]. */
private fun Event.toEntity() = EventEntity(
    id = id,
    title = title,
    dateTime = dateTime,
    venue = venue,
    posterUrl = posterUrl,
    organizer = organizer,
    description = description,
)
