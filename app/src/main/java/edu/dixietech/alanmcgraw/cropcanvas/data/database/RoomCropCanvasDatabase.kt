package edu.dixietech.alanmcgraw.cropcanvas.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import edu.dixietech.alanmcgraw.cropcanvas.data.database.dao.PlotDao
import edu.dixietech.alanmcgraw.cropcanvas.data.database.dao.ProductDao
import edu.dixietech.alanmcgraw.cropcanvas.data.database.dao.ProfileDao
import edu.dixietech.alanmcgraw.cropcanvas.data.database.dao.SeedDao
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProductEntity
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntity
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProfileEntityStub
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.SeedEntity
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.PlotEntity


@Database(
    entities = [ProfileEntityStub::class, SeedEntity::class, ProductEntity::class, PlotEntity::class],
    version = 3,
    exportSchema = false
)
abstract class RoomCropCanvasDatabase: RoomDatabase(), CropCanvasDatabase {

    abstract val profileDao: ProfileDao
    abstract val seedDao: SeedDao
    abstract val productDao: ProductDao
    abstract val plotDao: PlotDao

    override suspend fun getProfile(token: String) = profileDao.readProfile(token)
    override fun observeProfile(name: String) = profileDao.observeProfile(name)
    override suspend fun saveProfile(profile: ProfileEntity) {
        withTransaction {
            profileDao.upsertStub(profile.profile)
            seedDao.upsertSeeds(profile.seeds)
            productDao.upsertProducts(profile.products)
            plotDao.upsertPlots(profile.plots)
        }
    }
}