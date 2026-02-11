package com.example.campuskit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.campuskit.data.attendance.AttendanceDao
import com.example.campuskit.data.attendance.AttendanceEntity
import com.example.campuskit.data.attendance.TimetableDao
import com.example.campuskit.data.attendance.TimetableEntity
import com.example.campuskit.data.mess.YuckItemDao
import com.example.campuskit.data.mess.YuckItemEntity

@Database(
    entities = [
        AttendanceEntity::class,
        TimetableEntity::class,
        YuckItemEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDao
    abstract fun timetableDao(): TimetableDao
    abstract fun yuckItemDao(): YuckItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campuskit_database",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
