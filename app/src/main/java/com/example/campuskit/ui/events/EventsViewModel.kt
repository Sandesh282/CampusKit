package com.example.campuskit.ui.events

import androidx.lifecycle.ViewModel
import com.example.campuskit.data.events.MockEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventsViewModel : ViewModel() {

    private val _events = MutableStateFlow(MockEvents.getEvents())
    val events: StateFlow<List<com.example.campuskit.data.events.Event>> = _events.asStateFlow()

    private val _remindedEvents = MutableStateFlow<Set<String>>(emptySet())
    val remindedEvents: StateFlow<Set<String>> = _remindedEvents.asStateFlow()

    fun toggleReminder(eventId: String) {
        _remindedEvents.value = if (eventId in _remindedEvents.value) {
            _remindedEvents.value - eventId
        } else {
            _remindedEvents.value + eventId
        }
    }
}
