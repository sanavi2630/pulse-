package com.example.data.model

data class BattingStats(
    val totalInnings: Int = 0,
    val notOuts: Int = 0,
    val totalRuns: Int = 0,
    val totalBallsFaced: Int = 0,
    val highScore: Int = 0,
    val highScoreNotOut: Boolean = false,
    val battingAverage: Double = 0.0,
    val strikeRate: Double = 0.0,
    val centuries: Int = 0,
    val halfCenturies: Int = 0,
    val thirties: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val boundaryPercentage: Double = 0.0,
    val dismissalCounts: Map<String, Int> = emptyMap(),
    val bowlingTypeRuns: Map<String, Int> = emptyMap(),
    val phaseStats: Map<CyclePhase, PhaseCricketStats> = emptyMap()
) {
    val highScoreDisplay: String
        get() = if (highScoreNotOut) "$highScore*" else "$highScore"

    companion object {
        fun compute(innings: List<MatchInning>, sessions: List<TrainingSession>): BattingStats {
            if (innings.isEmpty()) {
                return BattingStats()
            }

            val totalInnings = innings.size
            val notOuts = innings.count { it.isNotOut }
            val totalRuns = innings.sumOf { it.runs }
            val totalBalls = innings.sumOf { it.ballsFaced }
            val fours = innings.sumOf { it.fours }
            val sixes = innings.sumOf { it.sixes }

            // High Score
            var bestScore = 0
            var bestScoreNotOut = false
            for (inning in innings) {
                if (inning.runs > bestScore || (inning.runs == bestScore && inning.isNotOut && !bestScoreNotOut)) {
                    bestScore = inning.runs
                    bestScoreNotOut = inning.isNotOut
                }
            }

            // Batting Average = totalRuns / (totalInnings - notOuts)
            val dismissals = totalInnings - notOuts
            val avg = if (dismissals > 0) totalRuns.toDouble() / dismissals else totalRuns.toDouble()

            // Strike Rate = (totalRuns / totalBalls) * 100
            val sr = if (totalBalls > 0) (totalRuns.toDouble() / totalBalls) * 100.0 else 0.0

            val centuries = innings.count { it.runs >= 100 }
            val halfCenturies = innings.count { it.runs in 50..99 }
            val thirties = innings.count { it.runs in 30..49 }

            val boundaryRuns = (fours * 4) + (sixes * 6)
            val boundaryPct = if (totalRuns > 0) (boundaryRuns.toDouble() / totalRuns) * 100.0 else 0.0

            // Dismissals
            val dismissalMap = mutableMapOf<String, Int>()
            innings.filter { !it.isNotOut }.forEach {
                dismissalMap[it.dismissalType] = (dismissalMap[it.dismissalType] ?: 0) + 1
            }

            // Bowling types
            val bowlMap = mutableMapOf<String, Int>()
            innings.forEach {
                bowlMap[it.bowlerFacedMost] = (bowlMap[it.bowlerFacedMost] ?: 0) + it.runs
            }

            // Phase stats
            val phaseMap = mutableMapOf<CyclePhase, PhaseCricketStats>()
            for (phase in CyclePhase.entries) {
                val phaseInnings = innings.filter { it.cyclePhaseAtTime.equals(phase.name, ignoreCase = true) }
                val phaseSessions = sessions.filter { it.cyclePhaseAtTime.equals(phase.name, ignoreCase = true) }

                val pRuns = phaseInnings.sumOf { it.runs }
                val pBalls = phaseInnings.sumOf { it.ballsFaced }
                val pNotOuts = phaseInnings.count { it.isNotOut }
                val pDismissals = phaseInnings.size - pNotOuts
                val pAvg = if (pDismissals > 0) pRuns.toDouble() / pDismissals else pRuns.toDouble()
                val pSr = if (pBalls > 0) (pRuns.toDouble() / pBalls) * 100.0 else 0.0

                val totalBallsTrained = phaseSessions.sumOf { it.totalBallsFaced }
                val middledBalls = phaseSessions.sumOf { it.middledCount }
                val middlePct = if (totalBallsTrained > 0) (middledBalls.toDouble() / totalBallsTrained) * 100.0 else 0.0

                phaseMap[phase] = PhaseCricketStats(
                    phase = phase,
                    inningsCount = phaseInnings.size,
                    runs = pRuns,
                    average = pAvg,
                    strikeRate = pSr,
                    trainingMiddlePercentage = middlePct,
                    totalTrainingBalls = totalBallsTrained
                )
            }

            return BattingStats(
                totalInnings = totalInnings,
                notOuts = notOuts,
                totalRuns = totalRuns,
                totalBallsFaced = totalBalls,
                highScore = bestScore,
                highScoreNotOut = bestScoreNotOut,
                battingAverage = avg,
                strikeRate = sr,
                centuries = centuries,
                halfCenturies = halfCenturies,
                thirties = thirties,
                fours = fours,
                sixes = sixes,
                boundaryPercentage = boundaryPct,
                dismissalCounts = dismissalMap,
                bowlingTypeRuns = bowlMap,
                phaseStats = phaseMap
            )
        }
    }
}

data class PhaseCricketStats(
    val phase: CyclePhase,
    val inningsCount: Int,
    val runs: Int,
    val average: Double,
    val strikeRate: Double,
    val trainingMiddlePercentage: Double,
    val totalTrainingBalls: Int
)
