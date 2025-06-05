package edu.dixietech.alanmcgraw.cropcanvas.data.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Plant(
    val name: String,
    val amount: Int?,

    @SerialName("planted_date")
    val plantedDate: String,

    @SerialName("maturation_date")
    val maturationDate: String,
)