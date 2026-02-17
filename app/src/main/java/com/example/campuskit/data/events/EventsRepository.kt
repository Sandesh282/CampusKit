package com.example.campuskit.data.events

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventsRepository @Inject constructor() {
    fun getEvents(): Flow<List<Event>> = flowOf(MockEvents.getEvents())
}
