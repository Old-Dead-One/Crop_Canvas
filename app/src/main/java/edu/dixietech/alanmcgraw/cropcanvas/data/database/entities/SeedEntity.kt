package edu.dixietech.alanmcgraw.cropcanvas.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed


@Entity(
    tableName ="seed",
    primaryKeys = ["name", "profileName"],
    foreignKeys = [
        ForeignKey(
            ProfileEntityStub::class,
            parentColumns = ["name"],
            childColumns = ["profileName"]
        )
    ]
)

data class SeedEntity(
    val name: String,
    val amount: Int?,
    val price: Int?,
    val growthDuration: Int,

    val profileName: String
) {
    fun toSeed() = Seed(
        name = name,
        amount = amount,
        price = price,
        growthDuration = growthDuration
    )
}

fun Seed.toSeedEntity(profileName: String) = SeedEntity(
    name = name,
    amount = amount,
    price = price,
    growthDuration = growthDuration,
    profileName = profileName
)