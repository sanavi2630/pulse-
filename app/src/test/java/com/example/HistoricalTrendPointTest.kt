package com.example

import com.example.data.model.MatchInning
import com.example.ui.components.HistoricalTrendPoint
import org.junit.Assert.assertEquals
import org.junit.Test

class HistoricalTrendPointTest {

    @Test
    fun testCumulativeAverageAndRunsProgression() {
        val innings = listOf(
            MatchInning(id = 1, runs = 50, ballsFaced = 40, isNotOut = false, opponent = "Team A", matchFormat = "T20"),
            MatchInning(id = 2, runs = 40, ballsFaced = 30, isNotOut = true, opponent = "Team B", matchFormat = "T20"),
            MatchInning(id = 3, runs = 60, ballsFaced = 50, isNotOut = false, opponent = "Team C", matchFormat = "ODI")
        )

        var cumRuns = 0
        var cumDismissals = 0

        val points = innings.mapIndexed { idx, match ->
            cumRuns += match.runs
            if (!match.isNotOut) cumDismissals += 1
            val avg = if (cumDismissals > 0) cumRuns.toDouble() / cumDismissals else cumRuns.toDouble()
            HistoricalTrendPoint(
                inningNumber = idx + 1,
                dateMillis = match.dateMillis,
                formattedDate = match.formattedDate,
                opponent = match.opponent,
                matchFormat = match.matchFormat,
                runs = match.runs,
                isNotOut = match.isNotOut,
                cumulativeRuns = cumRuns,
                cumulativeAverage = avg,
                cyclePhase = com.example.data.model.CyclePhase.FOLLICULAR
            )
        }

        // Point 1: 50 runs, 1 dismissal => avg = 50.0
        assertEquals(50, points[0].cumulativeRuns)
        assertEquals(50.0, points[0].cumulativeAverage, 0.001)

        // Point 2: +40 not out => 90 runs, 1 dismissal => avg = 90.0
        assertEquals(90, points[1].cumulativeRuns)
        assertEquals(90.0, points[1].cumulativeAverage, 0.001)

        // Point 3: +60 out => 150 runs, 2 dismissals => avg = 75.0
        assertEquals(150, points[2].cumulativeRuns)
        assertEquals(75.0, points[2].cumulativeAverage, 0.001)
    }
}
