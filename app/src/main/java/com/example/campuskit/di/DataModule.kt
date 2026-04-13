package com.example.campuskit.di

import android.content.Context
import com.example.campuskit.data.AppDatabase
import com.example.campuskit.data.attendance.AttendanceDao
import com.example.campuskit.data.attendance.TimetableDao
import com.example.campuskit.data.calendar.CalendarEventDao
import com.example.campuskit.data.events.EventDao
import com.example.campuskit.data.lostfound.LostFoundDao
import com.example.campuskit.data.mess.YuckItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao {
        return database.attendanceDao()
    }

    @Provides
    fun provideTimetableDao(database: AppDatabase): TimetableDao {
        return database.timetableDao()
    }

    @Provides
    fun provideYuckItemDao(database: AppDatabase): YuckItemDao {
        return database.yuckItemDao()
    }

    @Provides
    fun provideOfferingDao(database: AppDatabase): com.example.campuskit.data.academic.local.dao.OfferingDao {
        return database.offeringDao()
    }

    @Provides
    fun provideResourceDao(database: AppDatabase): com.example.campuskit.data.academic.local.dao.ResourceDao {
        return database.resourceDao()
    }

    @Provides
    fun provideSyncMetadataDao(database: AppDatabase): com.example.campuskit.data.academic.local.dao.SyncMetadataDao {
        return database.syncMetadataDao()
    }

    /** Provides [EventDao] for Hilt injection. */
    @Provides
    fun provideEventDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }

    /** Provides [LostFoundDao] for Hilt injection. */
    @Provides
    fun provideLostFoundDao(database: AppDatabase): LostFoundDao {
        return database.lostFoundDao()
    }

    /** Provides [CalendarEventDao] for Hilt injection. */
    @Provides
    fun provideCalendarEventDao(database: AppDatabase): CalendarEventDao {
        return database.calendarEventDao()
    }
}
