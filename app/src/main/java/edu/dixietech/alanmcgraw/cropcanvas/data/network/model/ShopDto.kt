package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Shop
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShopDto(
    val balance: Int,

    @SerialName("number_of_items")
    val numberOfItems: Int?,

    val items: List<SeedDto>
) {
    fun toShop(ownedSeeds: List<Seed>) = Shop(
        balance = balance,
        items = items.map(SeedDto::toSeed),
        products = emptyList(),
        ownedSeeds = ownedSeeds
    )
}