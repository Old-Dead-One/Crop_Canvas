package edu.dixietech.alanmcgraw.cropcanvas.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot

@Entity(
    tableName = "plots",
    primaryKeys = ["id", "profileName"],
    foreignKeys = [
        ForeignKey(
            ProfileEntityStub::class,
            parentColumns = ["name"],
            childColumns = ["profileName"],
            onDelete = CASCADE
        )
    ]
)

data class PlotEntity(
    val id: String,
    val name: String,
    val size: Int,

    val profileName: String
) {
    fun toPlot() = Plot(
        id = id,
        name = name,
        size = size,
    )
}