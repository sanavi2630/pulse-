package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class CyclePhase(
    val displayName: String,
    val description: String,
    val cricketFocus: String,
    val physiologyInsight: String,
    val powerPotential: String,
    val injuryPrecaution: String,
    val nutritionTip: String,
    val colorHex: Long
) {
    MENSTRUAL(
        displayName = "Menstrual Phase",
        description = "Days 1–5 • Low estrogen & progesterone",
        cricketFocus = "Technical batting drills, light throwdowns, precision over brute power.",
        physiologyInsight = "Baseline hormone levels. Anti-inflammatory recovery and comfort are key.",
        powerPotential = "Moderate - Focus on timing & balance",
        injuryPrecaution = "Warm up lumbar spine and hamstrings thoroughly before crouched wicketkeeping or slips fielding.",
        nutritionTip = "Iron-rich foods, magnesium for cramp easing, warm hydration.",
        colorHex = 0xFFE53935
    ),
    FOLLICULAR(
        displayName = "Follicular Phase",
        description = "Days 6–13 • Rising estrogen",
        cricketFocus = "High-intensity net sessions, aggressive power hitting, boundary clearing drills.",
        physiologyInsight = "High insulin sensitivity & superior carbohydrate utilization for quick sprint recoveries.",
        powerPotential = "Peak Power & High Anaerobic Capacity",
        injuryPrecaution = "Muscle recovery is fast; ideal time to increase bat swing velocity and heavy resistance gym work.",
        nutritionTip = "Complex carbs to fuel high-tempo batting, lean protein for muscle repair.",
        colorHex = 0xFF8E24AA
    ),
    OVULATORY(
        displayName = "Ovulatory Phase",
        description = "Days 14–16 • Estrogen peak & LH surge",
        cricketFocus = "Peak reaction time, facing high-velocity pace bowling, sharp reflex slip fielding.",
        physiologyInsight = "Neuromuscular coordination is at its sharpest; maximum strength & cognitive alertness.",
        powerPotential = "Maximum Timing & Rapid Reflexes",
        injuryPrecaution = "High estrogen increases ACL & ankle ligament laxity. Mandatory neuromuscular warm-ups before sprint turning.",
        nutritionTip = "Electrolyte hydration, antioxidant-rich berries, light clean meals before matches.",
        colorHex = 0xFFFB8C00
    ),
    LUTEAL(
        displayName = "Luteal Phase",
        description = "Days 17–28 • Elevated progesterone",
        cricketFocus = "Steady pacing, building long patient innings, rotational strike play, tactical discipline.",
        physiologyInsight = "Resting core temperature is up by ~0.5°C; elevated heart rate & higher rate of perceived exertion.",
        powerPotential = "Endurance & Mental Fortitude",
        injuryPrecaution = "Thermoregulation strain: prioritize aggressive cooling (ice towels, shade) between overs.",
        nutritionTip = "High sodium and extra fluids to combat water redistribution; healthy fats and protein snack breaks.",
        colorHex = 0xFF1E88E5
    );

    val color: Color
        get() = Color(colorHex)

    companion object {
        fun fromDayOfCycle(day: Int, cycleLength: Int = 28): CyclePhase {
            val normalizedDay = ((day - 1) % cycleLength) + 1
            return when {
                normalizedDay in 1..5 -> MENSTRUAL
                normalizedDay in 6..13 -> FOLLICULAR
                normalizedDay in 14..16 -> OVULATORY
                else -> LUTEAL
            }
        }

        fun fromString(name: String?): CyclePhase {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: FOLLICULAR
        }
    }
}
