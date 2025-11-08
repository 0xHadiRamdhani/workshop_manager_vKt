package com.banimasum.manager.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.banimasum.manager.models.Student
import com.banimasum.manager.models.Tool
import com.banimasum.manager.models.ToolUsage
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.utils.DateTypeConverter

@Database(
    entities = [
        Student::class,
        Tool::class,
        WorkshopSession::class,
        ToolUsage::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTypeConverter::class)
abstract class WorkshopDatabase : RoomDatabase() {
    
    abstract fun studentDao(): StudentDao
    abstract fun toolDao(): ToolDao
    abstract fun workshopSessionDao(): WorkshopSessionDao
    abstract fun toolUsageDao(): ToolUsageDao

    companion object {
        @Volatile
        private var INSTANCE: WorkshopDatabase? = null

        fun getDatabase(context: Context): WorkshopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkshopDatabase::class.java,
                    "workshop_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}