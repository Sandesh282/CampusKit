package com.example.campuskit.di

import android.content.Context
import com.example.campuskit.data.AppDatabase
import com.example.campuskit.data.attendance.AttendanceDao
import com.example.campuskit.data.attendance.TimetableDao
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
}
