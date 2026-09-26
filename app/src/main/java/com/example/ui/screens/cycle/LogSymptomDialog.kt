package com.example.ui.screens.cycle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CycleLog
import com.example.ui.theme.CycleRose
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogSymptomDialog(
    existingLog: CycleLog?,
    onDismiss: () -> Unit,
    onSave: (
        isPeriod: Boolean,
        flow: String,
        energy: Int,
        cramps: Int,
        soreness: Int,
        mood: String,
        notes: String
    ) -> Unit
) {
    var isPeriod by remember { mutableStateOf(existingLog?.isPeriodDay ?: false) }
    var flow by remember { mutableStateOf(existingLog?.flowIntensity ?: "Medium") }
    var energy by remember { mutableFloatStateOf((existingLog?.energyLevel ?: 4).toFloat()) }
    var cramps by remember { mutableFloatStateOf((existingLog?.crampPainLevel ?: 0).toFloat()) }
    var soreness by remember { mutableFloatStateOf((existingLog?.muscleSoreness ?: 1).toFloat()) }
    var mood by remember { mutableStateOf(existingLog?.mood ?: "Focused") }
    var notes by remember { mutableStateOf(existingLog?.notes ?: "") }

    val flowOptions = listOf("Spotting", "Light", "Medium", "Heavy")
    val moodOptions = listOf("Optimal", "Focused", "Calm", "Tired", "Irritable")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Today's Athletic Wellness Log",
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
                // Period Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Period / Menstruation Active?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = isPeriod,
                        onCheckedChange = { isPeriod = it },
                        modifier = Modifier.testTag("period_active_switch")
                    )
                }

                if (isPeriod) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Flow Level", style = MaterialTheme.typography.labelMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        flowOptions.forEach { fl ->
                            FilterChip(
                                selected = flow == fl,
                                onClick = { flow = fl },
                                label = { Text(fl) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Energy Slider
                Text(
                    text = "Energy & Stamina: ${energy.roundToInt()} / 5",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = energy,
                    onValueChange = { energy = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    modifier = Modifier.testTag("slider_energy")
                )

                // Cramp Slider
                Text(
                    text = "Abdominal / Cramp Pain: ${cramps.roundToInt()} / 5",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = cramps,
                    onValueChange = { cramps = it },
                    valueRange = 0f..5f,
                    steps = 4,
                    modifier = Modifier.testTag("slider_cramps")
                )

                // Muscle Soreness Slider
                Text(
                    text = "Muscle Soreness (DOMS): ${soreness.roundToInt()} / 5",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = soreness,
                    onValueChange = { soreness = it },
                    valueRange = 0f..5f,
                    steps = 4,
                    modifier = Modifier.testTag("slider_soreness")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mood
                Text("Athlete Mindset & Mood", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    moodOptions.forEach { m ->
                        FilterChip(
                            selected = mood == m,
                            onClick = { mood = m },
                            label = { Text(m) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Physical sensations or match prep notes") },
                    placeholder = { Text("e.g. hydrated with electrolyte, felt good bat swing") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        isPeriod,
                        if (isPeriod) flow else "None",
                        energy.roundToInt(),
                        cramps.roundToInt(),
                        soreness.roundToInt(),
                        mood,
                        notes
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_save_wellness_button")
            ) {
                Text("Save Log")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_wellness_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
