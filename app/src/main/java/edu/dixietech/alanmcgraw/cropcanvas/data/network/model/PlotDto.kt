package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import android.os.Build
import androidx.annotation.RequiresApi
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.PlotEntity
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import kotlinx.serialization.Serializable

@Serializable
data class PlotDto(
    val id: String,
    val name: String,
    val size: Int,
    val plant: PlantDto? = null,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toPlot() = Plot(
        id = id,
        name = name,
        size = size,
        plant = plant?.toPlant(),
        drawableResource = if (plant != null) R.drawable.crop_plot_planted else R.drawable.crop_plot_empty
    )

    fun toPlotEntity(profileName: String) = PlotEntity(
        id = id,
        name = name,
        size = size,
        profileName = profileName,
    )
}

@Serializable
data class AvailablePlotDto(
    val id: Int,
    val name: String,
    val size: Int,
    val price: Int
) {
    fun toPlot() = Plot(
        id = id.toString(),
        name = name,
        size = size,
        drawableResource = R.drawable.crop_plot_empty,
        price = price
    )
}

@Serializable
data class AvailablePlotsResponseDto(
    val number_of_items: Int,
    val items: List<AvailablePlotDto>,
    val balance: Int
)