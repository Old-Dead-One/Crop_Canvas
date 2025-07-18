package edu.dixietech.alanmcgraw.cropcanvas.data.domain

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Plant(
    val name: String,
    val amount: Int?,

    @SerialName("planted_date")
    val plantedDate: String,

    @SerialName("maturation_date")
    val maturationDate: String,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun isReadyToHarvest(): Boolean {
        return try {
            val maturationDateTime = java.time.OffsetDateTime.parse(maturationDate)
            val currentDateTime = java.time.OffsetDateTime.now()
            
            !currentDateTime.isBefore(maturationDateTime)
        } catch (e: Exception) {
            false
        }
    }
}