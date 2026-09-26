package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CycleLog
import com.example.data.model.MatchInning
import com.example.data.model.TrainingSession

@Database(
    entities = [
        MatchInning::class,
        TrainingSession::class,
        CycleLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CricPulseDatabase : RoomDatabase() {
    abstract fun matchInningDao(): MatchInningDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun cycleLogDao(): CycleLogDao

    companion object {
        @Volatile
        private var INSTANCE: CricPulseDatabase? = null

        fun getInstance(context: Context): CricPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CricPulseDatabase::class.java,
                    "cricpulse_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
