package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeedDto(
    val name: String,

    val amount: Int? = null,
    val price: Int? = null,

    @SerialName("growth_duration_seconds")
    val growthDuration: Int
) {
    fun toSeed() = Seed(
        name = name,
        amount = amount,
        price = price,
        growthDuration = growthDuration
    )
}