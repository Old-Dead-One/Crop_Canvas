package edu.dixietech.alanmcgraw.cropcanvas.data.repository

import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Receipt
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Shop
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.Flow

interface CropCanvasRepository {
    fun getUserProfile(): Flow<AsyncResult<Profile>>
    
    fun observeUserProfile(): Flow<Profile>

    suspend fun getShop(): Flow<AsyncResult<Shop>>

    suspend fun getAvailablePlots(): Flow<AsyncResult<Pair<List<Plot>, Int>>>

    suspend fun purchaseSeed(seed: Seed, amount: Int): Flow<AsyncResult<Receipt>>

    suspend fun purchasePlots(plotId: Int): Flow<AsyncResult<Receipt>>

    suspend fun getPlots(): Flow<AsyncResult<List<Plot>>>

    suspend fun getProducts(): Flow<AsyncResult<List<Product>>>

    suspend fun plantSeeds(plotId: String, seedName: String, seedAmount: Int): Flow<AsyncResult<Plot>>

    suspend fun harvestCrop(plot: Plot): Flow<AsyncResult<Plot>>
    
    suspend fun sellProducts(productName: String, amount: Int): Flow<AsyncResult<Receipt>>

}