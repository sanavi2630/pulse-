package com.example.ui.screens.training

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupTrainingDialog(
    onDismiss: () -> Unit,
    onStart: (pitch: String, bowling: String, ball: String, focus: String) -> Unit
) {
    var pitch by remember { mutableStateOf("Turf / Grass") }
    var bowling by remember { mutableStateOf("Sidearm Throwdown") }
    var ball by remember { mutableStateOf("White Ball (Leather)") }
    var focus by remember { mutableStateOf("Front Foot Drives") }

    val pitchOptions = listOf("Turf / Grass", "Astro Turf", "Indoor Net", "Cement / Matting")
    val bowlingOptions = listOf("Sidearm Throwdown", "Bowling Machine", "Pace Bowlers", "Spin Bowlers")
    val ballOptions = listOf("White Ball (Leather)", "Red Ball (Leather)", "Machine Ball", "Tennis / Poly")
    val focusOptions = listOf("Front Foot Drives", "Pull & Short Ball", "Spin Footwork", "Power Hitting", "Defense & Leaves")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Setup Net Training Session",
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
                Text("Pitch Surface", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pitchOptions.forEach { p ->
                        FilterChip(
                            selected = pitch == p,
                            onClick = { pitch = p },
                            label = { Text(p) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Bowling Delivery Mode", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    bowlingOptions.forEach { b ->
                        FilterChip(
                            selected = bowling == b,
                            onClick = { bowling = b },
                            label = { Text(b) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Ball Type", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ballOptions.forEach { bt ->
                        FilterChip(
                            selected = ball == bt,
                            onClick = { ball = bt },
                            label = { Text(bt) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Training Focus Area", style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    focusOptions.forEach { f ->
                        FilterChip(
                            selected = focus == f,
                            onClick = { focus = f },
                            label = { Text(f) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onStart(pitch, bowling, ball, focus) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("start_live_session_confirm_button")
            ) {
                Text("Enter Nets & Track")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_setup_session_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
