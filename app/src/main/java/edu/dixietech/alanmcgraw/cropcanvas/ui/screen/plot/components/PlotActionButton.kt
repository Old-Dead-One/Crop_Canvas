package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotActionButton(
    plot: Plot?,
    onInitiateSeedSelection: () -> Unit,
    onHarvest: (Plot) -> Unit,
    modifier: Modifier = Modifier
) {

    when (determinePlotAction(plot)) {
        is PlotAction.Plantable -> {
            Button(
                onClick = onInitiateSeedSelection,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = modifier.width(80.dp)
            ) {
                Text(
                    text = "Plant Seeds",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        is PlotAction.Harvestable -> {
            Button(
                onClick = { plot?.let { onHarvest(it) }},
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                modifier = modifier.width(80.dp)
            ) {
                Text(
                    text = "Harvest",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        is PlotAction.PlantedNotReady -> {
            Button(
                onClick = { },
                contentPadding = PaddingValues(0.dp),
                enabled = false,
                modifier = modifier.width(80.dp)
            ) {
                Text(
                    text ="Growing",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

sealed interface PlotAction {
    object Plantable: PlotAction
    object Harvestable: PlotAction
    object PlantedNotReady: PlotAction
}

@RequiresApi(Build.VERSION_CODES.O)
fun determinePlotAction(plot: Plot?): PlotAction {
    if (plot?.plant == null) {
        return PlotAction.Plantable
    }
    return if (plot.plant.isReadyToHarvest()) {
        PlotAction.Harvestable
    } else {
        PlotAction.PlantedNotReady
    }
}
