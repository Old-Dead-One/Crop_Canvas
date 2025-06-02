package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val name: String,
    val balance: Int,
    val inventory: Inventory
) {
    fun toProfile() = Profile(
        name = name,
        balance = balance,
        seeds = inventory.seeds.map(SeedDto::toSeed),
        products = inventory.products.map(ProductDto::toProduct),
    )

    @Serializable
    data class Inventory(
        val seeds: List<SeedDto>,
        val products: List<ProductDto>
    )
}