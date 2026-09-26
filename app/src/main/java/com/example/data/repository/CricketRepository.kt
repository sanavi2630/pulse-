package com.example.data.repository

import com.example.data.local.CricPulseDatabase
import com.example.data.local.SampleDataGenerator
import com.example.data.model.CycleLog
import com.example.data.model.MatchInning
import com.example.data.model.TrainingSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CricketRepository(private val database: CricPulseDatabase) {

    private val inningDao = database.matchInningDao()
    private val sessionDao = database.trainingSessionDao()
    private val cycleDao = database.cycleLogDao()

    val allInnings: Flow<List<MatchInning>> = inningDao.getAllInnings()
    val allSessions: Flow<List<TrainingSession>> = sessionDao.getAllSessions()
    val allCycleLogs: Flow<List<CycleLog>> = cycleDao.getAllCycleLogs()
    val periodDays: Flow<List<CycleLog>> = cycleDao.getPeriodDays()

    suspend fun insertInning(inning: MatchInning): Long = withContext(Dispatchers.IO) {
        inningDao.insertInning(inning)
    }

    suspend fun updateInning(inning: MatchInning) = withContext(Dispatchers.IO) {
        inningDao.updateInning(inning)
    }

    suspend fun deleteInning(inning: MatchInning) = withContext(Dispatchers.IO) {
        inningDao.deleteInning(inning)
    }

    suspend fun deleteInningById(id: Long) = withContext(Dispatchers.IO) {
        inningDao.deleteInningById(id)
    }

    suspend fun insertSession(session: TrainingSession): Long = withContext(Dispatchers.IO) {
        sessionDao.insertSession(session)
    }

    suspend fun deleteSession(session: TrainingSession) = withContext(Dispatchers.IO) {
        sessionDao.deleteSession(session)
    }

    suspend fun insertCycleLog(log: CycleLog): Long = withContext(Dispatchers.IO) {
        cycleDao.insertLog(log)
    }

    suspend fun getCycleLogForDate(dateIso: String): CycleLog? = withContext(Dispatchers.IO) {
        cycleDao.getLogForDate(dateIso)
    }

    suspend fun seedSampleDataIfEmpty() = withContext(Dispatchers.IO) {
        if (inningDao.getCount() == 0) {
            inningDao.insertAll(SampleDataGenerator.generateSampleInnings())
        }
        if (sessionDao.getCount() == 0) {
            sessionDao.insertAll(SampleDataGenerator.generateSampleTrainingSessions())
        }
        if (cycleDao.getCount() == 0) {
            cycleDao.insertAll(SampleDataGenerator.generateSampleCycleLogs())
        }
    }
}
