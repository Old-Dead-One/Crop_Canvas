package edu.dixietech.alanmcgraw.cropcanvas.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntity
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntityStub
import kotlinx.coroutines.flow.Flow


@Dao
interface ProfileDao {
    @Upsert
    suspend fun upsertStub(stub: ProfileEntityStub)

    @Transaction
    @Query("SELECT * FROM profile WHERE token = :token LIMIT 1")
    suspend fun readProfile(token: String): ProfileEntity

    @Transaction
    @Query("SELECT * FROM profile WHERE name = :name")
    fun observeProfile(name: String): Flow<ProfileEntity>
}