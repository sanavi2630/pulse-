package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Categorizes training intensity recommendations for female athletes based on cycle phases.
 */
enum class TrainingIntensity(
    val label: String,
    val rpeRangeDisplay: String,
    val minRpe: Int,
    val maxRpe: Int,
    val colorHex: Long,
    val summary: String
) {
    ACTIVE_RECOVERY(
        label = "Low - Active Recovery & Technique",
        rpeRangeDisplay = "RPE 3–5 / 10",
        minRpe = 3,
        maxRpe = 5,
        colorHex = 0xFF0284C7, // Sky Blue
        summary = "Low hormone baseline. Focus on skill precision, light technical throwdowns, mobility, and joint deload."
    ),
    HIGH_INTENSITY_POWER(
        label = "High - Maximum Power & Strength",
        rpeRangeDisplay = "RPE 7–9 / 10",
        minRpe = 7,
        maxRpe = 9,
        colorHex = 0xFF8E24AA, // Purple / Power
        summary = "Estrogen is rising. High carbohydrate utilization and optimal glycogen storage. Prime window for explosive boundary hitting, sprint intervals, and heavy gym PRs."
    ),
    PEAK_NEUROMUSCULAR(
        label = "Peak - Maximum Reflex & Timing",
        rpeRangeDisplay = "RPE 8–10 / 10",
        minRpe = 8,
        maxRpe = 10,
        colorHex = 0xFFFB8C00, // Amber / Flame
        summary = "Estrogen peaks alongside LH surge. Highest neuromuscular reaction time and bat swing velocity. Caution: increased joint and ligament laxity."
    ),
    MODERATE_ENDURANCE(
        label = "Moderate - Aerobic Pacing & Tactical",
        rpeRangeDisplay = "RPE 5–7 / 10",
        minRpe = 5,
        maxRpe = 7,
        colorHex = 0xFF15803D, // Deep Emerald
        summary = "High progesterone shifts metabolism towards fat oxidation. Resting core body temperature rises ~0.5°C. Prioritize steady rotational batting, mental discipline, and cooling strategies."
    );

    val color: Color
        get() = Color(colorHex)
}

/**
 * Data model representing the athletic training recommendations and physiological profile
 * for a specific day in the menstrual cycle.
 */
