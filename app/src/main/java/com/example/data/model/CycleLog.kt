package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cycle_logs",
    indices = [Index(value = ["dateIso"], unique = true)]
)
data class CycleLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateIso: String, // "YYYY-MM-DD"
    val dateMillis: Long = System.currentTimeMillis(),
    val isPeriodDay: Boolean = false,
    val flowIntensity: String = "None", // "None", "Spotting", "Light", "Medium", "Heavy"
    val energyLevel: Int = 4, // 1 to 5
    val crampPainLevel: Int = 0, // 0 to 5
    val muscleSoreness: Int = 1, // 0 to 5
    val mood: String = "Focused", // "Optimal", "Focused", "Calm", "Tired", "Irritable"
    val cyclePhase: String = "FOLLICULAR",
    val matchReadinessScore: Int = 85, // 1 - 100
    val notes: String = ""
) {
    companion object {
        fun calculateReadiness(
            energy: Int, // 1..5
            cramps: Int, // 0..5
            soreness: Int, // 0..5
            phase: CyclePhase
        ): Int {
            // Base on energy (5 = 50 pts, 1 = 10 pts)
            var score = energy * 10
            // Deduct for cramps (each point -4)
            score -= (cramps * 4)
            // Deduct for soreness (each point -3)
            score -= (soreness * 3)

            // Adjust by phase athletic synergy
            score += when (phase) {
                CyclePhase.FOLLICULAR -> 25 // peak strength/glycogen
                CyclePhase.OVULATORY -> 20 // peak reflexes & timing
                CyclePhase.LUTEAL -> 12 // thermoregulation strain
                CyclePhase.MENSTRUAL -> 8 // inflammatory base
            }

            return score.coerceIn(15, 100)
        }
    }
}
