package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MatchInning
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchInningDao {
    @Query("SELECT * FROM match_innings ORDER BY dateMillis DESC")
    fun getAllInnings(): Flow<List<MatchInning>>

    @Query("SELECT * FROM match_innings WHERE matchFormat = :format ORDER BY dateMillis DESC")
    fun getInningsByFormat(format: String): Flow<List<MatchInning>>

    @Query("SELECT * FROM match_innings WHERE id = :id")
    suspend fun getInningById(id: Long): MatchInning?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInning(inning: MatchInning): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(innings: List<MatchInning>)

    @Update
    suspend fun updateInning(inning: MatchInning)

    @Delete
    suspend fun deleteInning(inning: MatchInning)

    @Query("DELETE FROM match_innings WHERE id = :id")
    suspend fun deleteInningById(id: Long)

    @Query("SELECT COUNT(*) FROM match_innings")
    suspend fun getCount(): Int
}
