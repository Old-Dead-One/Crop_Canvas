package edu.dixietech.alanmcgraw.cropcanvas.data.domain

data class Shop(
    val balance: Int,
    val items: List<Seed>,
    val ownedSeeds: List<Seed>,
    val products: List<Product>,
)