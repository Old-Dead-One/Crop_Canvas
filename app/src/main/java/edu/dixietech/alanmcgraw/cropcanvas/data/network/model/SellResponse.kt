package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Receipt
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SellResponse(
    @SerialName("name")
    val name: String? = null,
    
    @SerialName("amount")
    val amount: Int? = null
) {
    fun toReceipt() = Receipt(
        oldBalance = 0,
        newBalance = 0,
        numberOfItemsPurchased = amount
    )
} 