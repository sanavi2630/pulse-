package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery5Bar
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CyclePhase
import com.example.data.model.HeartRateZone
import com.example.data.model.WearableTelemetryData
import com.example.ui.theme.CricketPitchGreen
import com.example.ui.theme.CycleRose
import com.example.ui.theme.MilestoneGold
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Live wearable fitness device telemetry UI component.
 * Displays live simulated heart rate, zone tracking, HRV, and post-over recovery metrics.
 * Includes a mandatory "Simulation Mode" indicator per platform safety guidelines.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WearableHeartRateTelemetryComponent(
    modifier: Modifier = Modifier,
    cyclePhase: CyclePhase = CyclePhase.FOLLICULAR,
    initialBpm: Int = 142
) {
    var simulatedBpm by remember { mutableIntStateOf(initialBpm) }
    var isStreaming by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(false) }

    val recentWavePoints = remember {
        mutableStateListOf(140f, 142f, 145f, 143f, 141f, 144f, 148f, 146f, 142f, 141f, 143f)
    }

    // Heartbeat pulse animation frequency linked to current BPM
    val pulseDurationMs = (60000 / simulatedBpm.coerceIn(50, 200))
    val pulseTransition = rememberInfiniteTransition(label = "heartbeat")
    val heartScale by pulseTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseDurationMs / 2, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )

    // Simulate continuous micro-fluctuations in heart rate
    LaunchedEffect(isStreaming, simulatedBpm) {
        while (isStreaming) {
            delay(1200L)
            val jitter = Random.nextInt(-2, 3)
            val nextVal = (simulatedBpm + jitter).coerceIn(60, 195)
            recentWavePoints.add(nextVal.toFloat())
            if (recentWavePoints.size > 24) {
                recentWavePoints.removeAt(0)
            }
        }
    }

    val telemetry = remember(simulatedBpm) {
        val hrr = when {
            simulatedBpm > 160 -> 38
            simulatedBpm > 140 -> 32
            simulatedBpm > 110 -> 26
            else -> 18
        }
        val hrv = when {
            simulatedBpm > 155 -> 42
            simulatedBpm > 130 -> 58
            else -> 72
        }
        WearableTelemetryData(
            currentBpm = simulatedBpm,
            heartRateRecovery1Min = hrr,
            heartRateVariabilityMs = hrv,
            caloriesBurned = (simulatedBpm * 2.8).toInt(),
            strainScore = String.format(Locale.getDefault(), "%.1f", (simulatedBpm / 11.5).coerceIn(4.0, 18.5)).toDouble()
        )
    }

    val currentZone = telemetry.currentZone

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wearable_telemetry_component"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Device & Mandatory Simulation Notice Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(currentZone.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BluetoothConnected,
                            contentDescription = null,
                            tint = currentZone.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = telemetry.deviceName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Battery: ${telemetry.batteryPercentage}% • BLE Synced",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Mandatory Platform Simulation Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MilestoneGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MilestoneGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MilestoneGold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SIMULATION MODE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MilestoneGold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Live BPM Display Billboard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                currentZone.color.copy(alpha = 0.15f),
                                currentZone.color.copy(alpha = 0.04f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Live Heartbeat",
                            tint = currentZone.color,
                            modifier = Modifier
                                .size(40.dp)
                                .scale(heartScale)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${telemetry.currentBpm}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "BPM",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = currentZone.color,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            Text(
                                text = "Zone ${currentZone.zoneNumber}: ${currentZone.zoneTitle} (${currentZone.bpmRange})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = currentZone.color
                    ) {
                        Text(
                            text = "ZONE ${currentZone.zoneNumber}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live ECG / BPM Waveform Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                val waveColor = currentZone.color
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (recentWavePoints.size > 1) {
                        val path = Path()
                        val stepX = size.width / (recentWavePoints.size - 1)
                        val minVal = 60f
                        val maxVal = 195f

                        recentWavePoints.forEachIndexed { i, bpmVal ->
                            val x = i * stepX
                            val normalizedY = ((bpmVal - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
                            val y = size.height - (normalizedY * size.height)

                            if (i == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = waveColor,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5-Zone Target Spectrum Bar
            Text(
                text = "Target Heart Rate Zones",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                HeartRateZone.entries.forEach { zone ->
                    val isCurrent = zone == currentZone
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (isCurrent) 10.dp else 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isCurrent) zone.color else zone.color.copy(alpha = 0.3f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recovery & Fatigue Analytics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Heart Rate Recovery Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.RestartAlt,
                                contentDescription = null,
                                tint = CricketPitchGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1-MIN HR RECOVERY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "-${telemetry.heartRateRecovery1Min} BPM",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = CricketPitchGreen
                        )
                        Text(
                            text = telemetry.recoveryQuality,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Heart Rate Variability (HRV) Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HRV READINESS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${telemetry.heartRateVariabilityMs} ms",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = telemetry.hrvReadinessTag,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cycle-Synchronized Cardiovascular Note
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = cyclePhase.color.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = cyclePhase.color,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Cardiovascular Synergy (${cyclePhase.displayName})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = cyclePhase.color
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val advice = when (cyclePhase) {
                            CyclePhase.FOLLICULAR -> "High glycogen storage enables faster lactate clearing in Zone 4. Great time for intensive short-ball and boundary sprint sets."
                            CyclePhase.OVULATORY -> "Maximal neuromuscular reactivity. Heart rate recovers quickly, but avoid sharp pivot turns without proper ankle and knee stabilization."
                            CyclePhase.LUTEAL -> "Cardiovascular drift: resting and exercise HR runs ~4–6 BPM higher due to progesterone core temperature elevation. Extend rest between overs."
                            CyclePhase.MENSTRUAL -> "Baseline low-temperature phase. Keep workouts in Zones 1–3 for technique, balance, and anti-inflammatory recovery."
                        }
                        Text(
                            text = advice,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Interactive Wearable Simulation Scenario Triggers
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { showControls = !showControls },
                    modifier = Modifier.testTag("toggle_telemetry_simulator_button")
                ) {
                    Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showControls) "Hide Simulation Scenarios" else "Simulate Live Cricket Match Scenarios",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = showControls) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "1-Tap Cricket Scenarios (Simulate Wearable Output):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val scenarios = listOf(
                        "Batting Stance / Ready" to 98,
                        "Defending Spin" to 125,
                        "Facing 125km/h Seam" to 152,
                        "Running Quick 2s" to 174,
                        "Post-Over Recovery" to 110
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scenarios.forEach { (label, bpm) ->
                            FilterChip(
                                selected = simulatedBpm == bpm,
                                onClick = { simulatedBpm = bpm },
                                label = { Text("$label ($bpm BPM)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentZone.color,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Manual BPM Slider
                    Text(
                        text = "Fine Tune Heart Rate: $simulatedBpm BPM",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = simulatedBpm.toFloat(),
                        onValueChange = { simulatedBpm = it.roundToInt() },
                        valueRange = 60f..195f,
                        modifier = Modifier.testTag("slider_simulated_bpm")
                    )
                }
            }
        }
    }
}
