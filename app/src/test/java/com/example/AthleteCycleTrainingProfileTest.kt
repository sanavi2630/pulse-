package com.example

import com.example.data.model.AthleteCycleTrainingProfile
import com.example.data.model.CyclePhase
import com.example.data.model.TrainingIntensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AthleteCycleTrainingProfileTest {

    @Test
    fun testMenstrualPhaseTrainingIntensity() {
        val profile = AthleteCycleTrainingProfile.forDay(day = 2, cycleLength = 28)
        assertEquals(CyclePhase.MENSTRUAL, profile.phase)
        assertEquals(TrainingIntensity.ACTIVE_RECOVERY, profile.intensity)
        assertEquals(3, profile.intensity.minRpe)
        assertEquals(5, profile.intensity.maxRpe)
        assertTrue(profile.recommendedDrills.any { it.contains("mobility", ignoreCase = true) || it.contains("alignment", ignoreCase = true) })
        assertTrue(profile.drillsToLimit.any { it.contains("sprint", ignoreCase = true) || it.contains("beep", ignoreCase = true) })
    }

    @Test
    fun testFollicularPhaseTrainingIntensity() {
        val profile = AthleteCycleTrainingProfile.forDay(day = 9, cycleLength = 28)
        assertEquals(CyclePhase.FOLLICULAR, profile.phase)
        assertEquals(TrainingIntensity.HIGH_INTENSITY_POWER, profile.intensity)
        assertEquals(7, profile.intensity.minRpe)
        assertEquals(9, profile.intensity.maxRpe)
        assertTrue(profile.recommendedDrills.any { it.contains("power", ignoreCase = true) || it.contains("explosive", ignoreCase = true) })
    }

    @Test
    fun testOvulatoryPhaseTrainingIntensity() {
        val profile = AthleteCycleTrainingProfile.forDay(day = 15, cycleLength = 28)
        assertEquals(CyclePhase.OVULATORY, profile.phase)
        assertEquals(TrainingIntensity.PEAK_NEUROMUSCULAR, profile.intensity)
        assertEquals(8, profile.intensity.minRpe)
        assertEquals(10, profile.intensity.maxRpe)
        assertTrue(profile.injuryCautionProtocol.contains("ACL", ignoreCase = true) || profile.injuryCautionProtocol.contains("laxity", ignoreCase = true))
    }

    @Test
    fun testLutealPhaseTrainingIntensity() {
        val profile = AthleteCycleTrainingProfile.forDay(day = 22, cycleLength = 28)
        assertEquals(CyclePhase.LUTEAL, profile.phase)
        assertEquals(TrainingIntensity.MODERATE_ENDURANCE, profile.intensity)
        assertEquals(5, profile.intensity.minRpe)
        assertEquals(7, profile.intensity.maxRpe)
        assertTrue(profile.thermoregulationAndHydration.contains("sodium", ignoreCase = true) || profile.thermoregulationAndHydration.contains("cooling", ignoreCase = true))
    }

    @Test
    fun testCycleWrapAround() {
        // Day 30 in a 28 day cycle should wrap to Day 2 (Menstrual)
        val profile = AthleteCycleTrainingProfile.forDay(day = 30, cycleLength = 28)
        assertEquals(2, profile.cycleDay)
        assertEquals(CyclePhase.MENSTRUAL, profile.phase)
    }
}
