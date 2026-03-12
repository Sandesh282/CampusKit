package com.example.campuskit.ui.mess

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.mess.DayMenu
import com.example.campuskit.data.mess.MessMenuData
import com.example.campuskit.data.mess.MessRepository
import com.example.campuskit.data.mess.YuckItemEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessViewModel @Inject constructor(
    private val repository: MessRepository
) : ViewModel() {

    val yuckItems = repository.getAllYuckItems().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList(),
    )

    private val _todayMenu = MutableStateFlow(MessMenuData.getTodayMenu())
    val todayMenu: StateFlow<DayMenu> = _todayMenu.asStateFlow()

    private val _weeklyMenu = MutableStateFlow(MessMenuData.getWeeklyMenu())
    val weeklyMenu = _weeklyMenu.asStateFlow()

    fun addYuckItem(name: String) {
        viewModelScope.launch {
            repository.insertYuckItem(YuckItemEntity(itemName = name.trim()))
        }
    }

    fun removeYuckItem(name: String) {
        viewModelScope.launch {
            repository.deleteYuckItemByName(name)
        }
    }

    fun getMenuForDay(day: java.time.DayOfWeek): DayMenu {
        return MessMenuData.getWeeklyMenu().first { it.day == day }
    }

    fun isYuckItem(itemName: String, yuckList: List<YuckItemEntity>): Boolean {
        return yuckList.any { yuck ->
            itemName.contains(yuck.itemName, ignoreCase = true)
        }
    }
}
