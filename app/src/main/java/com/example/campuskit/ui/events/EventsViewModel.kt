package com.example.campuskit.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.domain.events.FilterEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val filterEventsUseCase: FilterEventsUseCase
) : ViewModel() {

    val events = filterEventsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList(),
    )

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
