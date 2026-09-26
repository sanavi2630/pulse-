package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BattingRecord
import com.example.ui.theme.CricketPitchGreen
import com.example.ui.theme.MilestoneGold
import java.util.Locale

/**
 * A dedicated Jetpack Compose UI component for calculating and tracking cricket batting averages.
 *
 * Supports:
 * 1. Viewing live cricket average computed from [record]
 * 2. Steppers / custom inputs to tweak runs, innings, and not-outs
 * 3. Interactive "What-If" next match simulator
 * 4. Formula breakdown: Runs / (Innings - NotOuts)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BattingAverageCalculatorComponent(
    record: BattingRecord,
    modifier: Modifier = Modifier,
    title: String = "Batting Average & Metrics",
    enableSimulation: Boolean = true,
    onRecordUpdated: ((BattingRecord) -> Unit)? = null
) {
    var currentRuns by remember(record) { mutableIntStateOf(record.runsScored) }
    var currentInnings by remember(record) { mutableIntStateOf(record.inningsPlayed) }
    var currentNotOuts by remember(record) { mutableIntStateOf(record.notOuts) }

    val activeRecord = remember(currentRuns, currentInnings, currentNotOuts) {
        BattingRecord(
            runsScored = currentRuns,
            inningsPlayed = currentInnings,
            notOuts = currentNotOuts.coerceAtMost(currentInnings)
        )
    }

    // Simulator State
    var showSimulator by remember { mutableStateOf(false) }
    var projectedRuns by remember { mutableIntStateOf(45) }
    var projectedNotOut by remember { mutableStateOf(false) }

    val simulatedRecord = remember(activeRecord, projectedRuns, projectedNotOut) {
        activeRecord.simulateNextInning(projectedRuns, projectedNotOut)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("batting_average_calculator_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
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
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Runs • Innings • Not Outs Formula",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        activeRecord.battingAverage >= 40.0 -> MilestoneGold.copy(alpha = 0.15f)
                        activeRecord.battingAverage >= 25.0 -> CricketPitchGreen.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = activeRecord.performanceTier,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            activeRecord.battingAverage >= 40.0 -> MilestoneGold
                            activeRecord.battingAverage >= 25.0 -> CricketPitchGreen
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Average Billboard
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
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
                    Column {
                        Text(
                            text = "BATTING AVERAGE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeRecord.formattedAverage,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${activeRecord.runsScored} runs in ${activeRecord.dismissals} dismissals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", activeRecord.runsPerInning),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "Runs/Inning",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Column Stepper Controls (Runs Scored, Innings Played, Not Outs)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricStepperCard(
                    title = "Runs Scored",
                    value = currentRuns,
                    step = 10,
                    modifier = Modifier.weight(1f),
                    onValueChange = {
                        currentRuns = it.coerceAtLeast(0)
                        onRecordUpdated?.invoke(activeRecord)
                    },
                    testTag = "stepper_runs"
                )

                MetricStepperCard(
                    title = "Innings",
                    value = currentInnings,
                    step = 1,
                    modifier = Modifier.weight(1f),
                    onValueChange = {
                        val newInnings = it.coerceAtLeast(0)
                        currentInnings = newInnings
                        if (currentNotOuts > newInnings) currentNotOuts = newInnings
                        onRecordUpdated?.invoke(activeRecord)
                    },
                    testTag = "stepper_innings"
                )

                MetricStepperCard(
                    title = "Not Outs",
                    value = currentNotOuts,
                    step = 1,
                    modifier = Modifier.weight(1f),
                    onValueChange = {
                        currentNotOuts = it.coerceIn(0, currentInnings)
                        onRecordUpdated?.invoke(activeRecord)
                    },
                    testTag = "stepper_not_outs"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mathematical Cricket Formula Breakdown
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Formula: Runs ÷ (Innings - NotOuts)",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${activeRecord.runsScored} ÷ (${activeRecord.inningsPlayed} - ${activeRecord.notOuts}) = ${activeRecord.formattedAverage}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Interactive What-If Inning Projection Simulator
            if (enableSimulation) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { showSimulator = !showSimulator },
                        modifier = Modifier.testTag("toggle_simulator_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showSimulator) "Hide Next Match Simulator" else "Simulate Next Match Inning",
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (showSimulator) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = showSimulator) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Next Inning Impact Simulator",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Runs Chips
                        val presets = listOf(20, 35, 50, 75, 100)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presets.forEach { runs ->
                                FilterChip(
                                    selected = projectedRuns == runs,
                                    onClick = { projectedRuns = runs },
                                    label = { Text("+$runs runs") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Will be Not Out?", style = MaterialTheme.typography.bodySmall)
                            Switch(
                                checked = projectedNotOut,
                                onCheckedChange = { projectedNotOut = it },
                                modifier = Modifier.testTag("simulator_not_out_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Projection Result Card
                        val delta = simulatedRecord.battingAverage - activeRecord.battingAverage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CricketPitchGreen.copy(alpha = 0.12f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "If you score $projectedRuns${if (projectedNotOut) "*" else ""} next match:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Projected Average: ${simulatedRecord.formattedAverage}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = CricketPitchGreen
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (delta >= 0) CricketPitchGreen else MaterialTheme.colorScheme.error
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%+.2f", delta),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricStepperCard(
    title: String,
    value: Int,
    step: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit,
    testTag: String = ""
) {
    Surface(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$value",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { onValueChange(value - step) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Decrease $title",
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { onValueChange(value + step) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase $title",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
