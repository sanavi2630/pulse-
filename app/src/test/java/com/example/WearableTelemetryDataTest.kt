package com.example

import com.example.data.model.HeartRateZone
import com.example.data.model.WearableTelemetryData
import org.junit.Assert.assertEquals
import org.junit.Test

class WearableTelemetryDataTest {

    @Test
    fun testHeartRateZoneMapping() {
        assertEquals(HeartRateZone.ZONE_1, HeartRateZone.fromBpm(98))
        assertEquals(HeartRateZone.ZONE_2, HeartRateZone.fromBpm(122))
        assertEquals(HeartRateZone.ZONE_3, HeartRateZone.fromBpm(145))
        assertEquals(HeartRateZone.ZONE_4, HeartRateZone.fromBpm(162))
        assertEquals(HeartRateZone.ZONE_5, HeartRateZone.fromBpm(185))
    }

    @Test
    fun testRecoveryQualityMetrics() {
        val eliteRecovery = WearableTelemetryData(heartRateRecovery1Min = 36)
        assertEquals("Elite Aerobic Recovery", eliteRecovery.recoveryQuality)

        val strongRecovery = WearableTelemetryData(heartRateRecovery1Min = 28)
        assertEquals("Strong Aerobic Recovery", strongRecovery.recoveryQuality)

        val fatiguedRecovery = WearableTelemetryData(heartRateRecovery1Min = 12)
        assertEquals("Fatigued / Delayed Recovery", fatiguedRecovery.recoveryQuality)
    }

    @Test
    fun testHrvReadinessEvaluation() {
        val highHrv = WearableTelemetryData(heartRateVariabilityMs = 70)
        assertEquals("High Parasympathetic Readiness", highHrv.hrvReadinessTag)

        val lowHrv = WearableTelemetryData(heartRateVariabilityMs = 38)
        assertEquals("Sympathetic Dominance / Elevated Fatigue", lowHrv.hrvReadinessTag)
    }
}
