package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import android.os.Build
import androidx.annotation.RequiresApi
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class PlantDto(
    val name: String,
    val amount: Int,

    @SerialName("planted_date")
    val plantedDate: String,
    @SerialName("maturation_date")
    val maturationDate: String,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toPlant() = Plant(
        name = name,
        amount = amount,
        plantedDate = Instant.parse(plantedDate).toString(),
        maturationDate = Instant.parse(maturationDate).toString(),
    )
}