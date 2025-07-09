package edu.dixietech.alanmcgraw.cropcanvas.data.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.PlotEntity

@Dao
interface PlotDao {
    @Upsert
    fun upsertPlots(plots: List<PlotEntity>)
}