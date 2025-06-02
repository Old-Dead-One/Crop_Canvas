package edu.dixietech.alanmcgraw.cropcanvas.data.repository

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Receipt
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Shop
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.Flow

interface CropCanvasRepository {
    suspend fun getUserProfile(): Flow<AsyncResult<Profile>>

    suspend fun getShop(): Flow<AsyncResult<Shop>>

    suspend fun purchaseSeed(seed: Seed, amount: Int): Flow<AsyncResult<Receipt>>

    suspend fun getProducts(): Flow<AsyncResult<List<Product>>>
}