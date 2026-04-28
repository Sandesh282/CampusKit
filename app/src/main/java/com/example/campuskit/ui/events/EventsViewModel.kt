package com.example.campuskit.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.events.Event
import com.example.campuskit.data.events.EventsRepository
import com.example.campuskit.domain.events.FilterEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val filterEventsUseCase: FilterEventsUseCase,
    private val repository: EventsRepository,
) : ViewModel() {

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** Events filtered by the current search query. */
    val events: StateFlow<List<Event>> = _searchQuery
        .flatMapLatest { query -> filterEventsUseCase(query) }
        .stateIn(
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

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleReminder(eventId: String) {
        viewModelScope.launch {
            repository.toggleReminder(eventId)
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
        }
    }
}
