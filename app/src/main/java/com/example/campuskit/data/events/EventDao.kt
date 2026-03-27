package com.example.campuskit.data.events

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Room DAO for [EventEntity] CRUD operations. */
@Dao
interface EventDao {

    /** Returns all events, ordered by dateTime. */
    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    /** Inserts a single event. Replaces on conflict (e.g. re-seeding). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    /** Bulk-inserts events. Ignores duplicates by primary key. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(events: List<EventEntity>)

    /** Toggles the reminder flag for a given event. */
    @Query("UPDATE events SET isReminded = NOT isReminded WHERE id = :eventId")
    suspend fun toggleReminder(eventId: String)

    /** Returns the set of event IDs that have reminders enabled. */
    @Query("SELECT id FROM events WHERE isReminded = 1")
    fun getRemindedEventIds(): Flow<List<String>>

    /** Returns the number of events currently stored. Used for seed check. */
    @Query("SELECT COUNT(*) FROM events")
    suspend fun getCount(): Int
}
