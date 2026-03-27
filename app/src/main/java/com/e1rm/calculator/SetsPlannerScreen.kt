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

private fun roundTo2_5(weight: Double): Double = (weight / 2.5).roundToInt() * 2.5

private fun formatWeight(weight: Double): String {
    val rounded = roundTo2_5(weight)
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else "%.1f".format(rounded)
}

data class BackoffConfig(
    val id: Int,
    val numSets: String = "3",
    val reps: String = "",
    val type: String = "RPE",       // "RPE", "%", "Weight"
    val rpe: Double = 7.0,
    val percentReduction: String = "10",
    val specificWeight: String = ""
)

data class PlannedSet(
    val reps: Int,
    val weight: Double,
    val rpe: Double?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetsPlannerScreen(onNavigateBack: () -> Unit) {
    var topSetWeight by remember { mutableStateOf("") }
    var topSetReps by remember { mutableStateOf("") }
    var topSetRpe by remember { mutableStateOf(8.0) }
    var showTopRpeMenu by remember { mutableStateOf(false) }
    var backoffConfigs by remember { mutableStateOf(listOf(BackoffConfig(id = 0))) }
    var openRpeMenuId by remember { mutableStateOf(-1) }
    var plannedSets by remember { mutableStateOf<List<PlannedSet>?>(null) }
    var copied by remember { mutableStateOf(false) }
    var nextId by remember { mutableStateOf(1) }

    val rpeValues = OneRepMaxCalculator.getSupportedRpeValues()
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

            // ── Top Set ──────────────────────────────────────────────────
            Text(
                text = "First Set",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = topSetReps,
                    onValueChange = { topSetReps = it },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                ExposedDropdownMenuBox(
                    expanded = showTopRpeMenu,
                    onExpandedChange = {
                        focusManager.clearFocus()
                        showTopRpeMenu = !showTopRpeMenu
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "RPE $topSetRpe",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("RPE") },
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
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Backoff Sets ─────────────────────────────────────────────
            Text(
                text = "Additional Sets",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            backoffConfigs.forEachIndexed { index, config ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Set ${index + 2}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            if (backoffConfigs.size > 1) {
                                TextButton(
                                    onClick = {
                                        backoffConfigs = backoffConfigs.filter { it.id != config.id }
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("Remove", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = config.numSets,
                                onValueChange = { v ->
                                    backoffConfigs = backoffConfigs.map {
                                        if (it.id == config.id) it.copy(numSets = v) else it
                                    }
                                },
                                label = { Text("Sets") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = config.reps,
                                onValueChange = { v ->
                                    backoffConfigs = backoffConfigs.map {
                                        if (it.id == config.id) it.copy(reps = v) else it
                                    }
                                },
                                label = { Text("Reps (blank = same)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Type selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("RPE", "%", "Weight").forEach { type ->
                                FilterChip(
                                    selected = config.type == type,
                                    onClick = {
                                        backoffConfigs = backoffConfigs.map {
                                            if (it.id == config.id) it.copy(type = type) else it
                                        }
                                    },
                                    label = { Text(type, fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        when (config.type) {
                            "RPE" -> {
                                val menuOpen = openRpeMenuId == config.id
                                ExposedDropdownMenuBox(
                                    expanded = menuOpen,
                                    onExpandedChange = {
                                        focusManager.clearFocus()
                                        openRpeMenuId = if (menuOpen) -1 else config.id
                                    }
                                ) {
                                    OutlinedTextField(
                                        value = "RPE ${config.rpe}",
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = false,
                                        label = { Text("RPE") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuOpen) },
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
                                        expanded = menuOpen,
                                        onDismissRequest = { openRpeMenuId = -1 }
                                    ) {
                                        rpeValues.reversed().forEach { rpe ->
                                            DropdownMenuItem(
                                                text = { Text("RPE $rpe") },
                                                onClick = {
                                                    backoffConfigs = backoffConfigs.map {
                                                        if (it.id == config.id) it.copy(rpe = rpe) else it
                                                    }
                                                    openRpeMenuId = -1
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            "%" -> {
                                OutlinedTextField(
                                    value = config.percentReduction,
                                    onValueChange = { v ->
                                        backoffConfigs = backoffConfigs.map {
                                            if (it.id == config.id) it.copy(percentReduction = v) else it
                                        }
                                    },
                                    label = { Text("% Reduction from Top Set") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            "Weight" -> {
                                OutlinedTextField(
                                    value = config.specificWeight,
                                    onValueChange = { v ->
                                        backoffConfigs = backoffConfigs.map {
                                            if (it.id == config.id) it.copy(specificWeight = v) else it
                                        }
                                    },
                                    label = { Text("Specific Weight (lbs/kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    backoffConfigs = backoffConfigs + BackoffConfig(id = nextId)
                    nextId++
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Set")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Generate ─────────────────────────────────────────────────
            Button(
                onClick = {
                    focusManager.clearFocus()
                    val weight = topSetWeight.toDoubleOrNull() ?: return@Button
                    val reps = topSetReps.toIntOrNull() ?: return@Button
                    val oneRepMax = OneRepMaxCalculator.calculateOneRepMax(weight, reps, topSetRpe)

                    val sets = mutableListOf<PlannedSet>()
                    sets.add(PlannedSet(reps, weight, topSetRpe))

                    for (config in backoffConfigs) {
                        val numSets = config.numSets.toIntOrNull()?.coerceIn(1, 20) ?: continue
                        val bReps = config.reps.toIntOrNull() ?: reps

                        when (config.type) {
                            "RPE" -> {
                                val e1rm = oneRepMax ?: continue
                                val bWeight = OneRepMaxCalculator.calculateWeightForReps(e1rm, bReps, config.rpe)
                                    ?: continue
                                repeat(numSets) { sets.add(PlannedSet(bReps, bWeight, config.rpe)) }
                            }
                            "%" -> {
                                val reduction = config.percentReduction.toIntOrNull()?.coerceIn(1, 99) ?: continue
                                val bWeight = weight * (1.0 - reduction / 100.0)
                                repeat(numSets) { sets.add(PlannedSet(bReps, bWeight, null)) }
                            }
                            "Weight" -> {
                                val bWeight = config.specificWeight.toDoubleOrNull() ?: continue
                                repeat(numSets) { sets.add(PlannedSet(bReps, bWeight, null)) }
                            }
                        }
                    }

                    plannedSets = sets
                    copied = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = topSetWeight.isNotEmpty() && topSetReps.isNotEmpty()
            ) {
                Text("Generate Sets", fontSize = 18.sp)
            }

            // ── Results ───────────────────────────────────────────────────
            plannedSets?.let { sets ->
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val grouped = groupPlannedSets(sets)
                        grouped.forEachIndexed { index, (count, set) ->
                            val label = if (index == 0) "Set 1" else "Set ${index + 1}"
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
                                    text = "${formatWeight(set.weight)}$rpeStr",
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
                        clipboardManager.setText(AnnotatedString(buildCopyText(sets)))
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
            roundTo2_5(curr.weight) == roundTo2_5(prev.weight) &&
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

private fun buildCopyText(sets: List<PlannedSet>): String {
    val sb = StringBuilder()
    groupPlannedSets(sets).forEach { (count, set) ->
        sb.appendLine("$count x ${set.reps} @ ${formatWeight(set.weight)}")
    }
    return sb.toString().trimEnd()
}
