package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import edu.dixietech.alanmcgraw.cropcanvas.R

@Serializable
data class ProductDto(
    val name: String,
    val amount: Int,

    @SerialName("base_worth")
    val worth: Int,
) {
    fun toProduct() = Product(
        name = name,
        amount = amount,
        worth = worth,
        drawableResource = R.drawable.blueberries,
    )
}