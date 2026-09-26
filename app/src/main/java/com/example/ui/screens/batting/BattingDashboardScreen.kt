package com.example.ui.screens.batting

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CyclePhase
import com.example.data.model.MatchInning
import com.example.ui.CricPulseViewModel
import com.example.ui.components.CyclePhaseBadge
import com.example.ui.components.MetricCard
import com.example.ui.theme.MilestoneGold
import java.util.Locale

@Composable
fun BattingDashboardScreen(
    viewModel: CricPulseViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.battingStats.collectAsStateWithLifecycle()
    val innings by viewModel.filteredInnings.collectAsStateWithLifecycle()
    val selectedFormat by viewModel.selectedFormatFilter.collectAsStateWithLifecycle()
    val currentPhase by viewModel.currentCyclePhase.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    val formats = listOf("All", "T20", "ODI", "Test", "Club")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Hero Banner Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cricketer_hero_1790399586741),
                        contentDescription = "Cricketer action",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Batting Command Center",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MilestoneGold.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "PRO STATS",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Averages, boundary strike index, and match highlights",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Key Batting Metrics Grid
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Batting Avg",
                            value = String.format(Locale.getDefault(), "%.1f", stats.battingAverage),
                            subtitle = "${stats.totalRuns} runs in ${stats.totalInnings} inns",
                            icon = Icons.Filled.SportsCricket,
                            accentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_batting_avg"
                        )
                        MetricCard(
                            title = "Strike Rate",
                            value = String.format(Locale.getDefault(), "%.1f", stats.strikeRate),
                            subtitle = "${stats.totalBallsFaced} balls faced",
                            icon = Icons.Filled.ElectricBolt,
                            accentColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_strike_rate"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "High Score",
                            value = stats.highScoreDisplay,
                            subtitle = if (stats.highScoreNotOut) "Unbeaten Knock" else "Best Inning",
                            icon = Icons.Filled.Star,
                            accentColor = MilestoneGold,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_high_score"
                        )
                        MetricCard(
                            title = "Boundaries",
                            value = "${stats.fours * 4 + stats.sixes * 6}r",
                            subtitle = "${stats.fours}x4 • ${stats.sixes}x6 (${String.format(Locale.getDefault(), "%.0f", stats.boundaryPercentage)}%)",
                            icon = Icons.Filled.EmojiEvents,
                            accentColor = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_boundaries"
                        )
                    }

                    // Milestone Highlights Strip
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MilestonePill("100s", stats.centuries)
                            MilestonePill("50s", stats.halfCenturies)
                            MilestonePill("30s+", stats.thirties)
                            MilestonePill("Not Outs", stats.notOuts)
                        }
                    }
                }
            }

            // Interactive Batting Average Calculator & Simulator Component
            item {
                val currentBattingRecord = remember(stats) {
                    com.example.data.model.BattingRecord(
                        runsScored = stats.totalRuns,
                        inningsPlayed = stats.totalInnings,
                        notOuts = stats.notOuts,
                        ballsFaced = stats.totalBallsFaced
                    )
                }
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    com.example.ui.components.BattingAverageCalculatorComponent(
                        record = currentBattingRecord,
                        title = if (selectedFormat == "All") "Career Batting Average Tracker" else "$selectedFormat Batting Average Tracker"
                    )
                }
            }

            // Format Filter Pills
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Match Formats",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        formats.forEach { fmt ->
                            FilterChip(
                                selected = selectedFormat == fmt,
                                onClick = { viewModel.setFormatFilter(fmt) },
                                label = { Text(fmt) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Recent Match Innings (${innings.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Inning List
            if (innings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No match innings recorded for $selectedFormat.\nTap '+' to log your first knock!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(innings, key = { it.id }) { inning ->
                    MatchInningCard(
                        inning = inning,
                        onDelete = { viewModel.deleteInning(inning) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_match_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Match Inning")
        }
    }

    if (showAddDialog) {
        AddMatchDialog(
            initialCyclePhase = currentPhase,
            onDismiss = { showAddDialog = false },
            onSave = { newInning ->
                viewModel.addInning(newInning)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MilestonePill(title: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MatchInningCard(
    inning: MatchInning,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("match_inning_card_${inning.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = inning.matchFormat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "vs ${inning.opponent}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${inning.formattedDate} • ${inning.venue}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Big Score Display
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = inning.scoreDisplay,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = if (inning.runs >= 50) MilestoneGold else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "(${inning.ballsFaced} balls)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_match_${inning.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Inning",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub details: SR, Boundaries, Dismissal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SR: ${String.format(Locale.getDefault(), "%.1f", inning.strikeRate)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${inning.fours}x4, ${inning.sixes}x6",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (inning.isNotOut) "Not Out" else inning.dismissalType,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (inning.isNotOut) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    )
                }

                // Cycle Phase Tag
                val phase = CyclePhase.fromString(inning.cyclePhaseAtTime)
                CyclePhaseBadge(phase = phase)
            }

            if (inning.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "“${inning.notes}”",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
