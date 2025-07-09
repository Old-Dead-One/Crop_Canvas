package edu.dixietech.alanmcgraw.cropcanvas.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.utils.getDrawableResource

@Entity(
    tableName ="product",
    primaryKeys = ["name", "profileName"],
    foreignKeys = [
        ForeignKey(
            ProfileEntityStub::class,
            parentColumns = ["name"],
            childColumns = ["profileName"],
            onDelete = CASCADE
        )
    ]
)

data class ProductEntity(
    val name: String,
    val amount: Int,
    val worth: Int,

    val profileName: String
) {
    fun toProduct(): Product {
        val imageResource = getDrawableResource(this.name)

        return Product(
            name = name,
            amount = amount,
            worth = worth,
            drawableResource = imageResource
        )
    }
}