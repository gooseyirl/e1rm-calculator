package com.e1rm.calculator

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

fun roundWeight(weight: Double, rounding: String): Double {
    val inc = if (rounding.endsWith("2_5")) 2.5 else 0.5
    return when {
        rounding.startsWith("up")      -> ceil(weight / inc) * inc
        rounding.startsWith("down")    -> floor(weight / inc) * inc
        else /* "default" */           -> (weight / inc).roundToInt() * inc
    }
}

fun formatWeight(weight: Double, rounding: String): String {
    val rounded = roundWeight(weight, rounding)
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else "%.1f".format(rounded)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("e1rm_prefs", Context.MODE_PRIVATE)
        val dailyQuote = getNextQuote(prefs)
        var onDonatedCallback: (() -> Unit)? = null
        val billingManager = BillingManager(this) { onDonatedCallback?.invoke() }
        billingManager.connect()
        setContent {
            E1RMCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("main") }
                    // Hoisted so values survive navigation to settings / sets planner and back
                    var calcWeight by remember { mutableStateOf("") }
                    var calcReps by remember { mutableStateOf("1") }
                    var calcRpe by remember { mutableStateOf(10.0) }
                    var calcMax by remember { mutableStateOf<Double?>(null) }
                    var calcCustomPct by remember { mutableStateOf("") }
                    var isDonated by remember { mutableStateOf(BillingManager.isDonated(this@MainActivity)) }
                    onDonatedCallback = { isDonated = true }
                    var units by remember { mutableStateOf(prefs.getString("units", "kg") ?: "kg") }
                    var rounding by remember { mutableStateOf(prefs.getString("rounding", "default_0_5") ?: "default_0_5") }
                    val onUnitsChanged: (String) -> Unit = { selected ->
                        units = selected
                        prefs.edit().putString("units", selected).apply()
                    }
                    val onRoundingChanged: (String) -> Unit = { selected ->
                        rounding = selected
                        prefs.edit().putString("rounding", selected).apply()
                    }
                    when (currentScreen) {
                        "sets_planner" -> SetsPlannerScreen(
                            units = units,
                            rounding = rounding,
                            initialE1rm = calcMax,
                            onNavigateBack = { currentScreen = "main" }
                        )
                        "settings" -> SettingsScreen(
                            units = units,
                            rounding = rounding,
                            onUnitsChanged = onUnitsChanged,
                            onRoundingChanged = onRoundingChanged,
                            onNavigateBack = { currentScreen = "main" }
                        )
                        else -> OneRepMaxScreen(
                            units = units,
                            rounding = rounding,
                            isDonated = isDonated,
                            quote = dailyQuote,
                            weight = calcWeight,
                            reps = calcReps,
                            selectedRpe = calcRpe,
                            calculatedMax = calcMax,
                            customPercentage = calcCustomPct,
                            onWeightChanged = { calcWeight = it },
                            onRepsChanged = { calcReps = it },
                            onRpeChanged = { calcRpe = it },
                            onCalculatedMaxChanged = { calcMax = it },
                            onCustomPercentageChanged = { calcCustomPct = it },
                            onSupportDeveloper = { billingManager.launchPurchaseFlow() },
                            onNavigateToPlanner = { currentScreen = "sets_planner" },
                            onNavigateToSettings = { currentScreen = "settings" }
                        )
                    }
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
fun OneRepMaxScreen(
    units: String = "kg",
    rounding: String = "default_0_5",
    isDonated: Boolean = false,
    quote: String = "",
    weight: String = "",
    reps: String = "",
    selectedRpe: Double = 10.0,
    calculatedMax: Double? = null,
    customPercentage: String = "",
    onWeightChanged: (String) -> Unit = {},
    onRepsChanged: (String) -> Unit = {},
    onRpeChanged: (Double) -> Unit = {},
    onCalculatedMaxChanged: (Double?) -> Unit = {},
    onCustomPercentageChanged: (String) -> Unit = {},
    onSupportDeveloper: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var showRpeMenu by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }

    val rpeValues = OneRepMaxCalculator.getSupportedRpeValues()
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .statusBarsPadding()
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

            OutlinedTextField(
                value = weight,
                onValueChange = { onWeightChanged(it) },
                label = { Text("Weight ($units)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reps,
                onValueChange = { onRepsChanged(it) },
                label = { Text("Reps (1-10)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = showRpeMenu,
                onExpandedChange = {
                    focusManager.clearFocus()
                    showRpeMenu = !showRpeMenu
                }
            ) {
                OutlinedTextField(
                    value = "RPE: $selectedRpe",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Rate of Perceived Exertion") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRpeMenu) },
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
                    expanded = showRpeMenu,
                    onDismissRequest = { showRpeMenu = false }
                ) {
                    rpeValues.reversed().forEach { rpe ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = "RPE $rpe", fontWeight = FontWeight.Bold)
                                    Text(
                                        text = getRpeDescription(rpe),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onRpeChanged(rpe)
                                showRpeMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
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

            Button(
                onClick = {
                    val w = weight.toDoubleOrNull()
                    val r = reps.toIntOrNull()
                    if (w != null && r != null) {
                        onCalculatedMaxChanged(OneRepMaxCalculator.calculateOneRepMax(w, r, selectedRpe))
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

            calculatedMax?.let { max ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
                            text = formatWeight(max, rounding),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = units,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Training Percentages", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customPercentage,
                        onValueChange = { onCustomPercentageChanged(it) },
                        label = { Text("Custom %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    customPercentage.toIntOrNull()?.let { percentage ->
                        if (percentage in 1..100) {
                            val customWeight = formatWeight(max * percentage / 100.0, rounding)
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "$percentage%", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(
                                        text = "$customWeight $units",
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
                    val calculatedWeight = formatWeight(max * percentage / 100.0, rounding)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "$percentage%", fontWeight = FontWeight.Medium)
                        Text(
                            text = "$calculatedWeight $units",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "About This Calculator", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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

            // Extra space so content doesn't hide behind FAB
            Spacer(modifier = Modifier.height(72.dp))
        }

        // Scrim — closes FAB when tapping outside
        if (fabExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { fabExpanded = false }
            )
        }

        // Supporter star + quote footer
        if (isDonated) {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(start = 16.dp, bottom = 16.dp, end = 80.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Supporter",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = quote,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }

        } // end outer Column

        // Speed dial FAB
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedVisibility(
                visible = fabExpanded,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpeedDialItem(label = "Sets Planner") {
                        fabExpanded = false
                        onNavigateToPlanner()
                    }
                    SpeedDialItem(label = "Settings") {
                        fabExpanded = false
                        onNavigateToSettings()
                    }
                    if (!isDonated) {
                        SpeedDialItem(
                            label = "Support Developer",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        ) {
                            fabExpanded = false
                            onSupportDeveloper()
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { fabExpanded = !fabExpanded },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (fabExpanded) Icons.Filled.Close else Icons.Filled.Menu,
                    contentDescription = if (fabExpanded) "Close menu" else "Open menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun SpeedDialItem(label: String, leadingIcon: (@Composable () -> Unit)? = null, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            leadingIcon?.invoke()
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
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
