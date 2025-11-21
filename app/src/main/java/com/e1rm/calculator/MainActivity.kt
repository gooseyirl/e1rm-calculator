package com.e1rm.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            E1RMCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OneRepMaxScreen()
                }
            }
        }
    }
}

@Composable
fun E1RMCalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = MaterialTheme.colorScheme.primary,
            secondary = MaterialTheme.colorScheme.secondary,
            background = MaterialTheme.colorScheme.background
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneRepMaxScreen() {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var selectedRpe by remember { mutableStateOf(10.0) }
    var calculatedMax by remember { mutableStateOf<Double?>(null) }
    var showRpeMenu by remember { mutableStateOf(false) }
    var customPercentage by remember { mutableStateOf("") }

    val rpeValues = OneRepMaxCalculator.getSupportedRpeValues()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "E1RM Calculator",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "One Rep Max Estimator",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Weight Input
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Weight (lbs/kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reps Input
        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it },
            label = { Text("Reps (1-10)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // RPE Dropdown
        ExposedDropdownMenuBox(
            expanded = showRpeMenu,
            onExpandedChange = { showRpeMenu = !showRpeMenu }
        ) {
            OutlinedTextField(
                value = "RPE: $selectedRpe",
                onValueChange = {},
                readOnly = true,
                label = { Text("Rate of Perceived Exertion") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRpeMenu) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = showRpeMenu,
                onDismissRequest = { showRpeMenu = false }
            ) {
                rpeValues.reversed().forEach { rpe ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = "RPE $rpe",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = getRpeDescription(rpe),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            selectedRpe = rpe
                            showRpeMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // RPE Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "RPE ${selectedRpe}: ${getRpeDescription(selectedRpe)}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Calculate Button
        Button(
            onClick = {
                val w = weight.toDoubleOrNull()
                val r = reps.toIntOrNull()
                if (w != null && r != null) {
                    calculatedMax = OneRepMaxCalculator.calculateOneRepMax(w, r, selectedRpe)
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = weight.isNotEmpty() && reps.isNotEmpty()
        ) {
            Text("Calculate 1RM", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Result Display
        calculatedMax?.let { max ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Estimated 1RM",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${max.roundToInt()}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "lbs/kg",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Percentage Reference Table
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Training Percentages",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Percentage Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customPercentage,
                    onValueChange = { customPercentage = it },
                    label = { Text("Custom %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                customPercentage.toIntOrNull()?.let { percentage ->
                    if (percentage in 1..100) {
                        val customWeight = (max * percentage / 100.0).roundToInt()
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$percentage%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "$customWeight lbs/kg",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val percentages = listOf(95, 90, 85, 80, 75, 70, 65, 60)
            percentages.forEach { percentage ->
                val calculatedWeight = (max * percentage / 100.0).roundToInt()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$percentage%",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$calculatedWeight lbs/kg",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Info Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About This Calculator",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This calculator uses RPE (Rate of Perceived Exertion) based formulas " +
                            "similar to the Barbell Medicine approach. Enter the weight you lifted, " +
                            "the number of reps (1-10), and your RPE to get an accurate 1RM estimate.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

fun getRpeDescription(rpe: Double): String {
    return when (rpe) {
        10.0 -> "Maximum effort - no reps left"
        9.5 -> "Could do 1 more rep, maybe"
        9.0 -> "Could definitely do 1 more rep"
        8.5 -> "Could do 1-2 more reps"
        8.0 -> "Could definitely do 2 more reps"
        7.5 -> "Could do 2-3 more reps"
        7.0 -> "Could definitely do 3 more reps"
        6.5 -> "Could do 3-4 more reps"
        6.0 -> "Could definitely do 4 more reps"
        else -> "Unknown RPE"
    }
}
