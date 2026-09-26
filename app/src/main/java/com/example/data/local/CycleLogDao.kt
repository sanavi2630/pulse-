package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CycleLog
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleLogDao {
    @Query("SELECT * FROM cycle_logs ORDER BY dateIso DESC")
    fun getAllCycleLogs(): Flow<List<CycleLog>>

    @Query("SELECT * FROM cycle_logs WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getLogForDate(dateIso: String): CycleLog?

    @Query("SELECT * FROM cycle_logs WHERE isPeriodDay = 1 ORDER BY dateIso DESC LIMIT 30")
    fun getPeriodDays(): Flow<List<CycleLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CycleLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<CycleLog>)

    @Delete
    suspend fun deleteLog(log: CycleLog)

    @Query("SELECT COUNT(*) FROM cycle_logs")
    suspend fun getCount(): Int
}
