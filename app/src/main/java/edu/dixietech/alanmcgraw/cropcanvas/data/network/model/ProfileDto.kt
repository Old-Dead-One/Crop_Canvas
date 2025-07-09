package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import android.os.Build
import androidx.annotation.RequiresApi
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntity
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntityStub
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val name: String,
    val balance: Int,
    val inventory: Inventory,
    val plots: List<PlotDto>
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toProfile() = Profile(
        name = name,
        balance = balance,
        seeds = inventory.seeds.map(SeedDto::toSeed),
        products = inventory.products.map(ProductDto::toProduct),
        plots = this.plots.map(PlotDto::toPlot),
    )

    fun toProfileEntity(token: String) = ProfileEntity(
        profile = ProfileEntityStub(
            name = name,
            balance = balance,
            token = token
        ),
        seeds = inventory.seeds.map { it.toSeedEntity(name) },
        products = inventory.products.map { it.toProductEntity(name) },
        plots = this.plots.map { it.toPlotEntity(name) }
    )

    @Serializable
    data class Inventory(
        val seeds: List<SeedDto>,
        val products: List<ProductDto>
    )
}