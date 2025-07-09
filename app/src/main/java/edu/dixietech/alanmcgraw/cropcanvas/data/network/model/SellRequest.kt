package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SellRequest(
    val name: String,
    val amount: Int
) 