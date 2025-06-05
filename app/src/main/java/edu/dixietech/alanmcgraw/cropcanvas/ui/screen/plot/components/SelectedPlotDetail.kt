package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.PlotUiState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelectedPlotDetail(
    state: PlotUiState.Success,
    onPlantSeeds: (Plot) -> Unit,
    onHarvestCrop: (Plot) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlot = state.selectedPlot
    val mediumPadding = dimensionResource(R.dimen.medium_spacing)

    Column(Modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(mediumPadding),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ListRow(
                image = selectedPlot?.drawableResource ?: R.drawable.crop_plot_empty,
                title = selectedPlot?.name ?: "No Plots",
                labelOne = { Text("Size: ${selectedPlot?.size}") },
                labelTwo = { Text(selectedPlot?.plant?.name ?: "Not Planted") },
                modifier = Modifier.weight(1f)
            )

            PlotActionButton(
                plot = selectedPlot,
                onPlantSeeds = onPlantSeeds,
                onHarvest = onHarvestCrop,
                modifier = Modifier
                    .padding(mediumPadding)
            )
        }
        selectedPlot?.plant?.let { plant ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(mediumPadding),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.large_spacing))
                .fillMaxWidth()
        ) {
            val plantIconRes = when (plant.name.lowercase()) {
                "blueberries" -> R.drawable.blueberries
                "carrot" -> R.drawable.carrots
                "cauliflower" -> R.drawable.cauliflower
                "corn" -> R.drawable.corn
                "melons" -> R.drawable.melons
                "peppers" -> R.drawable.peppers
                "pomegranates" -> R.drawable.pomegranates
                "potatoes" -> R.drawable.potato
                "wheat" -> R.drawable.wheat
                else -> R.drawable.crop_plot_empty
            }

            Image(
                painter = painterResource(plantIconRes),
                contentDescription = plant.name
            )

            if (plant.amount != null) {
                Text(
                    text = selectedPlot.plant.amount?.toString() ?: "No Amount",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = selectedPlot.plant.plantedDate,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Text(
                    text = selectedPlot.plant.maturationDate,
                    style = MaterialTheme.typography.bodyMedium,

                    )
            }
        }
        }
    }
}