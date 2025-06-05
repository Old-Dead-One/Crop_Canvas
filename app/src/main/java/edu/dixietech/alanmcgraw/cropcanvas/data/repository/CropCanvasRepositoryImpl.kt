package edu.dixietech.alanmcgraw.cropcanvas.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserAuthenticator
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Receipt
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Shop
import edu.dixietech.alanmcgraw.cropcanvas.data.network.CropCanvasApi
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

class CropCanvasRepositoryImpl(
    private val network: CropCanvasApi,
    private val userAuthenticator: UserAuthenticator,
    private val context: CoroutineContext
): CropCanvasRepository {
    override suspend fun getUserProfile() = flow<AsyncResult<Profile>> {
        emit(AsyncResult.Loading())

        try {
            val token = getUserToken()
            val profileDto = network.getProfile(token)

            emit(AsyncResult.Success(profileDto.toProfile()))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    override suspend fun getShop() = flow<AsyncResult<Shop>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            val shopDto = network.getSeeds(token)
            emit(AsyncResult.Success(shopDto.toShop()))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    override suspend fun purchaseSeed(seed: Seed, amount: Int) = flow<AsyncResult<Receipt>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            val receiptDto = network.purchaseSeeds(token, name = seed.name, amount = amount)
            emit(AsyncResult.Success(receiptDto.toReceipt()))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPlots() = flow<AsyncResult<List<Plot>>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            val plotDto = network.getPlots(token)
            val plots = plotDto.map { it.toPlot() }
            emit(AsyncResult.Success(plots))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    override suspend fun getProducts(): Flow<AsyncResult<List<Product>>> {
        TODO("Not yet implemented")
    }

    override suspend fun plantSeeds(plot: Plot): Flow<AsyncResult<Plot>> {
        TODO("Not yet implemented")
    }

    override suspend fun harvestCrop(plot: Plot): Flow<AsyncResult<Plot>> {
        TODO("Not yet implemented")
    }


    private suspend fun getUserToken(): String {
        val token = userAuthenticator.authToken.first()
        return token ?: throw IllegalStateException("User not authenticated")
    }

    companion object {
        const val LOG_TAG = "CropCanvasRepository"
    }
}