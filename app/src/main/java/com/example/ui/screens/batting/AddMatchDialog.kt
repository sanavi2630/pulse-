package com.example.ui.screens.batting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.CyclePhase
import com.example.data.model.MatchInning

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddMatchDialog(
    initialCyclePhase: CyclePhase,
    onDismiss: () -> Unit,
    onSave: (MatchInning) -> Unit
) {
    var opponent by remember { mutableStateOf("") }
    var matchFormat by remember { mutableStateOf("T20") }
    var runsStr by remember { mutableStateOf("") }
    var ballsStr by remember { mutableStateOf("") }
    var foursStr by remember { mutableStateOf("") }
    var sixesStr by remember { mutableStateOf("") }
    var isNotOut by remember { mutableStateOf(false) }
    var battingPosition by remember { mutableIntStateOf(3) }
    var dismissalType by remember { mutableStateOf("Caught") }
    var bowlerFaced by remember { mutableStateOf("Pace / Seam") }
    var venue by remember { mutableStateOf("Home Ground") }
    var selectedPhase by remember { mutableStateOf(initialCyclePhase) }
    var notes by remember { mutableStateOf("") }

    val formats = listOf("T20", "ODI", "Test", "Club")
    val dismissals = listOf("Caught", "Bowled", "LBW", "Run Out", "Stumped", "Not Out")
    val bowlerTypes = listOf("Pace / Seam", "Spin / Orthodox", "Leg Spin / Wrist")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Match Inning",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Opponent / Team") },
                    placeholder = { Text("e.g. Sydney Sixers / University CC") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("match_opponent_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Format", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    formats.forEach { fmt ->
                        FilterChip(
                            selected = matchFormat == fmt,
                            onClick = { matchFormat = fmt },
                            label = { Text(fmt) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = runsStr,
                        onValueChange = { runsStr = it.filter { char -> char.isDigit() } },
                        label = { Text("Runs Scored *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("match_runs_input")
                    )
                    OutlinedTextField(
                        value = ballsStr,
                        onValueChange = { ballsStr = it.filter { char -> char.isDigit() } },
                        label = { Text("Balls Faced *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("match_balls_input")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = foursStr,
                        onValueChange = { foursStr = it.filter { char -> char.isDigit() } },
                        label = { Text("4s") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sixesStr,
                        onValueChange = { sixesStr = it.filter { char -> char.isDigit() } },
                        label = { Text("6s") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Remained Not Out?", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = isNotOut,
                        onCheckedChange = {
                            isNotOut = it
                            if (it) dismissalType = "Not Out"
                        },
                        modifier = Modifier.testTag("match_not_out_switch")
                    )
                }

                if (!isNotOut) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dismissal Mode", style = MaterialTheme.typography.labelMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        dismissals.filter { it != "Not Out" }.forEach { dis ->
                            FilterChip(
                                selected = dismissalType == dis,
                                onClick = { dismissalType = dis },
                                label = { Text(dis) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Bowler Faced Most", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    bowlerTypes.forEach { bt ->
                        FilterChip(
                            selected = bowlerFaced == bt,
                            onClick = { bowlerFaced = bt },
                            label = { Text(bt) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Cycle Phase During Match", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CyclePhase.entries.forEach { ph ->
                        FilterChip(
                            selected = selectedPhase == ph,
                            onClick = { selectedPhase = ph },
                            label = { Text(ph.displayName.substringBefore(" ")) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Ground / Venue") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Key Inning Notes & Tactics") },
                    placeholder = { Text("e.g. attacked short ball well, held one end") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val runs = runsStr.toIntOrNull() ?: 0
                    val balls = ballsStr.toIntOrNull() ?: 0
                    val fours = foursStr.toIntOrNull() ?: 0
                    val sixes = sixesStr.toIntOrNull() ?: 0

                    val inning = MatchInning(
                        dateMillis = System.currentTimeMillis(),
                        opponent = if (opponent.isBlank()) "Opponent XI" else opponent,
                        matchFormat = matchFormat,
                        runs = runs,
                        ballsFaced = balls,
                        fours = fours,
                        sixes = sixes,
                        isNotOut = isNotOut,
                        battingPosition = battingPosition,
                        dismissalType = if (isNotOut) "Not Out" else dismissalType,
                        bowlerFacedMost = bowlerFaced,
                        cyclePhaseAtTime = selectedPhase.name,
                        venue = if (venue.isBlank()) "Home Ground" else venue,
                        notes = notes
                    )
                    onSave(inning)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_save_match_button")
            ) {
                Text("Save Inning")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_save_match_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
