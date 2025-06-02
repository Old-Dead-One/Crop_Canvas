package edu.dixietech.alanmcgraw.cropcanvas.data.domain

import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.SeedDto

data class Shop(
    val balance: Int,
    val items: List<Seed>,
    val products: List<Product>,
)