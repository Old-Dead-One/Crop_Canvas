package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.PlotState
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.PlotUiState
import java.time.Duration
import java.time.OffsetDateTime

@RequiresApi(Build.VERSION_CODES.O)
fun calculateTimeRemaining(maturationDate: String): String {
    return try {
        val maturationDateTime = OffsetDateTime.parse(maturationDate)
        val currentDateTime = OffsetDateTime.now()
        
        if (currentDateTime.isAfter(maturationDateTime)) {
            return "Ready to harvest!"
        }
        
        val duration = Duration.between(currentDateTime, maturationDateTime)
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60
        
        when {
            days > 0 -> "$days days, $hours hours"
            hours > 0 -> "$hours hours, $minutes minutes"
            minutes > 0 -> "$minutes minutes"
            else -> "Almost ready!"
        }
    } catch (e: Exception) {
        "Time unknown"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedPlotDetail(
    state: PlotUiState.Success,
    onPlantSeeds: (plot: Plot, seedName: String, seedAmount: Int) -> Unit,
    onHarvestCrop: (Plot) -> Unit,
    modifier: Modifier = Modifier
) {
    // Access state directly instead of capturing locally
    val availableSeeds = state.availableSeeds

    var showSeedSelection by remember { mutableStateOf(false) }
    var selectedSeedToPlant by remember { mutableStateOf<Seed?>(null) }
    // Use state.activePlotState?.plot?.size as the key to ensure it updates when the selected plot changes
    var seedAmountToPlant by remember(state.activePlotState?.plot?.size) { 
        mutableIntStateOf(if ((state.activePlotState?.plot?.size ?: 0) > 0) 1 else 0) 
    }
    val mediumPadding = dimensionResource(R.dimen.medium_spacing)

    Column(Modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(mediumPadding),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ListRow(
                image = state.activePlotState?.plot?.drawableResource ?: R.drawable.crop_plot_empty,
                title = state.activePlotState?.plot?.name ?: "No Plots",
                labelOne = { 
                    Text("Size: ${state.activePlotState?.plot?.size}") 
                },
                labelTwo = { 
                    val plot = state.activePlotState?.plot
                    val plant = plot?.plant
                    val statusText = when {
                        plant == null -> "Not Planted"
                        plant.isReadyToHarvest() -> "Ready to Harvest: ${plant.name}"
                        else -> "Growing: ${plant.name}"
                    }
                    Text(statusText)
                },
                modifier = Modifier.weight(1f)
            )

            PlotActionButton(
                plot = state.activePlotState?.plot,
                onInitiateSeedSelection = {
                    selectedSeedToPlant = null
                    seedAmountToPlant = if ((state.activePlotState?.plot?.size ?: 0) > 0) 1 else 0
                    showSeedSelection = true
                },
                onHarvest = { plotToHarvest ->
                    onHarvestCrop(plotToHarvest)
                },
                modifier = Modifier
                    .padding(mediumPadding)
            )
        }
        // Show harvest confirmation if plot was just harvested
        if (state.activePlotState is PlotState.Harvested) {
            val harvestedState = state.activePlotState
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.large_spacing))
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(mediumPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✅ Successfully harvested ${harvestedState.harvestedCrop}!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "The plot is now ready for new seeds.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Show plant details if plot has a growing plant
        state.activePlotState?.plot?.plant?.let { plant ->
            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.large_spacing))
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(mediumPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val plantIconRes = when (plant.name.lowercase()) {
                        "blueberries seeds" -> R.drawable.blueberries
                        "carrot seeds" -> R.drawable.carrots
                        "cauliflower seeds" -> R.drawable.cauliflower
                        "corn seeds" -> R.drawable.corn
                        "melons seeds" -> R.drawable.melons
                        "peppers seeds" -> R.drawable.peppers
                        "pomegranate seeds" -> R.drawable.pomegranates
                        "potatoes seeds" -> R.drawable.potato
                        "wheat seeds" -> R.drawable.wheat
                        else -> R.drawable.crop_plot_empty
                    }

                    Row {
                        Image(
                            painter = painterResource(plantIconRes),
                            contentDescription = plant.name,
                            modifier = Modifier
                                .size(48.dp)
                                .weight(1f)
                        )
                        Text(
                            text = "Plant: ${plant.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                            .weight(1f)
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(mediumPadding),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "Time Remaining:",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = calculateTimeRemaining(plant.maturationDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (plant.isReadyToHarvest()) Color.Green else Color.Blue
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Status:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = if (plant.isReadyToHarvest()) "Ready" else "Growing",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (plant.isReadyToHarvest()) Color.Green else Color.Blue
                        )
                    }
                }
            }
        }

        if (showSeedSelection) {

            val currentPlot = state.activePlotState?.plot ?: return@Column

            AlertDialog(
                onDismissRequest = {
                    showSeedSelection = false
                    selectedSeedToPlant = null
                },
                title = { Text("Select Seed & Quantity for ${currentPlot.name}") },
                text = {
                    Column {
                        Text("Available Seeds:")
                        SeedSelectionList(
                            seeds = availableSeeds,
                            onSeedSelected = { seed ->
                                selectedSeedToPlant = seed
                                seedAmountToPlant = if (currentPlot.size > 0) {
                                    1.coerceAtMost(currentPlot.size)
                                } else {
                                    0
                                }
                            },
                            selectedSeed = selectedSeedToPlant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        if (selectedSeedToPlant != null) {
                            val maxPossibleSeeds = minOf(currentPlot.size, selectedSeedToPlant!!.amount ?: 0)
                            Text("Quantity (Max: $maxPossibleSeeds):")
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(onClick = { if (seedAmountToPlant > 1) seedAmountToPlant-- },
                                    enabled = seedAmountToPlant > 1
                                ) {
                                    Text("-")
                                }
                                Text(
                                    "$seedAmountToPlant",
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Button(
                                    onClick = { if (seedAmountToPlant < maxPossibleSeeds) seedAmountToPlant++ },
                                    enabled = seedAmountToPlant < maxPossibleSeeds
                                ) {
                                    Text("+")
                                }
                                Spacer(Modifier.weight(1f))
                                Button(
                                    onClick = { seedAmountToPlant = maxPossibleSeeds },
                                    enabled = seedAmountToPlant < maxPossibleSeeds
                                ) {
                                    Text("Max")
                                }
                            }
                        } else {
                            Text(
                                text = "Select a seed to choose quantity.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedSeedToPlant?.let { seed ->
                                if (seedAmountToPlant > 0) {
                                    onPlantSeeds(currentPlot, seed.name, seedAmountToPlant)
                                    showSeedSelection = false
                                }
                            }
                        },
                        enabled = selectedSeedToPlant != null && seedAmountToPlant > 0 && seedAmountToPlant <= minOf(currentPlot.size, selectedSeedToPlant?.amount ?: 0)
                    ) {
                        Text("Confirm Plant")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showSeedSelection = false
                        selectedSeedToPlant = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}