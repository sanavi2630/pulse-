package com.example

import com.example.data.model.BattingRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BattingRecordTest {

    @Test
    fun testStandardBattingAverageCalculation() {
        // 500 runs in 12 innings with 2 not outs = 10 dismissals => Average = 50.0
        val record = BattingRecord(
            runsScored = 500,
            inningsPlayed = 12,
            notOuts = 2
        )
        assertEquals(10, record.dismissals)
        assertEquals(50.0, record.battingAverage, 0.001)
        assertEquals("50.00", record.formattedAverage)
        assertEquals("Legendary (50+)", record.performanceTier)
    }

    @Test
    fun testUndefeatedBatsmanAverage() {
        // Never dismissed (not outs == innings played)
        val record = BattingRecord(
            runsScored = 135,
            inningsPlayed = 3,
            notOuts = 3
        )
        assertEquals(0, record.dismissals)
        assertEquals(135.0, record.battingAverage, 0.001)
    }

    @Test
    fun testZeroInnings() {
        val record = BattingRecord()
        assertEquals(0.0, record.battingAverage, 0.001)
        assertEquals("0.00", record.formattedAverage)
    }

    @Test
    fun testSimulateNextInningDismissed() {
        val current = BattingRecord(runsScored = 400, inningsPlayed = 10, notOuts = 2) // 8 dismissals => avg 50.0
        // Score 50 and get dismissed -> 450 runs in 9 dismissals => avg 50.0
        val simulated = current.simulateNextInning(projectedRuns = 50, isNotOut = false)
        assertEquals(450, simulated.runsScored)
        assertEquals(11, simulated.inningsPlayed)
        assertEquals(2, simulated.notOuts)
        assertEquals(9, simulated.dismissals)
        assertEquals(50.0, simulated.battingAverage, 0.001)
    }

    @Test
    fun testSimulateNextInningNotOut() {
        val current = BattingRecord(runsScored = 400, inningsPlayed = 10, notOuts = 2) // 8 dismissals => avg 50.0
        // Score 50 and stay not out -> 450 runs in 8 dismissals => avg 56.25
        val simulated = current.simulateNextInning(projectedRuns = 50, isNotOut = true)
        assertEquals(450, simulated.runsScored)
        assertEquals(11, simulated.inningsPlayed)
        assertEquals(3, simulated.notOuts)
        assertEquals(8, simulated.dismissals)
        assertEquals(56.25, simulated.battingAverage, 0.001)
    }

    @Test
    fun testRunsNeededForTargetAverage() {
        // Current: 360 runs, 10 dismissals (avg 36.0).
        // Wants target average 40.0 in next dismissed inning (total dismissals will be 11).
        // 11 * 40.0 = 440 runs. Needed runs = 440 - 360 = 80 runs.
        val current = BattingRecord(runsScored = 360, inningsPlayed = 10, notOuts = 0)
        val runsNeeded = current.runsNeededForTargetAverage(40.0)
        assertEquals(80, runsNeeded)
    }
}
