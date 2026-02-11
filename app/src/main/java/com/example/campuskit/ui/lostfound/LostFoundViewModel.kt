package com.example.campuskit.ui.lostfound

import androidx.lifecycle.ViewModel
import com.example.campuskit.data.lostfound.LostFoundItem
import com.example.campuskit.data.lostfound.MockLostFound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LostFoundViewModel : ViewModel() {

    private val _items = MutableStateFlow(MockLostFound.getItems())
    val items: StateFlow<List<LostFoundItem>> = _items.asStateFlow()

    fun addItem(item: LostFoundItem) {
        _items.value = listOf(item) + _items.value
    }
}
