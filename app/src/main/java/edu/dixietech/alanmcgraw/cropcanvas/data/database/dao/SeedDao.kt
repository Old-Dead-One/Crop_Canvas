package edu.dixietech.alanmcgraw.cropcanvas.data.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.SeedEntity


@Dao
interface SeedDao {
    @Upsert
    suspend fun upsertSeeds(seeds: List<SeedEntity>)
}