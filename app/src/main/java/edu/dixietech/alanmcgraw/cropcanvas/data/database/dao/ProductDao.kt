package edu.dixietech.alanmcgraw.cropcanvas.data.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.ProductEntity


@Dao
interface ProductDao {
    @Upsert
    suspend fun upsertProducts(products: List<ProductEntity>)
}