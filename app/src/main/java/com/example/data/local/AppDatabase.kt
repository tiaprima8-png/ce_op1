package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ActivityMasterDao
import com.example.data.local.dao.ActivityReportDao
import com.example.data.local.dao.OperatorDao
import com.example.data.local.dao.UnitDao
import com.example.data.local.entity.ActivityMasterEntity
import com.example.data.local.entity.ActivityReportEntity
import com.example.data.local.entity.OperatorEntity
import com.example.data.local.entity.UnitEntity

@Database(
    entities = [
        UnitEntity::class,
        ActivityMasterEntity::class,
        OperatorEntity::class,
        ActivityReportEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unitDao(): UnitDao
    abstract fun activityMasterDao(): ActivityMasterDao
    abstract fun operatorDao(): OperatorDao
    abstract fun activityReportDao(): ActivityReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "heavytrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
