package com.example.campuskit.data.mess

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface YuckItemDao {

    @Query("SELECT * FROM yuck_items ORDER BY itemName ASC")
    fun getAll(): Flow<List<YuckItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: YuckItemEntity): Long

    @Delete
    suspend fun delete(entity: YuckItemEntity)

    @Query("DELETE FROM yuck_items WHERE itemName = :name")
    suspend fun deleteByName(name: String)
}
