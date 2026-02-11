package com.example.campuskit.data.mess

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "yuck_items")
data class YuckItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String,
)
