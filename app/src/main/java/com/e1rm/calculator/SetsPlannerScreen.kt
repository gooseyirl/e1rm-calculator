package com.e1rm.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class PlannedSet(
    val reps: Int,
    val weight: Double,
    val rpe: Double?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetsPlannerScreen(onNavigateBack: () -> Unit) {
    var exerciseName by remember { mutableStateOf("") }
    var topSetWeight by remember { mutableStateOf("") }
    var topSetReps by remember { mutableStateOf("") }
    var topSetRpe by remember { mutableStateOf(8.0) }
    var showTopRpeMenu by remember { mutableStateOf(false) }
    var numBackoffSets by remember { mutableStateOf("3") }
    var backoffReps by remember { mutableStateOf("") }
    var reductionType by remember { mutableStateOf("RPE") }
    var rpeReduction by remember { mutableStateOf(1.0) }
    var showReductionMenu by remember { mutableStateOf(false) }
    var percentReduction by remember { mutableStateOf("10") }
    var plannedSets by remember { mutableStateOf<List<PlannedSet>?>(null) }
    var copied by remember { mutableStateOf(false) }

    val rpeValues = OneRepMaxCalculator.getSupportedRpeValues()
    val rpeReductionOptions = listOf(0.5, 1.0, 1.5, 2.0, 2.5, 3.0)
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Sets Planner") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it },
                label = { Text("Exercise Name (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Top Set",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = topSetWeight,
                onValueChange = { topSetWeight = it },
                label = { Text("Weight (lbs/kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = topSetReps,
                onValueChange = { topSetReps = it },
                label = { Text("Reps") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = showTopRpeMenu,
                onExpandedChange = {
                    focusManager.clearFocus()
                    showTopRpeMenu = !showTopRpeMenu
                }
            ) {
                OutlinedTextField(
                    value = "RPE $topSetRpe",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Top Set RPE") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTopRpeMenu) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showTopRpeMenu,
                    onDismissRequest = { showTopRpeMenu = false }
                ) {
                    rpeValues.reversed().forEach { rpe ->
                        DropdownMenuItem(
                            text = { Text("RPE $rpe") },
                            onClick = {
                                topSetRpe = rpe
                                showTopRpeMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Backoff Sets",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = numBackoffSets,
                    onValueChange = { if (it.isEmpty() || (it.toIntOrNull() ?: 0) <= 10) numBackoffSets = it },
                    label = { Text("No. of Sets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = backoffReps,
                    onValueChange = { backoffReps = it },
                    label = { Text("Reps (blank = same)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = reductionType == "RPE",
                    onClick = { reductionType = "RPE" },
                    label = { Text("RPE Drop") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = reductionType == "%",
                    onClick = { reductionType = "%" },
                    label = { Text("% Reduction") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (reductionType == "RPE") {
                ExposedDropdownMenuBox(
                    expanded = showReductionMenu,
                    onExpandedChange = {
                        focusManager.clearFocus()
                        showReductionMenu = !showReductionMenu
                    }
                ) {
                    OutlinedTextField(
                        value = "-$rpeReduction RPE",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("RPE Reduction") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showReductionMenu) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showReductionMenu,
                        onDismissRequest = { showReductionMenu = false }
                    ) {
                        rpeReductionOptions.forEach { reduction ->
                            DropdownMenuItem(
                                text = { Text("-$reduction RPE") },
                                onClick = {
                                    rpeReduction = reduction
                                    showReductionMenu = false
                                }
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = percentReduction,
                    onValueChange = { percentReduction = it },
                    label = { Text("% Reduction from Top Set") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    val weight = topSetWeight.toDoubleOrNull() ?: return@Button
                    val reps = topSetReps.toIntOrNull() ?: return@Button
                    val numSets = numBackoffSets.toIntOrNull()?.coerceIn(1, 10) ?: return@Button
                    val bReps = backoffReps.toIntOrNull() ?: reps

                    val sets = mutableListOf<PlannedSet>()
                    sets.add(PlannedSet(reps, weight, topSetRpe))

                    if (reductionType == "RPE") {
                        val oneRepMax = OneRepMaxCalculator.calculateOneRepMax(weight, reps, topSetRpe)
                            ?: return@Button
                        val backoffRpe = (topSetRpe - rpeReduction).coerceAtLeast(6.0)
                        val backoffWeight = OneRepMaxCalculator.calculateWeightForReps(oneRepMax, bReps, backoffRpe)
                            ?: return@Button
                        repeat(numSets) { sets.add(PlannedSet(bReps, backoffWeight, backoffRpe)) }
                    } else {
                        val reduction = percentReduction.toIntOrNull()?.coerceIn(1, 50) ?: 10
                        val backoffWeight = weight * (1.0 - reduction / 100.0)
                        repeat(numSets) { sets.add(PlannedSet(bReps, backoffWeight, null)) }
                    }

                    plannedSets = sets
                    copied = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = topSetWeight.isNotEmpty() && topSetReps.isNotEmpty() && numBackoffSets.isNotEmpty()
            ) {
                Text("Generate Sets", fontSize = 18.sp)
            }

            plannedSets?.let { sets ->
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (exerciseName.isNotBlank()) {
                            Text(
                                text = exerciseName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        val grouped = groupPlannedSets(sets)
                        grouped.forEachIndexed { index, (count, set) ->
                            val label = if (index == 0) "Top Set" else "Backoff"
                            val rpeStr = set.rpe?.let { "  @RPE $it" } ?: ""
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.width(60.dp)
                                )
                                Text(
                                    text = "${count}×${set.reps}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${set.weight.roundToInt()}$rpeStr",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (index < grouped.size - 1) {
                                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(buildCopyText(exerciseName, sets)))
                        copied = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = if (copied) ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) else ButtonDefaults.buttonColors()
                ) {
                    Text(if (copied) "✓ Copied!" else "Copy to Clipboard")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun groupPlannedSets(sets: List<PlannedSet>): List<Pair<Int, PlannedSet>> {
    if (sets.isEmpty()) return emptyList()
    val result = mutableListOf<Pair<Int, PlannedSet>>()
    var count = 1
    for (i in 1 until sets.size) {
        val curr = sets[i]
        val prev = sets[i - 1]
        if (curr.reps == prev.reps &&
            curr.weight.roundToInt() == prev.weight.roundToInt() &&
            curr.rpe == prev.rpe
        ) {
            count++
        } else {
            result.add(count to prev)
            count = 1
        }
    }
    result.add(count to sets.last())
    return result
}

private fun buildCopyText(exerciseName: String, sets: List<PlannedSet>): String {
    val sb = StringBuilder()
    if (exerciseName.isNotBlank()) sb.appendLine(exerciseName)
    groupPlannedSets(sets).forEach { (count, set) ->
        val rpeStr = set.rpe?.let { " @RPE$it" } ?: ""
        sb.appendLine("${count}x${set.reps} @ ${set.weight.roundToInt()}$rpeStr")
    }
    return sb.toString().trimEnd()
}
