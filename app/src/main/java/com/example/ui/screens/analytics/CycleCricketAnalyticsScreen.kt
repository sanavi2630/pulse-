package com.example.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CyclePhase
import com.example.data.model.PhaseCricketStats
import com.example.ui.CricPulseViewModel
import com.example.ui.components.CyclePhaseBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.StatBar
import com.example.ui.theme.CricketPitchGreen
import com.example.ui.theme.MilestoneGold
import java.util.Locale

@Composable
fun CycleCricketAnalyticsScreen(
    viewModel: CricPulseViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.battingStats.collectAsStateWithLifecycle()
    val currentPhase by viewModel.currentCyclePhase.collectAsStateWithLifecycle()
    val allInnings by viewModel.allInnings.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Hero Insights Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analytics_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cycle & Cricket Synergy",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Cross-referencing your match scores and training session ball telemetry against menstrual cycle hormonal phases.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Historical Batting Trends and Run Accumulation Canvas Chart
        item {
            Spacer(modifier = Modifier.height(16.dp))
            com.example.ui.components.BattingTrendDashboardComponent(
                innings = allInnings,
                title = "Historical Batting Trends & Run Trajectory"
            )
        }

        // Cycle Phase Comparison Grid
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Batting Performance by Cycle Phase",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(CyclePhase.entries) { phase ->
            val phaseStat = stats.phaseStats[phase]
            PhasePerformanceCard(
                phase = phase,
                stats = phaseStat,
                isCurrentPhase = phase == currentPhase,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        // Bowling Type Breakdown (Pace vs Spin)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Runs Scored vs Delivery Styles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val totalRuns = stats.totalRuns.coerceAtLeast(1)
                    stats.bowlingTypeRuns.entries.sortedByDescending { it.value }.forEach { (type, runs) ->
                        val pct = (runs.toFloat() / totalRuns) * 100f
                        StatBar(
                            label = type,
                            percentage = pct,
                            displayValue = "$runs runs (${String.format(Locale.getDefault(), "%.0f", pct)}%)",
                            barColor = if (type.contains("Pace", ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Dismissal Mode Analysis
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Dismissal Modes Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val totalDismissals = stats.dismissalCounts.values.sum().coerceAtLeast(1)
                    stats.dismissalCounts.entries.sortedByDescending { it.value }.forEach { (mode, count) ->
                        val pct = (count.toFloat() / totalDismissals) * 100f
                        StatBar(
                            label = mode,
                            percentage = pct,
                            displayValue = "$count times (${String.format(Locale.getDefault(), "%.0f", pct)}%)",
                            barColor = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PhasePerformanceCard(
    phase: CyclePhase,
    stats: PhaseCricketStats?,
    isCurrentPhase: Boolean,
    modifier: Modifier = Modifier
) {
    val phaseColor = phase.color

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPhase) phaseColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentPhase) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CyclePhaseBadge(phase = phase)
                    if (isCurrentPhase) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = phaseColor
                        ) {
                            Text(
                                text = "CURRENT",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${stats?.inningsCount ?: 0} Innings",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats grid for this phase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn(
                    title = "Batting Avg",
                    value = String.format(Locale.getDefault(), "%.1f", stats?.average ?: 0.0),
                    color = phaseColor
                )
                StatColumn(
                    title = "Strike Rate",
                    value = String.format(Locale.getDefault(), "%.1f", stats?.strikeRate ?: 0.0),
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatColumn(
                    title = "Training Middle %",
                    value = "${String.format(Locale.getDefault(), "%.0f", stats?.trainingMiddlePercentage ?: 0.0)}%",
                    color = CricketPitchGreen
                )
                StatColumn(
                    title = "Total Runs",
                    value = "${stats?.runs ?: 0}",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = phase.powerPotential,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = phaseColor
            )
        }
    }
}

@Composable
private fun StatColumn(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
