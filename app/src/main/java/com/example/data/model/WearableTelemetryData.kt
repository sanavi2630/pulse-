package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class HeartRateZone(
    val zoneNumber: Int,
    val zoneTitle: String,
    val bpmRange: String,
    val minBpm: Int,
    val maxBpm: Int,
    val colorHex: Long,
    val cricketImpact: String
) {
    ZONE_1(1, "Active Recovery", "90–114 BPM", 90, 114, 0xFF0284C7, "Stance alignment, throwdowns & technical adjustments."),
    ZONE_2(2, "Aerobic Base", "115–133 BPM", 115, 133, 0xFF15803D, "Sustained long-innings concentration & strike rotation."),
    ZONE_3(3, "Tempo Strike", "134–152 BPM", 134, 152, 0xFFEAB308, "Running quick singles, intense spin footwork drills."),
    ZONE_4(4, "Anaerobic Threshold", "153–171 BPM", 153, 171, 0xFFF97316, "Facing high pace, back-to-back boundary running."),
    ZONE_5(5, "Peak Power", "172–190+ BPM", 172, 195, 0xFFDC2626, "Max effort power-hitting intervals & sprint running.");

    val color: Color
        get() = Color(colorHex)

    companion object {
        fun fromBpm(bpm: Int): HeartRateZone {
            return when {
                bpm < 115 -> ZONE_1
                bpm < 134 -> ZONE_2
                bpm < 153 -> ZONE_3
                bpm < 172 -> ZONE_4
                else -> ZONE_5
            }
        }
    }
}

/**
 * Data model for wearable fitness device telemetry metrics.
 */
data class WearableTelemetryData(
    val currentBpm: Int = 142,
    val restingBpm: Int = 54,
    val maxBpm: Int = 192,
    val heartRateVariabilityMs: Int = 68, // rMSSD HRV in milliseconds
    val heartRateRecovery1Min: Int = 34, // BPM drop in 60s
    val caloriesBurned: Int = 380,
    val strainScore: Double = 12.4, // e.g. Whoop-style daily strain 0-21
    val deviceName: String = "CricPulse Sensor Pro (BLE)",
    val batteryPercentage: Int = 86,
    val isConnected: Boolean = true,
    val isSimulatedMode: Boolean = true
) {
    val currentZone: HeartRateZone
        get() = HeartRateZone.fromBpm(currentBpm)

    val recoveryQuality: String
        get() = when {
            heartRateRecovery1Min >= 35 -> "Elite Aerobic Recovery"
            heartRateRecovery1Min >= 25 -> "Strong Aerobic Recovery"
            heartRateRecovery1Min >= 15 -> "Moderate Recovery"
            else -> "Fatigued / Delayed Recovery"
        }

    val hrvReadinessTag: String
        get() = when {
            heartRateVariabilityMs >= 65 -> "High Parasympathetic Readiness"
            heartRateVariabilityMs >= 45 -> "Normal Physiological Baseline"
            else -> "Sympathetic Dominance / Elevated Fatigue"
        }
}
