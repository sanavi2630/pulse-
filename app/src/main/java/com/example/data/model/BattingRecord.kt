package com.example.data.model

import java.util.Locale

/**
 * Data model for tracking and calculating cricket batting averages and career figures.
 *
 * In cricket:
 * - Dismissals = Innings Played - Times Not Out
 * - Batting Average = Total Runs / Dismissals (or Total Runs if never dismissed)
 * - Strike Rate = (Total Runs / Balls Faced) * 100
 */
data class BattingRecord(
    val runsScored: Int = 0,
    val inningsPlayed: Int = 0,
    val notOuts: Int = 0,
    val ballsFaced: Int = 0
) {
    init {
        require(runsScored >= 0) { "Runs scored cannot be negative" }
        require(inningsPlayed >= 0) { "Innings played cannot be negative" }
        require(notOuts in 0..inningsPlayed) { "Not outs ($notOuts) cannot exceed innings played ($inningsPlayed)" }
        require(ballsFaced >= 0) { "Balls faced cannot be negative" }
    }

    /**
     * Total number of times the batsman has been dismissed.
     */
    val dismissals: Int
        get() = (inningsPlayed - notOuts).coerceAtLeast(0)

    /**
     * Official cricket batting average: Runs / Dismissals.
     * If dismissals is 0 and runs > 0, returns the total runs (undefeated).
     * If 0 innings have been played, returns 0.0.
     */
    val battingAverage: Double
        get() = when {
            inningsPlayed == 0 -> 0.0
            dismissals == 0 -> runsScored.toDouble()
            else -> runsScored.toDouble() / dismissals.toDouble()
        }

    /**
     * Formatted string of the batting average (e.g., "52.33" or "0.00").
     */
    val formattedAverage: String
        get() = String.format(Locale.getDefault(), "%.2f", battingAverage)

    /**
     * Runs scored per innings regardless of dismissals.
     */
    val runsPerInning: Double
        get() = if (inningsPlayed > 0) runsScored.toDouble() / inningsPlayed else 0.0

    /**
     * Strike rate (runs per 100 balls faced).
     */
    val strikeRate: Double
        get() = if (ballsFaced > 0) (runsScored.toDouble() / ballsFaced) * 100.0 else 0.0

    /**
     * Formatted strike rate string.
     */
    val formattedStrikeRate: String
        get() = String.format(Locale.getDefault(), "%.1f", strikeRate)

    /**
     * Evaluates performance category based on batting average.
     */
    val performanceTier: String
        get() = when {
            inningsPlayed == 0 -> "Unranked"
            battingAverage >= 50.0 -> "Legendary (50+)"
            battingAverage >= 40.0 -> "Elite (40–49)"
            battingAverage >= 30.0 -> "Solid Starter (30–39)"
            battingAverage >= 20.0 -> "Anchor / Rotator (20–29)"
            else -> "Developing (<20)"
        }

    /**
     * Calculates the projected batting average if the batter scores [projectedRuns]
     * in the next inning, either staying not out or being dismissed.
     */
    fun simulateNextInning(projectedRuns: Int, isNotOut: Boolean, projectedBalls: Int = 0): BattingRecord {
        return copy(
            runsScored = runsScored + projectedRuns,
            inningsPlayed = inningsPlayed + 1,
            notOuts = if (isNotOut) notOuts + 1 else notOuts,
            ballsFaced = ballsFaced + projectedBalls
        )
    }

    /**
     * Calculates runs required in the next dismissed inning to reach a target average.
     * Target Average = (Current Runs + Needed Runs) / (Current Dismissals + 1)
     * => Needed Runs = [targetAverage * (dismissals + 1)] - current runs
     */
    fun runsNeededForTargetAverage(targetAverage: Double): Int {
        val nextDismissals = dismissals + 1
        val requiredTotalRuns = (targetAverage * nextDismissals).toInt()
        return (requiredTotalRuns - runsScored).coerceAtLeast(0)
    }

    companion object {
        fun fromInnings(runs: List<Int>, notOutFlags: List<Boolean>, balls: List<Int> = emptyList()): BattingRecord {
            val totalRuns = runs.sum()
            val totalInnings = runs.size
            val totalNotOuts = notOutFlags.count { it }
            val totalBalls = balls.sum()
            return BattingRecord(
                runsScored = totalRuns,
                inningsPlayed = totalInnings,
                notOuts = totalNotOuts,
                ballsFaced = totalBalls
            )
        }
    }
}
