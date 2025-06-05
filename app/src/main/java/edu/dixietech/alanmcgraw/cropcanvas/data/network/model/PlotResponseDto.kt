package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlotResponseDto(
    @SerialName("old_balance")
    val oldBalance: Int,

    @SerialName("new_balance")
    val newBalance: Int,

    @SerialName("number_of_items_purchased")
    val numberOfItemsPurchased: Int,

    val items: PlotDto
)
