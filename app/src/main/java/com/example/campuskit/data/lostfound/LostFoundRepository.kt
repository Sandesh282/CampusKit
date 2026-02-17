package com.example.campuskit.data.lostfound

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LostFoundRepository @Inject constructor() {
    private val _items = MutableStateFlow(MockLostFound.getItems())
    
    fun getItems(): Flow<List<LostFoundItem>> = _items.asStateFlow()

    fun addItem(item: LostFoundItem) {
        _items.value = listOf(item) + _items.value
    }
}
