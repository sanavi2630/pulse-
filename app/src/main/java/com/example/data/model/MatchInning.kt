package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "match_innings")
data class MatchInning(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long = System.currentTimeMillis(),
    val opponent: String,
    val matchFormat: String, // "T20", "ODI", "Test", "Club"
    val runs: Int,
    val ballsFaced: Int,
    val fours: Int = 0,
    val sixes: Int = 0,
    val isNotOut: Boolean = false,
    val battingPosition: Int = 3,
    val dismissalType: String = "Not Out", // "Not Out", "Bowled", "Caught", "LBW", "Run Out", "Stumped"
    val bowlerFacedMost: String = "Pace / Seam",
    val cyclePhaseAtTime: String = "FOLLICULAR",
    val venue: String = "Home Ground",
    val notes: String = ""
) {
    val strikeRate: Double
        get() = if (ballsFaced > 0) (runs.toDouble() / ballsFaced) * 100.0 else 0.0

    val boundaryRuns: Int
        get() = (fours * 4) + (sixes * 6)

    val boundaryPercentage: Double
        get() = if (runs > 0) (boundaryRuns.toDouble() / runs) * 100.0 else 0.0

    val formattedDate: String
        get() = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(dateMillis))

    val scoreDisplay: String
        get() = if (isNotOut) "$runs*" else "$runs"
}
