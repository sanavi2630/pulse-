package com.example.ui.screens.training

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CyclePhase
import com.example.data.model.TrainingSession
import com.example.ui.CricPulseViewModel
import com.example.ui.ShotOutcome
import com.example.ui.components.CyclePhaseBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.StatBar
import com.example.ui.theme.CricketPitchGreen
import com.example.ui.theme.MilestoneGold
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveTrainingScreen(
    viewModel: CricPulseViewModel,
    modifier: Modifier = Modifier
) {
    val isSessionActive by viewModel.isSessionActive.collectAsStateWithLifecycle()
    val isSessionPaused by viewModel.isSessionPaused.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.sessionTimerSeconds.collectAsStateWithLifecycle()
    val ballsCount by viewModel.sessionBallsCount.collectAsStateWithLifecycle()
    val middledCount by viewModel.sessionMiddled.collectAsStateWithLifecycle()
    val edgedCount by viewModel.sessionEdged.collectAsStateWithLifecycle()
    val beatenCount by viewModel.sessionBeaten.collectAsStateWithLifecycle()
    val defendedCount by viewModel.sessionDefended.collectAsStateWithLifecycle()

    val pitchSurface by viewModel.trainingPitchSurface.collectAsStateWithLifecycle()
    val bowlingType by viewModel.trainingBowlingType.collectAsStateWithLifecycle()
    val focusArea by viewModel.trainingFocusArea.collectAsStateWithLifecycle()
    val currentPhase by viewModel.currentCyclePhase.collectAsStateWithLifecycle()

    val pastSessions by viewModel.allTrainingSessions.collectAsStateWithLifecycle()

    var showSetupDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var selectedShotType by remember { mutableStateOf("Cover Drive") }

    val commonShots = listOf(
        "Cover Drive", "Straight Drive", "Pull Shot", "Cut Shot",
        "Sweep", "Flick / On Drive", "Lofted Shot", "Front Defense"
    )

    if (isSessionActive) {
        // --- REAL-TIME LIVE NET SESSION HUD ---
        val mins = timerSeconds / 60
        val secs = timerSeconds % 60
        val timeDisplay = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

        val middledPct = if (ballsCount > 0) (middledCount.toDouble() / ballsCount) * 100.0 else 0.0
        val controlPct = if (ballsCount > 0) ((middledCount + defendedCount).toDouble() / ballsCount) * 100.0 else 0.0

        val overs = ballsCount / 6
        val ballsInOver = ballsCount % 6

        val pulseAnim = rememberInfiniteTransition(label = "pulse")
        val alpha by pulseAnim.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Live Status Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isSessionPaused) MilestoneGold else CricketPitchGreen)
                                        .then(if (!isSessionPaused) Modifier.alpha(alpha) else Modifier)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSessionPaused) "SESSION PAUSED" else "LIVE NET IN PROGRESS",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSessionPaused) MilestoneGold else CricketPitchGreen
                                )
                            }
                            CyclePhaseBadge(phase = currentPhase)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Timer & Balls Faced Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = timeDisplay,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Net Duration",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$ballsCount balls",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Over $overs.$ballsInOver",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$focusArea • $bowlingType • $pitchSurface",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Real-Time Contact Gauges
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Real-Time Batting Execution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        StatBar(
                            label = "Middled / Sweet Spot",
                            percentage = middledPct.toFloat(),
                            displayValue = "${String.format(Locale.getDefault(), "%.0f", middledPct)}% ($middledCount)",
                            barColor = CricketPitchGreen
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        StatBar(
                            label = "Control Rate (Middled + Defense)",
                            percentage = controlPct.toFloat(),
                            displayValue = "${String.format(Locale.getDefault(), "%.0f", controlPct)}%",
                            barColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Live Wearable Heart Rate & Recovery Telemetry
            item {
                Spacer(modifier = Modifier.height(14.dp))
                com.example.ui.components.WearableHeartRateTelemetryComponent(
                    cyclePhase = currentPhase,
                    initialBpm = 148
                )
            }

            // Shot Type Selector Chips
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Shot Type (Tap to tag incoming ball)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    commonShots.forEach { shot ->
                        FilterChip(
                            selected = selectedShotType == shot,
                            onClick = { selectedShotType = shot },
                            label = { Text(shot) }
                        )
                    }
                }
            }

            // GLOVE-FRIENDLY LARGE TOUCH BUTTONS FOR INSTANT BALL OUTCOME LOGGING
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Log Ball Outcome (1-Tap Fast)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Big Green Button: MIDDLED
                    Button(
                        onClick = { viewModel.logLiveBall(ShotOutcome.MIDDLED, selectedShotType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("log_ball_middled_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CricketPitchGreen)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.SportsCricket, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SWEET SPOT / MIDDLED (+$middledCount)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Row: DEFENDED / LEAVE and EDGED
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.logLiveBall(ShotOutcome.DEFENDED, selectedShotType) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("log_ball_defended_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                        ) {
                            Text(
                                text = "DEFENDED ($defendedCount)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.logLiveBall(ShotOutcome.EDGED, selectedShotType) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("log_ball_edged_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MilestoneGold)
                        ) {
                            Text(
                                text = "EDGED ($edgedCount)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Red Button: BEATEN / MISSED
                    Button(
                        onClick = { viewModel.logLiveBall(ShotOutcome.BEATEN, selectedShotType) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("log_ball_beaten_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text(
                            text = "BEATEN / MISSED ($beatenCount)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Session Controls: Pause / Finish
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (isSessionPaused) viewModel.resumeLiveSession() else viewModel.pauseLiveSession()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("pause_resume_session_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isSessionPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSessionPaused) "Resume" else "Pause")
                    }

                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("finish_save_session_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finish & Save")
                    }
                }
            }
        }
    } else {
        // --- IDLE / NET TRAINING HOME & HISTORY ---
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Hero Start Net Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Live Batting Net Tracker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Track sweet-spot middles, control percentage, and shot execution in real time with high-contrast, glove-friendly buttons.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showSetupDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("start_live_net_session_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Live Net Session",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Quick Stats summary of training
            item {
                Spacer(modifier = Modifier.height(16.dp))
                val totalBalls = pastSessions.sumOf { it.totalBallsFaced }
                val totalMiddled = pastSessions.sumOf { it.middledCount }
                val avgMiddlePct = if (totalBalls > 0) (totalMiddled.toDouble() / totalBalls) * 100.0 else 0.0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Balls Faced",
                        value = "$totalBalls",
                        subtitle = "${pastSessions.size} net sessions",
                        icon = Icons.Filled.SportsCricket,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Career Middle %",
                        value = "${String.format(Locale.getDefault(), "%.1f", avgMiddlePct)}%",
                        subtitle = "$totalMiddled balls sweet spot",
                        icon = Icons.Filled.CheckCircle,
                        accentColor = CricketPitchGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Wearable Device Live Telemetry & Recovery Tracker
            item {
                Spacer(modifier = Modifier.height(16.dp))
                com.example.ui.components.WearableHeartRateTelemetryComponent(
                    cyclePhase = currentPhase,
                    initialBpm = 96
                )
            }

            // Previous Sessions List
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Training Diary & Net Logs (${pastSessions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (pastSessions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No sessions logged yet. Tap 'Start Live Net Session' above to begin!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(pastSessions, key = { it.id }) { session ->
                    TrainingSessionCard(
                        session = session,
                        onDelete = { viewModel.deleteSession(session) },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }

    if (showSetupDialog) {
        SetupTrainingDialog(
            onDismiss = { showSetupDialog = false },
            onStart = { pitch, bowling, ball, focus ->
                viewModel.startLiveSession(pitch, bowling, ball, focus)
                showSetupDialog = false
            }
        )
    }

    if (showSaveDialog) {
        val middledPct = if (ballsCount > 0) (middledCount.toDouble() / ballsCount) * 100.0 else 0.0
        SaveSessionDialog(
            totalBalls = ballsCount,
            middledPct = middledPct,
            onDismiss = { showSaveDialog = false },
            onConfirm = { rpe, notes ->
                viewModel.finishAndSaveLiveSession(rpe, notes)
                showSaveDialog = false
            }
        )
    }
}

@Composable
private fun TrainingSessionCard(
    session: TrainingSession,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("training_session_card_${session.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    Text(
                        text = session.focusArea,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${session.formattedDate} • ${session.formattedDuration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Middle % Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CricketPitchGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${String.format(Locale.getDefault(), "%.0f", session.middledPercentage)}% Middled",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CricketPitchGreen
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Session",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub details: Balls, Surface, Bowling, RPE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${session.totalBallsFaced} balls • ${session.pitchSurface} • RPE: ${session.rpeIntensity}/10",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                CyclePhaseBadge(phase = CyclePhase.fromString(session.cyclePhaseAtTime))
            }

            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“${session.notes}”",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
