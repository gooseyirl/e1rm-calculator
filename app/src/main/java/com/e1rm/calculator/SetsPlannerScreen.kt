package com.e1rm.calculator

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import kotlinx.coroutines.launch
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// "RPE"    — weight from e1rm + RPE table
// "%1RM"   — percentage of e1rm
// "%last"  — percentage change from previous set's weight
// "Weight" — explicit weight
data class SetConfig(
    val id: Int,
    val numSets: String = "1",
    val reps: String = "5",
    val type: String = "RPE",
    val rpe: Double = 8.0,
    val percentE1rm: String = "80",
    val percentDelta: String = "5",
    val percentIsIncrease: Boolean = false,
    val specificWeight: String = ""
)

data class PlannedSet(
    val reps: Int,
    val weight: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetsPlannerScreen(
    units: String = "kg",
    rounding: String = "default_0_5",
    initialE1rm: Double? = null,
    onNavigateBack: () -> Unit
) {
    var e1rmInput by remember {
        mutableStateOf(
            if (initialE1rm != null) formatWeight(initialE1rm, rounding) else ""
        )
    }
    var sets by remember { mutableStateOf(listOf(SetConfig(id = 0))) }
    var nextId by remember { mutableStateOf(1) }
    var openRpeMenuId by remember { mutableStateOf(-1) }
    var plannedSets by remember { mutableStateOf<List<PlannedSet>?>(null) }
    var generateError by remember { mutableStateOf<String?>(null) }
    var copied by remember { mutableStateOf(false) }
    // Local rounding that can be toggled without leaving the screen
    var localRounding by remember { mutableStateOf(rounding) }

    val rpeValues = OneRepMaxCalculator.getSupportedRpeValues()
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(plannedSets) {
        if (plannedSets != null) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    // Extracted generate logic — called from the button and from the rounding toggle
    fun doGenerate() {
        focusManager.clearFocus()
        generateError = null

        val e1rm = e1rmInput.toDoubleOrNull()
        val result = mutableListOf<PlannedSet>()
        var lastWeight: Double? = null

        for ((index, config) in sets.withIndex()) {
            val reps = config.reps.toIntOrNull()
            if (reps == null || reps <= 0) {
                generateError = "Set ${index + 1}: enter a valid rep count"
                return
            }
            val numSets = config.numSets.toIntOrNull()?.coerceAtLeast(1) ?: 1

            val weight: Double = when (config.type) {
                "RPE" -> {
                    if (e1rm == null) { generateError = "Enter an E1RM to use RPE sets"; return }
                    OneRepMaxCalculator.calculateWeightForReps(e1rm, reps, config.rpe)
                        ?: run { generateError = "Set ${index + 1}: reps/RPE combination not in table"; return }
                }
                "%1RM" -> {
                    if (e1rm == null) { generateError = "Enter an E1RM to use % 1RM sets"; return }
                    val pct = config.percentE1rm.toDoubleOrNull() ?: run {
                        generateError = "Set ${index + 1}: enter a valid percentage"; return
                    }
                    e1rm * (pct / 100.0)
                }
                "%last" -> {
                    if (lastWeight == null) {
                        generateError = "Set ${index + 1}: no previous set to reference"; return
                    }
                    val delta = config.percentDelta.toDoubleOrNull() ?: run {
                        generateError = "Set ${index + 1}: enter a valid percentage"; return
                    }
                    if (config.percentIsIncrease) lastWeight!! * (1 + delta / 100.0)
                    else lastWeight!! * (1 - delta / 100.0)
                }
                else -> { // "Weight"
                    config.specificWeight.toDoubleOrNull() ?: run {
                        generateError = "Set ${index + 1}: enter a valid weight"; return
                    }
                }
            }

            val rounded = roundWeight(weight, localRounding)
            repeat(numSets) { result.add(PlannedSet(reps = reps, weight = rounded)) }
            lastWeight = rounded
        }

        plannedSets = result
        copied = false
    }

    BackHandler { onNavigateBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sets Planner",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Plan your sets",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── E1RM ──────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "E1RM",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = e1rmInput,
                        onValueChange = { e1rmInput = it; plannedSets = null; generateError = null },
                        label = { Text("Estimated 1RM ($units) — optional") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (e1rmInput.isNotBlank() && e1rmInput.toDoubleOrNull() == null) {
                        Text(
                            "Enter a valid number",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    } else {
                        Text(
                            "Required for RPE and % 1RM sets",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sets ──────────────────────────────────────────────────────
            Text(
                text = "Sets",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            sets.forEachIndexed { index, config ->
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
                            Text("Set ${index + 1}", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            if (sets.size > 1) {
                                TextButton(
                                    onClick = {
                                        sets = sets.filter { it.id != config.id }
                                        plannedSets = null
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
                                    sets = sets.map { if (it.id == config.id) it.copy(numSets = v) else it }
                                    plannedSets = null
                                },
                                label = { Text("Sets") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = config.reps,
                                onValueChange = { v ->
                                    sets = sets.map { if (it.id == config.id) it.copy(reps = v) else it }
                                    plannedSets = null
                                },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("RPE", "%1RM", "%last", "Weight").forEach { type ->
                                val chipLabel = when {
                                    type == "%1RM" -> "% 1RM"
                                    type == "%last" && config.type == "%last" ->
                                        if (config.percentIsIncrease) "% +" else "% -"
                                    type == "%last" -> "% last"
                                    else -> type
                                }
                                FilterChip(
                                    selected = config.type == type,
                                    onClick = {
                                        sets = sets.map {
                                            if (it.id == config.id) {
                                                if (it.type == "%last" && type == "%last")
                                                    it.copy(percentIsIncrease = !it.percentIsIncrease)
                                                else
                                                    it.copy(type = type, percentIsIncrease = false)
                                            } else it
                                        }
                                        plannedSets = null
                                    },
                                    label = { Text(chipLabel, fontSize = 12.sp) },
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
                                                    sets = sets.map {
                                                        if (it.id == config.id) it.copy(rpe = rpe) else it
                                                    }
                                                    openRpeMenuId = -1
                                                    plannedSets = null
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            "%1RM" -> {
                                OutlinedTextField(
                                    value = config.percentE1rm,
                                    onValueChange = { v ->
                                        sets = sets.map { if (it.id == config.id) it.copy(percentE1rm = v) else it }
                                        plannedSets = null
                                    },
                                    label = { Text("% of E1RM") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    suffix = { Text("%") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            "%last" -> {
                                OutlinedTextField(
                                    value = config.percentDelta,
                                    onValueChange = { v ->
                                        sets = sets.map { if (it.id == config.id) it.copy(percentDelta = v) else it }
                                        plannedSets = null
                                    },
                                    label = {
                                        Text(
                                            if (config.percentIsIncrease) "% increase from last set"
                                            else "% reduction from last set"
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    suffix = { Text("%") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = "Tap the chip again to toggle + / −",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            "Weight" -> {
                                OutlinedTextField(
                                    value = config.specificWeight,
                                    onValueChange = { v ->
                                        sets = sets.map { if (it.id == config.id) it.copy(specificWeight = v) else it }
                                        plannedSets = null
                                    },
                                    label = { Text("Weight ($units)") },
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
                    sets = sets + SetConfig(id = nextId)
                    nextId++
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Set")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Rounding toggle ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Rounding",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                listOf("0.5", "2.5").forEach { inc ->
                    val isSelected = if (inc == "2.5") localRounding.endsWith("2_5")
                                     else !localRounding.endsWith("2_5")
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                localRounding = if (inc == "2.5")
                                    localRounding.replace("0_5", "2_5")
                                else
                                    localRounding.replace("2_5", "0_5")
                                if (plannedSets != null) doGenerate()
                            }
                        },
                        label = { Text("$inc $units") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Generate ──────────────────────────────────────────────────
            Button(
                onClick = { doGenerate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Generate Sets", fontSize = 18.sp)
            }

            generateError?.let { err ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(err, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
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
                        val grouped = groupPlannedSets(sets, localRounding)
                        grouped.forEachIndexed { index, (count, set) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Set ${index + 1}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${count}×${set.reps}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "${formatWeight(set.weight, localRounding)} $units",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                            if (index < grouped.size - 1) {
                                Divider(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(buildCopyText(sets, units, localRounding)))
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

private fun groupPlannedSets(sets: List<PlannedSet>, rounding: String): List<Pair<Int, PlannedSet>> {
    if (sets.isEmpty()) return emptyList()
    val result = mutableListOf<Pair<Int, PlannedSet>>()
    var count = 1
    for (i in 1 until sets.size) {
        val curr = sets[i]
        val prev = sets[i - 1]
        if (curr.reps == prev.reps &&
            roundWeight(curr.weight, rounding) == roundWeight(prev.weight, rounding)
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

private fun buildCopyText(sets: List<PlannedSet>, units: String, rounding: String): String {
    val sb = StringBuilder()
    groupPlannedSets(sets, rounding).forEach { (count, set) ->
        sb.appendLine("$count x ${set.reps} @ ${formatWeight(set.weight, rounding)}$units")
    }
    return sb.toString().trimEnd()
}
