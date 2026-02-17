package com.example.campuskit.ui.lostfound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.lostfound.LostFoundItem
import com.example.campuskit.data.lostfound.LostFoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LostFoundViewModel @Inject constructor(
    private val repository: LostFoundRepository
) : ViewModel() {

    val items: StateFlow<List<LostFoundItem>> = repository.getItems().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addItem(item: LostFoundItem) {
        repository.addItem(item)
    }
}
