package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "training_sessions")
data class TrainingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val pitchSurface: String = "Turf / Grass",
    val bowlingType: String = "Sidearm Throwdown",
    val ballType: String = "White Ball (Leather)",
    val focusArea: String = "Front Foot Drives",
    val totalBallsFaced: Int = 0,
    val middledCount: Int = 0,
    val edgedCount: Int = 0,
    val missedBeatenCount: Int = 0,
    val defendedLeftCount: Int = 0,
    val shotsBreakdownJson: String = "{}", // e.g. {"Cover Drive":12, "Pull":8}
    val rpeIntensity: Int = 7, // 1 - 10
    val cyclePhaseAtTime: String = "FOLLICULAR",
    val notes: String = ""
) {
    val middledPercentage: Double
        get() = if (totalBallsFaced > 0) (middledCount.toDouble() / totalBallsFaced) * 100.0 else 0.0

    val controlPercentage: Double
        get() = if (totalBallsFaced > 0) {
            ((middledCount + defendedLeftCount).toDouble() / totalBallsFaced) * 100.0
        } else 0.0

    val formattedDuration: String
        get() {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            return "${minutes}m ${seconds}s"
        }

    val formattedDate: String
        get() = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(dateMillis))
}
