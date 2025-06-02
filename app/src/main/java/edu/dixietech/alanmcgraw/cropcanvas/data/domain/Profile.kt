package edu.dixietech.alanmcgraw.cropcanvas.data.domain

data class Profile(
    val name: String,
    val balance: Int,

    val seeds: List<Seed>,
    val products: List<Product>
)