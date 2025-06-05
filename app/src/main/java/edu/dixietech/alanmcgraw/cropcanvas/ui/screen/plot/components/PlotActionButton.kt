package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface PlotAction {
    object Plantable: PlotAction
    object Harvestable: PlotAction
    object PlantedNotReady: PlotAction
}

@RequiresApi(Build.VERSION_CODES.O)
fun determinePlotAction(plot: Plot?): PlotAction {
    if (plot?.plant == null) return PlotAction.Plantable

    return try {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val matureDate = LocalDate.parse(plot.plant.maturationDate, dateFormatter)
        val currentDate = LocalDate.now()

        if (currentDate.isAfter(matureDate) || currentDate.isEqual(matureDate)) {
            PlotAction.Harvestable
        } else {
            PlotAction.PlantedNotReady
        }
    } catch (e: Exception) {
        PlotAction.Plantable
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotActionButton(
    plot: Plot?,
    onPlantSeeds: (Plot) -> Unit,
    onHarvest: (Plot) -> Unit,
    modifier: Modifier = Modifier
) {
    val action = determinePlotAction(plot)

    when (action) {
        is PlotAction.Plantable -> {
            Button(
                onClick = { plot?.let { onPlantSeeds(it) } },
                modifier = modifier
            ) {
                Text("Plant Seeds")
            }
        }
        is PlotAction.Harvestable -> {
            Button(
                onClick = { plot?.let { onHarvest(it) } },
                modifier = modifier
            ) {
                Text("Harvest")
            }
        }
        is PlotAction.PlantedNotReady -> {
            Button(
                onClick = { },
                enabled = false,
                modifier = modifier,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text("Growing")
            }
        }
    }
}

enum class PlotDisplayStatus(val label: String) {
    PLANTABLE("Plantable"),
    HARVESTABLE("Harvestable"),
    PLANTED_NOT_READY("Planted Not Ready"),
    UNKNOWN("Unknown")
}

@RequiresApi(Build.VERSION_CODES.O)
fun getPlotDisplayStatus(plot: Plot?): PlotDisplayStatus {
    if (plot?.plant == null) return PlotDisplayStatus.PLANTABLE

    return try {
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val matureDate = LocalDate.parse(plot.plant.maturationDate, dateFormatter)
        val currentDate = LocalDate.now()

        if (currentDate.isAfter(matureDate) || currentDate.isEqual(matureDate)) {
            PlotDisplayStatus.HARVESTABLE
        } else {
            PlotDisplayStatus.PLANTED_NOT_READY
        }
        } catch (e: Exception) {
        PlotDisplayStatus.UNKNOWN
    }
}

