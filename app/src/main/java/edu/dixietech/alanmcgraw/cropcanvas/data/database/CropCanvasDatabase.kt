package edu.dixietech.alanmcgraw.cropcanvas.data.database

import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntity
import kotlinx.coroutines.flow.Flow

interface CropCanvasDatabase {
    suspend fun getProfile(token: String): ProfileEntity
    suspend fun saveProfile(profile: ProfileEntity)

    fun observeProfile(name: String): Flow<ProfileEntity>
}