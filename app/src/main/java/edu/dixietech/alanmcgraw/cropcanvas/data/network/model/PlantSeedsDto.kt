package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PlantSeedsDto(
    @SerialName("planted_date")
    val plantedDate: String,
    val amount: Int,
    val name: String,
    @SerialName("maturation_date")
    val maturationDate: String
) {
    fun toPlot(): Plot {
        return Plot(
            id = "",
            name = name,
            plant = Plant(
                amount = amount,
                plantedDate = plantedDate,
                maturationDate = maturationDate,
                name = name
            ),
            size = null ?: 0
        )
    }
}