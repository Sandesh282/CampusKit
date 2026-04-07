package com.example.campuskit.data.lostfound

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Room DAO for [LostFoundEntity] CRUD operations. */
@Dao
interface LostFoundDao {

    /** Returns all lost-and-found items, newest first. */
    @Query("SELECT * FROM lost_found ORDER BY rowid DESC")
    fun getAllItems(): Flow<List<LostFoundEntity>>

    /** Inserts a single item. Replaces on conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LostFoundEntity)

    /** Bulk-inserts items. Ignores duplicates by primary key. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<LostFoundEntity>)

    /** Deletes a specific item. */
    @Delete
    suspend fun delete(item: LostFoundEntity)

    /** Returns the total number of lost-and-found items. Used by [LostFoundRepository.seedIfEmpty]. */
    @Query("SELECT COUNT(*) FROM lost_found")
    suspend fun getCount(): Int
}
