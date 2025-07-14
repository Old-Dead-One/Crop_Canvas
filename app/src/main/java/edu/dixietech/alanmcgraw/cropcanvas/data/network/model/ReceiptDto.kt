package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Receipt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDto(

    @SerialName("old_balance")
    val oldBalance : Int,

    @SerialName("new_balance")
    val newBalance : Int,

    @SerialName("number_of_items_purchased")
    val numberOfItemsPurchased: Int,

    @SerialName("items")
    val items: List<PurchasedItemDto> = emptyList(),
) {
    fun toReceipt() = Receipt(
        oldBalance = oldBalance,
        newBalance = newBalance,
        numberOfItemsPurchased = numberOfItemsPurchased
    )
}

@Serializable
data class PurchasedItemDto(
    val id: String,
    val name: String,
    val size: Int
)