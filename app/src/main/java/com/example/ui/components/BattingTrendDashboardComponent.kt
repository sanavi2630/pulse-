package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CyclePhase
import com.example.data.model.MatchInning
import com.example.ui.theme.CricketPitchGreen
import com.example.ui.theme.MilestoneGold
import java.util.Locale

data class HistoricalTrendPoint(
    val inningNumber: Int,
    val dateMillis: Long,
    val formattedDate: String,
    val opponent: String,
    val matchFormat: String,
    val runs: Int,
    val isNotOut: Boolean,
    val cumulativeRuns: Int,
    val cumulativeAverage: Double,
    val cyclePhase: CyclePhase
)

enum class TrendViewMode(val title: String) {
    AVERAGE("Batting Average Trend"),
    ACCUMULATION("Accumulated Runs"),
    COMBINED("Combined Analytics")
}

/**
 * Native Jetpack Compose data visualization dashboard for tracking historical
 * batting average trends and run accumulation over time.
 *
 * Implements smooth hardware-accelerated canvas curves, area fills,
 * interactive touch scrubber inspection, and milestone peak telemetry.
 */
@Composable
fun BattingTrendDashboardComponent(
    innings: List<MatchInning>,
    modifier: Modifier = Modifier,
    title: String = "Historical Performance Trends"
) {
    var viewMode by remember { mutableStateOf(TrendViewMode.COMBINED) }
    var selectedPointIndex by remember { mutableIntStateOf(-1) }

    // Sort chronologically (oldest to newest) to track career trajectory
    val chronologicalInnings = remember(innings) {
        innings.sortedBy { it.dateMillis }
    }

    val trendPoints = remember(chronologicalInnings) {
        var cumRuns = 0
        var cumDismissals = 0
        chronologicalInnings.mapIndexed { index, match ->
            cumRuns += match.runs
            if (!match.isNotOut) {
                cumDismissals += 1
            }
            val avg = if (cumDismissals > 0) cumRuns.toDouble() / cumDismissals else cumRuns.toDouble()
            HistoricalTrendPoint(
                inningNumber = index + 1,
                dateMillis = match.dateMillis,
                formattedDate = match.formattedDate,
                opponent = match.opponent,
                matchFormat = match.matchFormat,
                runs = match.runs,
                isNotOut = match.isNotOut,
                cumulativeRuns = cumRuns,
                cumulativeAverage = avg,
                cyclePhase = CyclePhase.fromString(match.cyclePhaseAtTime)
            )
        }
    }

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(trendPoints, viewMode) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    val selectedPoint = trendPoints.getOrNull(selectedPointIndex)
    val maxAvg = trendPoints.maxOfOrNull { it.cumulativeAverage } ?: 50.0
    val totalAccumulated = trendPoints.lastOrNull()?.cumulativeRuns ?: 0
    val careerAverage = trendPoints.lastOrNull()?.cumulativeAverage ?: 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("batting_trend_dashboard_component"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
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
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timeline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
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
                            text = "${trendPoints.size} Match Innings Analyzed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Peak Avg: ${String.format(Locale.getDefault(), "%.1f", maxAvg)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrendViewMode.entries.forEach { mode ->
                    FilterChip(
                        selected = viewMode == mode,
                        onClick = {
                            viewMode = mode
                            selectedPointIndex = -1
                        },
                        label = { Text(mode.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Inspection HUD
            if (selectedPoint != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Inning #${selectedPoint.inningNumber}: vs ${selectedPoint.opponent} (${selectedPoint.matchFormat})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Scored: ${selectedPoint.runs}${if (selectedPoint.isNotOut) "*" else ""} runs • ${selectedPoint.formattedDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Avg: ${String.format(Locale.getDefault(), "%.2f", selectedPoint.cumulativeAverage)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Total: ${selectedPoint.cumulativeRuns} runs",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = CricketPitchGreen
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                Text(
                    text = "Tap on any data point to inspect match details & trajectory",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Canvas Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    .padding(8.dp)
            ) {
                if (trendPoints.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Log match innings to visualize career trends", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = CricketPitchGreen
                    val goldColor = MilestoneGold
                    val highlightLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(trendPoints) {
                                detectTapGestures { tapOffset ->
                                    val paddingLeft = 35f
                                    val paddingRight = 20f
                                    val chartWidth = size.width - paddingLeft - paddingRight
                                    if (trendPoints.size > 1) {
                                        val stepX = chartWidth / (trendPoints.size - 1)
                                        val clickedIdx = ((tapOffset.x - paddingLeft + (stepX / 2)) / stepX)
                                            .toInt()
                                            .coerceIn(0, trendPoints.size - 1)
                                        selectedPointIndex = clickedIdx
                                    } else {
                                        selectedPointIndex = 0
                                    }
                                }
                            }
                    ) {
                        val paddingLeft = 45f
                        val paddingRight = 25f
                        val paddingTop = 25f
                        val paddingBottom = 35f

                        val width = size.width - paddingLeft - paddingRight
                        val height = size.height - paddingTop - paddingBottom

                        val n = trendPoints.size
                        val stepX = if (n > 1) width / (n - 1) else width

                        // Max scale references
                        val maxRuns = trendPoints.maxOfOrNull { it.cumulativeRuns }?.coerceAtLeast(100) ?: 100
                        val upperAvg = (maxAvg * 1.25).coerceAtLeast(30.0)

                        // 1. Draw horizontal grid lines
                        val gridLines = 4
                        for (i in 0..gridLines) {
                            val y = paddingTop + (height / gridLines) * i
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(paddingLeft, y),
                                end = Offset(size.width - paddingRight, y),
                                strokeWidth = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        }

                        // 2. Draw Run Accumulation Bars or Area (if in ACCUMULATION or COMBINED mode)
                        if (viewMode == TrendViewMode.ACCUMULATION || viewMode == TrendViewMode.COMBINED) {
                            val barWidth = (stepX * 0.45f).coerceIn(8f, 24f)
                            trendPoints.forEachIndexed { i, pt ->
                                val x = paddingLeft + i * stepX
                                val normalizedRunHeight = (pt.runs.toFloat() / 120f).coerceIn(0f, 1f) * height * animProgress.value
                                val barTop = paddingTop + height - normalizedRunHeight

                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(secondaryColor.copy(alpha = 0.7f), secondaryColor.copy(alpha = 0.25f)),
                                        startY = barTop,
                                        endY = paddingTop + height
                                    ),
                                    topLeft = Offset(x - barWidth / 2, barTop),
                                    size = Size(barWidth, normalizedRunHeight),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                                )
                            }
                        }

                        // 3. Draw Cumulative Batting Average Trend Curve
                        if (viewMode == TrendViewMode.AVERAGE || viewMode == TrendViewMode.COMBINED) {
                            val path = Path()
                            val fillPath = Path()

                            val points = trendPoints.mapIndexed { i, pt ->
                                val x = paddingLeft + i * stepX
                                val yNormalized = (pt.cumulativeAverage / upperAvg).coerceIn(0.0, 1.0).toFloat()
                                val y = paddingTop + height - (yNormalized * height * animProgress.value)
                                Offset(x, y)
                            }

                            if (points.isNotEmpty()) {
                                path.moveTo(points.first().x, points.first().y)
                                fillPath.moveTo(points.first().x, paddingTop + height)
                                fillPath.lineTo(points.first().x, points.first().y)

                                for (i in 0 until points.size - 1) {
                                    val p0 = points[i]
                                    val p1 = points[i + 1]
                                    val controlX1 = p0.x + (p1.x - p0.x) / 2
                                    val controlY1 = p0.y
                                    val controlX2 = p0.x + (p1.x - p0.x) / 2
                                    val controlY2 = p1.y

                                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                                }

                                fillPath.lineTo(points.last().x, paddingTop + height)
                                fillPath.close()

                                // Gradient Area under line
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.35f),
                                            primaryColor.copy(alpha = 0.02f)
                                        ),
                                        startY = paddingTop,
                                        endY = paddingTop + height
                                    )
                                )

                                // Main Trend Line
                                drawPath(
                                    path = path,
                                    color = primaryColor,
                                    style = Stroke(
                                        width = 3.5.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )

                                // Data Point Nodes
                                points.forEachIndexed { idx, pointOffset ->
                                    val isSelected = idx == selectedPointIndex
                                    val is50Plus = trendPoints[idx].runs >= 50
                                    val nodeColor = if (is50Plus) goldColor else primaryColor

                                    drawCircle(
                                        color = if (isSelected) Color.White else nodeColor,
                                        radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                                        center = pointOffset
                                    )
                                    drawCircle(
                                        color = nodeColor,
                                        radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                                        center = pointOffset,
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
                            }
                        }

                        // 4. Highlight Selected Match Line
                        if (selectedPointIndex in trendPoints.indices) {
                            val selectedX = paddingLeft + selectedPointIndex * stepX
                            drawLine(
                                color = highlightLineColor,
                                start = Offset(selectedX, paddingTop),
                                end = Offset(selectedX, paddingTop + height),
                                strokeWidth = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = MaterialTheme.colorScheme.primary, label = "Cumulative Average")
                LegendItem(color = CricketPitchGreen, label = "Innings Runs")
                LegendItem(color = MilestoneGold, label = "50s & Milestones")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
