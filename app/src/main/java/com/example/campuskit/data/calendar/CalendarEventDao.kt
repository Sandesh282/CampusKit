package com.example.campuskit.data.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Room DAO for [CalendarEventEntity] CRUD operations. */
@Dao
interface CalendarEventDao {

    /** Returns all calendar events. */
    @Query("SELECT * FROM calendar_events ORDER BY dateString ASC, startTimeString ASC")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    /** Returns events for a specific date string (ISO format, e.g. "2026-03-28"). */
    @Query("SELECT * FROM calendar_events WHERE dateString = :dateString ORDER BY startTimeString ASC")
    fun getEventsForDate(dateString: String): Flow<List<CalendarEventEntity>>

    /** Inserts a single event. Replaces on conflict. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CalendarEventEntity)

    /** Bulk-inserts events. Ignores duplicates by primary key. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<CalendarEventEntity>)

    /** Deletes a specific event. */
    @Delete
    suspend fun delete(event: CalendarEventEntity)

    /** Returns the total number of calendar events. Used by [CalendarRepository.seedIfEmpty]. */
    @Query("SELECT COUNT(*) FROM calendar_events")
    suspend fun getCount(): Int
}
