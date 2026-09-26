package com.example.ui.screens.cycle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CycleLog
import com.example.data.model.CyclePhase
import com.example.ui.CricPulseViewModel
import com.example.ui.components.CyclePhaseBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.StatBar
import com.example.ui.theme.CricketPitchGreen
import com.example.ui.theme.CycleRose
import com.example.ui.theme.MilestoneGold

@Composable
fun CycleTrackerScreen(
    viewModel: CricPulseViewModel,
    modifier: Modifier = Modifier
) {
    val currentDay by viewModel.currentCycleDay.collectAsStateWithLifecycle()
    val currentPhase by viewModel.currentCyclePhase.collectAsStateWithLifecycle()
    val daysToNextPeriod by viewModel.daysUntilNextPeriod.collectAsStateWithLifecycle()
    val todayLog by viewModel.todayLog.collectAsStateWithLifecycle()
    val cycleLogs by viewModel.allCycleLogs.collectAsStateWithLifecycle()

    var showLogDialog by remember { mutableStateOf(false) }
    var selectedPhaseTab by remember { mutableIntStateOf(currentPhase.ordinal) }

    val readinessScore = todayLog?.matchReadinessScore ?: CycleLog.calculateReadiness(4, 0, 1, currentPhase)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Main Cycle Status Dial Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cycle_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    currentPhase.color.copy(alpha = 0.18f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(currentPhase.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.WaterDrop,
                                        contentDescription = null,
                                        tint = currentPhase.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CYCLE DAY $currentDay / 28",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = currentPhase.color
                                    )
                                    Text(
                                        text = currentPhase.displayName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "$daysToNextPeriod days to period",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Cycle Day Progress Visualizer (28 segments)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            for (i in 1..28) {
                                val segPhase = CyclePhase.fromDayOfCycle(i)
                                val isPassedOrCurrent = i <= currentDay
                                val isCurrent = i == currentDay

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(if (isCurrent) 12.dp else 6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (isCurrent) segPhase.color
                                            else if (isPassedOrCurrent) segPhase.color.copy(alpha = 0.7f)
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = currentPhase.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Match Day Readiness Score Strip
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("match_readiness_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Bolt,
                                contentDescription = null,
                                tint = if (readinessScore >= 75) CricketPitchGreen else MilestoneGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MATCH READINESS INDEX",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (readinessScore >= 80) "Optimal for High Batting Intensity"
                            else if (readinessScore >= 60) "Good Condition • Monitor Hydration"
                            else "Focus on Technical Drills & Recovery",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$readinessScore/100",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = if (readinessScore >= 75) CricketPitchGreen else MilestoneGold
                        )
                        Text(
                            text = if (todayLog != null) "Logged Today" else "Estimated",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Button to Log Today's Symptoms
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { showLogDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("log_today_symptoms_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (todayLog != null) "Edit Today's Wellness Log" else "Log Today's Athletic Symptoms",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Dedicated Cycle Training Intensity Component
        item {
            Spacer(modifier = Modifier.height(16.dp))
            com.example.ui.components.CycleIntensityTrackerComponent(
                currentCycleDay = currentDay,
                cycleLength = 28,
                allowDayScrubbing = true
            )
        }

        // ATHLETIC BLUEPRINT FOR CURRENT CRICKET TRAINING
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Cricket Performance Blueprint",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Physiological adaptations for female batters during ${currentPhase.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Guidance Cards
            CricketGuidanceCard(
                icon = Icons.Filled.FitnessCenter,
                title = "Batting Focus & Power Capacity",
                content = currentPhase.cricketFocus,
                extra = currentPhase.powerPotential,
                accentColor = currentPhase.color
            )

            Spacer(modifier = Modifier.height(10.dp))

            CricketGuidanceCard(
                icon = Icons.Filled.Shield,
                title = "Injury Risk & Warm-Up Protocol",
                content = currentPhase.injuryPrecaution,
                extra = null,
                accentColor = CycleRose
            )

            Spacer(modifier = Modifier.height(10.dp))

            CricketGuidanceCard(
                icon = Icons.Filled.LocalDining,
                title = "Hydration & Nutritional Fueling",
                content = currentPhase.nutritionTip,
                extra = null,
                accentColor = MilestoneGold
            )
        }

        // 4-Phase Educational Explorer for Athletes
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Cycle Phase Science for Cricketers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            TabRow(
                selectedTabIndex = selectedPhaseTab,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                CyclePhase.entries.forEachIndexed { idx, phase ->
                    Tab(
                        selected = selectedPhaseTab == idx,
                        onClick = { selectedPhaseTab = idx },
                        text = {
                            Text(
                                text = phase.name.take(4),
                                fontWeight = if (selectedPhaseTab == idx) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            val viewingPhase = CyclePhase.entries[selectedPhaseTab]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CyclePhaseBadge(phase = viewingPhase)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewingPhase.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = viewingPhase.physiologyInsight,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Match strategy: ${viewingPhase.cricketFocus}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Recent 7 Days Wellness Diary
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Recent Daily Logs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(cycleLogs.take(7), key = { it.dateIso }) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = log.dateIso,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (log.isPeriodDay) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CycleRose.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Period (${log.flowIntensity})",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CycleRose,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Energy: ${log.energyLevel}/5 • Cramps: ${log.crampPainLevel}/5 • Soreness: ${log.muscleSoreness}/5",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${log.matchReadinessScore} pts",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    if (showLogDialog) {
        LogSymptomDialog(
            existingLog = todayLog,
            onDismiss = { showLogDialog = false },
            onSave = { isPeriod, flow, energy, cramps, soreness, mood, notes ->
                viewModel.saveCycleLog(isPeriod, flow, energy, cramps, soreness, mood, notes)
                showLogDialog = false
            }
        )
    }
}

@Composable
private fun CricketGuidanceCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String,
    extra: String?,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (extra != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = extra,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
