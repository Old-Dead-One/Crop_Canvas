package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import kotlinx.serialization.Serializable

@Serializable
class PlantSeedRequest(
    val name: String,
    val amount: Int
) {
}