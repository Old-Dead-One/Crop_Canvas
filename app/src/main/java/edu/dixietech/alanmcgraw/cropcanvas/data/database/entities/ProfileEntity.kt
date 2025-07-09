package edu.dixietech.alanmcgraw.cropcanvas.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ProfileEntity(
    @Embedded
    val profile: ProfileEntityStub,

    @Relation(
        parentColumn = "name",
        entityColumn = "profileName"
    )
    val seeds: List<SeedEntity>,

    @Relation(
        parentColumn = "name",
        entityColumn = "profileName"
    )
    val products: List<ProductEntity>,

    @Relation(
        parentColumn = "name",
        entityColumn = "profileName"
    )
    val plots: List<PlotEntity>
) {
    fun toProfile() = profile.toProfile(
        seeds = seeds.map(SeedEntity::toSeed),
        products = products.map(ProductEntity::toProduct),
        plots = plots.map(PlotEntity::toPlot)
    )
}