data class AthleteCycleTrainingProfile(
    val cycleDay: Int,
    val cycleLength: Int = 28,
    val phase: CyclePhase,
    val intensity: TrainingIntensity,
    val heartRateZoneTarget: String,
    val recommendedDrills: List<String>,
    val drillsToLimit: List<String>,
    val injuryCautionProtocol: String,
    val thermoregulationAndHydration: String,
    val nutritionalFueling: String
) {
    val phaseProgressPercentage: Float
        get() = (cycleDay.toFloat() / cycleLength.toFloat()).coerceIn(0f, 1f)

    companion object {
        fun forDay(day: Int, cycleLength: Int = 28): AthleteCycleTrainingProfile {
            val normalizedDay = ((day - 1) % cycleLength) + 1
            val phase = CyclePhase.fromDayOfCycle(normalizedDay, cycleLength)

            return when (phase) {
                CyclePhase.MENSTRUAL -> AthleteCycleTrainingProfile(
                    cycleDay = normalizedDay,
                    cycleLength = cycleLength,
                    phase = phase,
                    intensity = TrainingIntensity.ACTIVE_RECOVERY,
                    heartRateZoneTarget = "Zone 1–2 (60–70% Max HR)",
                    recommendedDrills = listOf(
                        "Batting grip & stance alignment",
                        "Stationary batting tee drives",
                        "Soft throwdowns for hand-eye timing",
                        "Gentle spine & hip mobility flows",
                        "Tactical match video analysis"
                    ),
                    drillsToLimit = listOf(
                        "Maximal effort sprint running between wickets",
                        "Heavy spinal loaded back squats",
                        "Exhaustive aerobic beep tests"
                    ),
                    injuryCautionProtocol = "Increased lower back cramping and pelvic tightness. Perform 10 mins of cat-cow and glute activation before holding crouched wicketkeeping or slip stances.",
                    thermoregulationAndHydration = "Baseline body temperature is low. Warm herbal teas, ginger, and room-temperature electrolyte fluids.",
                    nutritionalFueling = "High iron-rich foods (spinach, lentils, red meat), vitamin C for iron absorption, magnesium (300mg) for muscle cramp alleviation."
                )

                CyclePhase.FOLLICULAR -> AthleteCycleTrainingProfile(
                    cycleDay = normalizedDay,
                    cycleLength = cycleLength,
                    phase = phase,
                    intensity = TrainingIntensity.HIGH_INTENSITY_POWER,
                    heartRateZoneTarget = "Zone 4–5 (85–95% Max HR)",
                    recommendedDrills = listOf(
                        "Explosive boundary power-hitting drills",
                        "Bowling machine facing 120km/h+ speeds",
                        "High-intensity interval running between wickets",
                        "Heavy resistance training (trap bar deadlifts, power cleans)",
                        "Fast twitch pull & cut shot practice"
                    ),
                    drillsToLimit = listOf(
                        "Under-training: Do not waste this high anabolic window on low-effort sessions"
                    ),
                    injuryCautionProtocol = "Recovery speed is at its fastest. Muscle protein synthesis is elevated. Maintain good landing mechanics on quick jump catches.",
                    thermoregulationAndHydration = "High heat dissipation capacity. Standard hydration with 500ml water per 45 mins of batting.",
                    nutritionalFueling = "Optimal carbohydrate utilization. Fuel with complex oats, sweet potato, and 25-30g protein post-session to maximize muscle hypertrophy."
                )

                CyclePhase.OVULATORY -> AthleteCycleTrainingProfile(
                    cycleDay = normalizedDay,
                    cycleLength = cycleLength,
                    phase = phase,
                    intensity = TrainingIntensity.PEAK_NEUROMUSCULAR,
                    heartRateZoneTarget = "Zone 4–5 Neuromuscular Peak",
                    recommendedDrills = listOf(
                        "High-velocity reaction slip fielding",
                        "Facing express pace & reverse swing",
                        "Rapid footwork dances against spinners",
                        "Max bat swing speed telemetry sessions",
                        "Sharp reflex reaction balls & slip cradles"
                    ),
                    drillsToLimit = listOf(
                        "Unwarmed sharp pivot running",
                        "Sudden deceleration drills without prior dynamic warmup"
                    ),
                    injuryCautionProtocol = "CRITICAL: Estrogen surge induces ligament laxity, particularly in the anterior cruciate ligament (ACL) and ankles. Mandatory 15-min neuromuscular knee and ankle stabilizer activation before training.",
                    thermoregulationAndHydration = "Core temperature begins upward shift. Drink cold electrolyte water with sodium to maintain vascular volume.",
                    nutritionalFueling = "High antioxidant meals (blueberries, dark leafy greens) to combat acute inflammatory markers. Light pre-match meals."
                )

                CyclePhase.LUTEAL -> AthleteCycleTrainingProfile(
                    cycleDay = normalizedDay,
                    cycleLength = cycleLength,
                    phase = phase,
                    intensity = TrainingIntensity.MODERATE_ENDURANCE,
                    heartRateZoneTarget = "Zone 3 Steady State (70–80% Max HR)",
                    recommendedDrills = listOf(
                        "Long innings simulation (batting 60+ balls)",
                        "Tactical strike rotation & gap finding",
                        "Defensive front & back foot block stability",
                        "Moderate tempo bowling spells",
                        "Zone 2 aerobic base cardio & fielding positioning"
                    ),
                    drillsToLimit = listOf(
                        "Prolonged heat exposure without cooling breaks",
                        "Heavy max-effort PR lifts without adequate rest intervals",
                        "Extreme glycogen-depleting fasted sessions"
                    ),
                    injuryCautionProtocol = "Elevated core body temperature (+0.5°C) and higher resting heart rate. Rate of Perceived Exertion (RPE) feels higher for the same workload. Take 2-minute shaded cooling breaks between batting sets.",
                    thermoregulationAndHydration = "High sodium pre-loading (+500mg sodium in 500ml fluid) 1 hour prior to batting. Use ice towels on neck between overs during warm matches.",
                    nutritionalFueling = "Higher metabolic resting rate (+100-200 kcal/day). Increase healthy fats and protein to support progesterone synthesis. Snack on almonds and Greek yogurt."
                )
            }
        }
    }
}
