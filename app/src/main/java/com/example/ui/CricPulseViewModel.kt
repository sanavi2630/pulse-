package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CricPulseDatabase
import com.example.data.model.BattingStats
import com.example.data.model.CycleLog
import com.example.data.model.CyclePhase
import com.example.data.model.MatchInning
import com.example.data.model.TrainingSession
import com.example.data.repository.CricketRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ShotOutcome(val label: String, val colorHex: Long) {
    MIDDLED("Middled / Sweet Spot", 0xFF15803D),
    DEFENDED("Defended / Leave", 0xFF0284C7),
    EDGED("Edged / In Air", 0xFFF59E0B),
    BEATEN("Beaten / Missed", 0xFFDC2626)
}

class CricPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CricketRepository
    private val vibrator: Vibrator?

    init {
        val db = CricPulseDatabase.getInstance(application)
        repository = CricketRepository(db)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    // --- Format Filter ---
    private val _selectedFormatFilter = MutableStateFlow("All")
    val selectedFormatFilter: StateFlow<String> = _selectedFormatFilter.asStateFlow()

    fun setFormatFilter(format: String) {
        _selectedFormatFilter.value = format
    }

    // --- All Innings ---
    val allInnings: StateFlow<List<MatchInning>> = repository.allInnings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filtered Innings ---
    val filteredInnings: StateFlow<List<MatchInning>> = combine(
        allInnings,
        _selectedFormatFilter
    ) { innings, filter ->
        if (filter == "All") innings else innings.filter { it.matchFormat.equals(filter, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Training Sessions ---
    val allTrainingSessions: StateFlow<List<TrainingSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cycle Logs ---
    val allCycleLogs: StateFlow<List<CycleLog>> = repository.allCycleLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Computed Batting Stats ---
    val battingStats: StateFlow<BattingStats> = combine(
        filteredInnings,
        allTrainingSessions
    ) { innings, sessions ->
        BattingStats.compute(innings, sessions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BattingStats())

    // --- Cycle Calculations ---
    val averageCycleLength = MutableStateFlow(28)
    val averagePeriodLength = MutableStateFlow(5)

    val currentCycleDay: StateFlow<Int> = allCycleLogs.combine(averageCycleLength) { logs, cycleLen ->
        calculateCurrentCycleDay(logs, cycleLen)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8)

    val currentCyclePhase: StateFlow<CyclePhase> = currentCycleDay.combine(averageCycleLength) { day, cycleLen ->
        CyclePhase.fromDayOfCycle(day, cycleLen)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CyclePhase.FOLLICULAR)

    val daysUntilNextPeriod: StateFlow<Int> = currentCycleDay.combine(averageCycleLength) { day, cycleLen ->
        val remaining = cycleLen - day
        if (remaining >= 0) remaining else (cycleLen - (day % cycleLen))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20)

    val todayIso: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todayLog: StateFlow<CycleLog?> = allCycleLogs.combine(MutableStateFlow(todayIso)) { logs, today ->
        logs.firstOrNull { it.dateIso == today }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // --- LIVE NET TRAINING SESSION STATE ---
    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _isSessionPaused = MutableStateFlow(false)
    val isSessionPaused: StateFlow<Boolean> = _isSessionPaused.asStateFlow()

    private val _sessionTimerSeconds = MutableStateFlow(0)
    val sessionTimerSeconds: StateFlow<Int> = _sessionTimerSeconds.asStateFlow()

    private val _sessionBallsCount = MutableStateFlow(0)
    val sessionBallsCount: StateFlow<Int> = _sessionBallsCount.asStateFlow()

    private val _sessionMiddled = MutableStateFlow(0)
    val sessionMiddled: StateFlow<Int> = _sessionMiddled.asStateFlow()

    private val _sessionEdged = MutableStateFlow(0)
    val sessionEdged: StateFlow<Int> = _sessionEdged.asStateFlow()

    private val _sessionBeaten = MutableStateFlow(0)
    val sessionBeaten: StateFlow<Int> = _sessionBeaten.asStateFlow()

    private val _sessionDefended = MutableStateFlow(0)
    val sessionDefended: StateFlow<Int> = _sessionDefended.asStateFlow()

    private val _shotTypeCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val shotTypeCounts: StateFlow<Map<String, Int>> = _shotTypeCounts.asStateFlow()

    private val _trainingPitchSurface = MutableStateFlow("Turf / Grass")
    val trainingPitchSurface: StateFlow<String> = _trainingPitchSurface.asStateFlow()

    private val _trainingBowlingType = MutableStateFlow("Sidearm Throwdown")
    val trainingBowlingType: StateFlow<String> = _trainingBowlingType.asStateFlow()

    private val _trainingBallType = MutableStateFlow("White Ball (Leather)")
    val trainingBallType: StateFlow<String> = _trainingBallType.asStateFlow()

    private val _trainingFocusArea = MutableStateFlow("Power & Middle Strike")
    val trainingFocusArea: StateFlow<String> = _trainingFocusArea.asStateFlow()

    private var timerJob: Job? = null

    fun startLiveSession(
        pitch: String,
        bowling: String,
        ball: String,
        focus: String
    ) {
        _trainingPitchSurface.value = pitch
        _trainingBowlingType.value = bowling
        _trainingBallType.value = ball
        _trainingFocusArea.value = focus

        _isSessionActive.value = true
        _isSessionPaused.value = false
        _sessionTimerSeconds.value = 0
        _sessionBallsCount.value = 0
        _sessionMiddled.value = 0
        _sessionEdged.value = 0
        _sessionBeaten.value = 0
        _sessionDefended.value = 0
        _shotTypeCounts.value = emptyMap()

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isSessionActive.value && !_isSessionPaused.value) {
                delay(1000L)
                _sessionTimerSeconds.value += 1
            }
        }
    }

    fun pauseLiveSession() {
        _isSessionPaused.value = true
        timerJob?.cancel()
    }

    fun resumeLiveSession() {
        _isSessionPaused.value = false
        startTimer()
    }

    fun logLiveBall(outcome: ShotOutcome, shotName: String) {
        if (!_isSessionActive.value) return

        _sessionBallsCount.value += 1
        when (outcome) {
            ShotOutcome.MIDDLED -> {
                _sessionMiddled.value += 1
                triggerHaptic(HapticStrength.STRONG)
            }
            ShotOutcome.EDGED -> {
                _sessionEdged.value += 1
                triggerHaptic(HapticStrength.LIGHT)
            }
            ShotOutcome.BEATEN -> {
                _sessionBeaten.value += 1
                triggerHaptic(HapticStrength.DOUBLE)
            }
            ShotOutcome.DEFENDED -> {
                _sessionDefended.value += 1
                triggerHaptic(HapticStrength.LIGHT)
            }
        }

        if (shotName.isNotBlank()) {
            val map = _shotTypeCounts.value.toMutableMap()
            map[shotName] = (map[shotName] ?: 0) + 1
            _shotTypeCounts.value = map
        }
    }

    fun finishAndSaveLiveSession(rpeIntensity: Int, notes: String) {
        timerJob?.cancel()
        val duration = _sessionTimerSeconds.value
        val totalBalls = _sessionBallsCount.value

        if (totalBalls > 0) {
            val jsonObject = JSONObject()
            _shotTypeCounts.value.forEach { (shot, count) ->
                jsonObject.put(shot, count)
            }

            val session = TrainingSession(
                dateMillis = System.currentTimeMillis(),
                durationSeconds = duration,
                pitchSurface = _trainingPitchSurface.value,
                bowlingType = _trainingBowlingType.value,
                ballType = _trainingBallType.value,
                focusArea = _trainingFocusArea.value,
                totalBallsFaced = totalBalls,
                middledCount = _sessionMiddled.value,
                edgedCount = _sessionEdged.value,
                missedBeatenCount = _sessionBeaten.value,
                defendedLeftCount = _sessionDefended.value,
                shotsBreakdownJson = jsonObject.toString(),
                rpeIntensity = rpeIntensity,
                cyclePhaseAtTime = currentCyclePhase.value.name,
                notes = notes
            )

            viewModelScope.launch {
                repository.insertSession(session)
            }
        }

        _isSessionActive.value = false
        _isSessionPaused.value = false
    }

    fun discardLiveSession() {
        timerJob?.cancel()
        _isSessionActive.value = false
        _isSessionPaused.value = false
    }

    // --- Match Innings CRUD ---
    fun addInning(inning: MatchInning) {
        viewModelScope.launch {
            repository.insertInning(inning)
        }
    }

    fun deleteInning(inning: MatchInning) {
        viewModelScope.launch {
            repository.deleteInning(inning)
        }
    }

    fun deleteSession(session: TrainingSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    // --- Cycle Logging ---
    fun saveCycleLog(
        isPeriod: Boolean,
        flow: String,
        energy: Int,
        cramps: Int,
        soreness: Int,
        mood: String,
        notes: String
    ) {
        val phase = currentCyclePhase.value
        val readiness = CycleLog.calculateReadiness(energy, cramps, soreness, phase)

        val log = CycleLog(
            dateIso = todayIso,
            dateMillis = System.currentTimeMillis(),
            isPeriodDay = isPeriod,
            flowIntensity = flow,
            energyLevel = energy,
            crampPainLevel = cramps,
            muscleSoreness = soreness,
            mood = mood,
            cyclePhase = phase.name,
            matchReadinessScore = readiness,
            notes = notes
        )

        viewModelScope.launch {
            repository.insertCycleLog(log)
        }
    }

    private fun calculateCurrentCycleDay(logs: List<CycleLog>, cycleLength: Int): Int {
        val periodLogs = logs.filter { it.isPeriodDay }.sortedByDescending { it.dateIso }
        if (periodLogs.isEmpty()) return 8 // Default fallback day in follicular

        val mostRecentPeriod = periodLogs.first()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val periodDate = isoFormat.parse(mostRecentPeriod.dateIso)
            val todayDate = isoFormat.parse(todayIso)
            if (periodDate != null && todayDate != null) {
                val diffDays = ((todayDate.time - periodDate.time) / (1000L * 60 * 60 * 24)).toInt()
                val day = (diffDays % cycleLength) + 1
                if (day in 1..cycleLength) day else 8
            } else 8
        } catch (_: Exception) {
            8
        }
    }

    private enum class HapticStrength { LIGHT, STRONG, DOUBLE }

    private fun triggerHaptic(strength: HapticStrength) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (strength) {
                    HapticStrength.LIGHT -> vibrator?.vibrate(
                        VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                    HapticStrength.STRONG -> vibrator?.vibrate(
                        VibrationEffect.createOneShot(80, 255)
                    )
                    HapticStrength.DOUBLE -> {
                        val pattern = longArrayOf(0, 40, 60, 40)
                        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
                    }
                }
            }
        } catch (_: Exception) {
            // Graceful fallback if vibrator is not present
        }
    }
}
