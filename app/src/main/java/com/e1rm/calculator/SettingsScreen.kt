package com.e1rm.calculator

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    units: String = "kg",
    rounding: String = "default_0_5",
    onUnitsChanged: (String) -> Unit = {},
    onRoundingChanged: (String) -> Unit = {},
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val selectedIncrement = if (rounding.endsWith("2_5")) "2.5" else "0.5"
    val selectedDirection = rounding.substringBefore("_")

    fun emitRounding(increment: String = selectedIncrement, direction: String = selectedDirection) {
        val incKey = if (increment == "2.5") "2_5" else "0_5"
        onRoundingChanged("${direction}_$incKey")
    }

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
        ) {
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Units ────────────────────────────────────────────────────
            Text("Units of Measurement", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("kg", "lbs").forEach { option ->
                    FilterChip(
                        selected = units == option,
                        onClick = { onUnitsChanged(option) },
                        label = { Text(option) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Rounding increment ───────────────────────────────────────
            Text("Rounding Increment", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "The nearest value all calculated weights are rounded to",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("2.5", "0.5").forEach { option ->
                    FilterChip(
                        selected = selectedIncrement == option,
                        onClick = { emitRounding(increment = option) },
                        label = { Text(option) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Rounding direction ───────────────────────────────────────
            Text("Rounding Direction", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Default rounds to the nearest increment",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    "default" to "Default",
                    "up"      to "Always Up",
                    "down"    to "Always Down"
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = selectedDirection == value,
                        onClick = { emitRounding(direction = value) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}
