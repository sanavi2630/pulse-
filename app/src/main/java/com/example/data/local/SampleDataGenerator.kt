package com.example.data.local

import com.example.data.model.CycleLog
import com.example.data.model.CyclePhase
import com.example.data.model.MatchInning
import com.example.data.model.TrainingSession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SampleDataGenerator {

    fun generateSampleInnings(): List<MatchInning> {
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000

        return listOf(
            MatchInning(
                dateMillis = now - (1L * day),
                opponent = "Southern Vipers",
                matchFormat = "T20",
                runs = 68,
                ballsFaced = 44,
                fours = 7,
                sixes = 2,
                isNotOut = true,
                battingPosition = 3,
                dismissalType = "Not Out",
                bowlerFacedMost = "Pace / Seam",
                cyclePhaseAtTime = "FOLLICULAR",
                venue = "County Ground Oval",
                notes = "Felt high bat speed; dispatched bouncers and drove well through covers."
            ),
            MatchInning(
                dateMillis = now - (4L * day),
                opponent = "Northern Diamonds",
                matchFormat = "T20",
                runs = 45,
                ballsFaced = 31,
                fours = 5,
                sixes = 1,
                isNotOut = false,
                battingPosition = 3,
                dismissalType = "Caught",
                bowlerFacedMost = "Spin / Orthodox",
                cyclePhaseAtTime = "FOLLICULAR",
                venue = "Riverside Stadium",
                notes = "Good sweep execution against orthodox spin. Caught on deep boundary trying to loft."
            ),
            MatchInning(
                dateMillis = now - (9L * day),
                opponent = "Central Sparks",
                matchFormat = "ODI",
                runs = 104,
                ballsFaced = 92,
                fours = 11,
                sixes = 3,
                isNotOut = false,
                battingPosition = 2,
                dismissalType = "Bowled",
                bowlerFacedMost = "Pace / Seam",
                cyclePhaseAtTime = "OVULATORY",
                venue = "Edgbaston Arena",
                notes = "Match-winning century! Reflexes were razor sharp against 120km/h seamers."
            ),
            MatchInning(
                dateMillis = now - (15L * day),
                opponent = "Western Storm",
                matchFormat = "ODI",
                runs = 38,
                ballsFaced = 46,
                fours = 4,
                sixes = 0,
                isNotOut = false,
                battingPosition = 3,
                dismissalType = "LBW",
                bowlerFacedMost = "Leg Spin / Wrist",
                cyclePhaseAtTime = "LUTEAL",
                venue = "Taunton Park",
                notes = "Hot afternoon; paced well but misjudged wrong'un pitching on middle."
            ),
            MatchInning(
                dateMillis = now - (21L * day),
                opponent = "South East Stars",
                matchFormat = "T20",
                runs = 26,
                ballsFaced = 22,
                fours = 3,
                sixes = 0,
                isNotOut = false,
                battingPosition = 4,
                dismissalType = "Caught",
                bowlerFacedMost = "Pace / Seam",
                cyclePhaseAtTime = "MENSTRUAL",
                venue = "The Oval",
                notes = "Day 2 of period, slight cramp during running. Solid strike rotation."
            ),
            MatchInning(
                dateMillis = now - (27L * day),
                opponent = "The Blaze",
                matchFormat = "Club",
                runs = 82,
                ballsFaced = 58,
                fours = 9,
                sixes = 2,
                isNotOut = true,
                battingPosition = 3,
                dismissalType = "Not Out",
                bowlerFacedMost = "Pace / Seam",
                cyclePhaseAtTime = "FOLLICULAR",
                venue = "Trent Bridge Suburb",
                notes = "Clean timing through midwicket; finished the chase with a boundary."
            ),
            MatchInning(
                dateMillis = now - (35L * day),
                opponent = "Melbourne Stars XI",
                matchFormat = "T20",
                runs = 51,
                ballsFaced = 35,
                fours = 6,
                sixes = 1,
                isNotOut = false,
                battingPosition = 3,
                dismissalType = "Run Out",
                bowlerFacedMost = "Spin / Orthodox",
                cyclePhaseAtTime = "OVULATORY",
                venue = "Junction Oval",
                notes = "Fluent fifty. Direct hit run-out while trying a quick single."
            ),
            MatchInning(
                dateMillis = now - (42L * day),
                opponent = "Sydney Thunder Academy",
                matchFormat = "ODI",
                runs = 72,
                ballsFaced = 78,
                fours = 8,
                sixes = 1,
                isNotOut = false,
                battingPosition = 3,
                dismissalType = "Caught",
                bowlerFacedMost = "Pace / Seam",
                cyclePhaseAtTime = "LUTEAL",
                venue = "Showground",
                notes = "Patient anchor knock under high humidity; hydrated thoroughly."
            )
        )
    }

    fun generateSampleTrainingSessions(): List<TrainingSession> {
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000

        return listOf(
            TrainingSession(
                dateMillis = now - (2 * 3600 * 1000), // 2 hours ago
                durationSeconds = 1860, // 31 mins
                pitchSurface = "Turf / Grass",
                bowlingType = "Sidearm Throwdown",
                ballType = "White Ball (Leather)",
                focusArea = "Cover Drive & Off-Side Flow",
                totalBallsFaced = 78,
                middledCount = 61,
                edgedCount = 9,
                missedBeatenCount = 4,
                defendedLeftCount = 4,
                shotsBreakdownJson = "{\"Cover Drive\":34,\"Straight Drive\":18,\"Punch\":15,\"Cut\":11}",
                rpeIntensity = 7,
                cyclePhaseAtTime = "FOLLICULAR",
                notes = "Crisp contact, transferred weight cleanly into the ball."
            ),
            TrainingSession(
                dateMillis = now - (2L * day),
                durationSeconds = 2400, // 40 mins
                pitchSurface = "Astro Turf",
                bowlingType = "Bowling Machine (115 km/h)",
                ballType = "Machine Dimple Ball",
                focusArea = "Pull Shot & Hook against Short Pitch",
                totalBallsFaced = 90,
                middledCount = 68,
                edgedCount = 12,
                missedBeatenCount = 7,
                defendedLeftCount = 3,
                shotsBreakdownJson = "{\"Pull\":45,\"Hook\":20,\"Duck/Weave\":15,\"Upper Cut\":10}",
                rpeIntensity = 8,
                cyclePhaseAtTime = "FOLLICULAR",
                notes = "Kept roll of wrists down on the pull. Fast hip rotation."
            ),
            TrainingSession(
                dateMillis = now - (6L * day),
                durationSeconds = 1500, // 25 mins
                pitchSurface = "Indoor Net",
                bowlingType = "Spin Bowlers",
                ballType = "White Ball (Leather)",
                focusArea = "Footwork vs Left-Arm Spin",
                totalBallsFaced = 60,
                middledCount = 46,
                edgedCount = 6,
                missedBeatenCount = 4,
                defendedLeftCount = 4,
                shotsBreakdownJson = "{\"Step Out Drive\":20,\"Sweep\":22,\"Paddle\":10,\"Block\":8}",
                rpeIntensity = 6,
                cyclePhaseAtTime = "OVULATORY",
                notes = "Great reading of length from hand; swept safely along the ground."
            ),
            TrainingSession(
                dateMillis = now - (14L * day),
                durationSeconds = 2700, // 45 mins
                pitchSurface = "Turf / Grass",
                bowlingType = "Pace Bowlers",
                ballType = "Red Ball (Leather)",
                focusArea = "Match Scenario: First 10 Overs",
                totalBallsFaced = 102,
                middledCount = 74,
                edgedCount = 14,
                missedBeatenCount = 8,
                defendedLeftCount = 6,
                shotsBreakdownJson = "{\"Defense\":35,\"Cover Drive\":25,\"Cut\":18,\"Flick\":24}",
                rpeIntensity = 8,
                cyclePhaseAtTime = "LUTEAL",
                notes = "Challenging swing early on. Focused on high elbow defense."
            )
        )
    }

    fun generateSampleCycleLogs(): List<CycleLog> {
        val list = mutableListOf<CycleLog>()
        val cal = Calendar.getInstance()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Generate past 28 days
        for (i in 27 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            val iso = isoFormat.format(c.time)
            val millis = c.timeInMillis

            // Day in simulated cycle (1 to 28)
            // Say today is Day 8 (Follicular)
            val dayOfCycle = (8 - i)
            val normalizedCycleDay = if (dayOfCycle <= 0) dayOfCycle + 28 else dayOfCycle
            val phase = CyclePhase.fromDayOfCycle(normalizedCycleDay)

            val isPeriod = normalizedCycleDay in 1..5
            val flow = when (normalizedCycleDay) {
                1 -> "Medium"
                2 -> "Heavy"
                3 -> "Medium"
                4 -> "Light"
                5 -> "Spotting"
                else -> "None"
            }

            val cramps = when (normalizedCycleDay) {
                1 -> 4
                2 -> 3
                3 -> 2
                4 -> 1
                else -> 0
            }

            val energy = when (phase) {
                CyclePhase.FOLLICULAR -> 5
                CyclePhase.OVULATORY -> 5
                CyclePhase.LUTEAL -> 3
                CyclePhase.MENSTRUAL -> 3
            }

            val readiness = CycleLog.calculateReadiness(
                energy = energy,
                cramps = cramps,
                soreness = if (normalizedCycleDay in 1..3) 3 else 1,
                phase = phase
            )

            list.add(
                CycleLog(
                    dateIso = iso,
                    dateMillis = millis,
                    isPeriodDay = isPeriod,
                    flowIntensity = flow,
                    energyLevel = energy,
                    crampPainLevel = cramps,
                    muscleSoreness = if (isPeriod) 2 else 1,
                    mood = if (energy >= 4) "Optimal" else "Focused",
                    cyclePhase = phase.name,
                    matchReadinessScore = readiness,
                    notes = if (isPeriod) "Electrolyte & iron recovery" else "Great hitting tempo"
                )
            )
        }

        return list
    }
}
