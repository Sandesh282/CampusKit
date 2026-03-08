package com.example.campuskit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.campuskit.data.academic.local.dao.OfferingDao
import com.example.campuskit.data.academic.local.dao.ResourceDao
import com.example.campuskit.data.academic.local.dao.SyncMetadataDao
import com.example.campuskit.data.academic.local.entity.OfferingEntity
import com.example.campuskit.data.academic.local.entity.ResourceEntity
import com.example.campuskit.data.academic.local.entity.SubjectEntity
import com.example.campuskit.data.academic.local.entity.SyncMetadataEntity
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
        SubjectEntity::class,
        OfferingEntity::class,
        ResourceEntity::class,
        SyncMetadataEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDao
    abstract fun timetableDao(): TimetableDao
    abstract fun yuckItemDao(): YuckItemDao
    abstract fun offeringDao(): OfferingDao
    abstract fun resourceDao(): ResourceDao
    abstract fun syncMetadataDao(): SyncMetadataDao

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
