package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProductEntity
import edu.dixietech.alanmcgraw.cropcanvas.utils.getDrawableResource

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
        drawableResource = getDrawableResource(name),
    )

    fun toProductEntity(profileName: String) = ProductEntity(
        name = name,
        amount = amount,
        worth = worth,
        profileName = profileName,
    )
}