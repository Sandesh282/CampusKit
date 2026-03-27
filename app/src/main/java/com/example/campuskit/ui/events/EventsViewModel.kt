package com.example.campuskit.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.events.EventsRepository
import com.example.campuskit.domain.events.FilterEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val filterEventsUseCase: FilterEventsUseCase,
    private val repository: EventsRepository,
) : ViewModel() {

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    val events = filterEventsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList(),
    )

    /** Persisted reminder state from Room. */
    val remindedEvents: StateFlow<Set<String>> = repository.getRemindedEventIds()
        .map { it.toSet() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptySet(),
        )

    fun toggleReminder(eventId: String) {
        viewModelScope.launch {
            repository.toggleReminder(eventId)
        }
    }
}
