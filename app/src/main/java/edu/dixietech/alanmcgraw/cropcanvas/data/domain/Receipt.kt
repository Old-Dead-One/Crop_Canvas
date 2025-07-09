package edu.dixietech.alanmcgraw.cropcanvas.data.domain

data class Receipt(
    val oldBalance: Int,
    val newBalance: Int,
    val numberOfItemsPurchased: Int?
)