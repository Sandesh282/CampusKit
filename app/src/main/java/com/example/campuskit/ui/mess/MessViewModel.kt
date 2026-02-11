package com.example.campuskit.ui.mess

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.data.AppDatabase
import com.example.campuskit.data.mess.MessMenuData
import com.example.campuskit.data.mess.YuckItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MessViewModel(application: Application) : AndroidViewModel(application) {

    private val yuckDao = AppDatabase.getInstance(application).yuckItemDao()

    val yuckItems = yuckDao.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList(),
    )

    private val _todayMenu = MutableStateFlow(MessMenuData.getTodayMenu())
    val todayMenu: StateFlow<com.example.campuskit.data.mess.DayMenu> = _todayMenu.asStateFlow()

    private val _weeklyMenu = MutableStateFlow(MessMenuData.getWeeklyMenu())
    val weeklyMenu = _weeklyMenu.asStateFlow()

    fun addYuckItem(name: String) {
        viewModelScope.launch {
            yuckDao.insert(YuckItemEntity(itemName = name.trim()))
        }
    }

    fun removeYuckItem(name: String) {
        viewModelScope.launch {
            yuckDao.deleteByName(name)
        }
    }

    fun isYuckItem(itemName: String, yuckList: List<YuckItemEntity>): Boolean {
        return yuckList.any { yuck ->
            itemName.contains(yuck.itemName, ignoreCase = true)
        }
    }
}
