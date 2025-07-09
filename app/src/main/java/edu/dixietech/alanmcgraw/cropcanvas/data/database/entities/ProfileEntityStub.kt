package edu.dixietech.alanmcgraw.cropcanvas.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed

@Entity("profile")
data class ProfileEntityStub(
    @PrimaryKey
    val name: String,
    val balance: Int,
    val token: String
) {
    fun toProfile(seeds: List<Seed>, products: List<Product>, plots: List<Plot>) = Profile(
        name = name,
        balance = balance,
        seeds = seeds,
        products = products,
        plots = plots
    )
}