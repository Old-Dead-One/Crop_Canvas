package edu.dixietech.alanmcgraw.cropcanvas.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserAuthenticator
import edu.dixietech.alanmcgraw.cropcanvas.data.database.CropCanvasDatabase
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Product
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Receipt
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Shop
import edu.dixietech.alanmcgraw.cropcanvas.data.network.CropCanvasApi
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.SeedDto
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

class CropCanvasRepositoryImpl(
    private val network: CropCanvasApi,
    private val userAuthenticator: UserAuthenticator,
    private val database: CropCanvasDatabase,
    private val context: CoroutineContext
): CropCanvasRepository {
    private var apiCallCount = 0
    
    override fun getUserProfile(): Flow<AsyncResult<Profile>> {
        return flow<AsyncResult<Profile>> {
            apiCallCount++
            emit(AsyncResult.Loading())

            try {
                val token = getUserToken()
                val profileDto = network.getProfile(token)

                database.saveProfile(profileDto.toProfileEntity(token))

                // Get the saved profile from database
                val savedProfile = database.getProfile(token)
                
                emit(AsyncResult.Success(savedProfile.toProfile()))
            } catch (e: Exception) {
                emit(AsyncResult.Error(e.message ?: "Unknown Error"))
            }
        }.flowOn(context)
    }

    // Nuclear option: Always get fresh data from API
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun refreshProfileFromApi(): Profile {
        val token = getUserToken()
        
        val freshProfileDto = network.getProfile(token)
        freshProfileDto.inventory.products.forEachIndexed { index, product ->
        }
        freshProfileDto.plots.forEachIndexed { index, plot ->
        }

        database.saveProfile(freshProfileDto.toProfileEntity(token))
        
        val freshProfile = freshProfileDto.toProfile()
        freshProfile.plots.forEachIndexed { index, plot ->
        }
        return freshProfile
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeUserProfile(): Flow<Profile> {
        return flow {
                try {
                    val freshProfile = refreshProfileFromApi()
                    emit(freshProfile)
                } catch (e: Exception) {

                    try {
                        val token = getUserToken()
                        val profileEntity = database.getProfile(token)
                        val profile = profileEntity.toProfile()
                        emit(profile)
                    } catch (_: Exception) {
                    }
                } catch (e: Exception) {
                throw e
            }
        }.flowOn(context)
    }

    override suspend fun getShop() = flow<AsyncResult<Shop>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()

            val (shopDto, profileDto) = coroutineScope {
                val shop = async { network.getSeeds(token) }
                val profile = async { network.getProfile(token) }

                return@coroutineScope shop.await() to profile.await()
            }

            emit(AsyncResult.Success(shopDto.toShop(profileDto.inventory.seeds.map(SeedDto::toSeed))))
        } catch (e: Exception) {
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun purchaseSeed(seed: Seed, amount: Int) = flow<AsyncResult<Receipt>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            val receiptDto = network.purchaseSeeds(token, name = seed.name, amount = amount)
            refreshProfileFromApi()
            emit(AsyncResult.Success(receiptDto.toReceipt()))
        } catch (e: Exception) {
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAvailablePlots() = flow<AsyncResult<Pair<List<Plot>, Int>>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            val availablePlotsResponseDto = network.getAvailablePlots(token)
            val availablePlots = availablePlotsResponseDto.items.map { it.toPlot() }
            emit(AsyncResult.Success(availablePlots to availablePlotsResponseDto.balance))
        } catch (e: Exception) {
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun purchasePlots(plotId: Int) = flow<AsyncResult<Receipt>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            val receiptDto = network.purchasePlots(token, plotId)
            refreshProfileFromApi()
            emit(AsyncResult.Success(receiptDto.toReceipt()))
        } catch (e: Exception) {
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getPlots() = flow<AsyncResult<List<Plot>>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            val profileEntity = database.getProfile(token)
            
            // Get plots from local database
            val plots = profileEntity.plots.map { it.toPlot() }
            emit(AsyncResult.Success(plots))
        } catch (e: Exception) {
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun plantSeeds(plotId: String, seedName: String, seedAmount: Int): Flow<AsyncResult<Plot>> {
        return flow<AsyncResult<Plot>> {
            emit(AsyncResult.Loading())
            try {
                val token = getUserToken()
                val plantSeedsDto = network.plantSeeds(
                    token = token,
                    plotId = plotId,
                    seedName = seedName,
                    seedAmount = seedAmount
                )
                refreshProfileFromApi()
                val updatedPlot = plantSeedsDto.toPlot()
                emit(AsyncResult.Success(updatedPlot))
            } catch (e: Exception) {
                emit(AsyncResult.Error(e.message ?: "Unknown Error"))
            }
        }.flowOn(context)
    }

    override suspend fun getProducts(): Flow<AsyncResult<List<Product>>> {
        return flow<AsyncResult<List<Product>>> {
            emit(AsyncResult.Loading())
            try {
                val token = getUserToken()
                val profileEntity = database.getProfile(token)
                
                // Get products from local database
                val products = profileEntity.products.map { it.toProduct() }
                emit(AsyncResult.Success(products))
            } catch (e: Exception) {
                emit(AsyncResult.Error(e.message ?: "Unknown Error"))
            }
        }.flowOn(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun harvestCrop(plot: Plot): Flow<AsyncResult<Plot>> {
        return flow<AsyncResult<Plot>> {
            emit(AsyncResult.Loading())
            try {
                val token = getUserToken()

                val harvestedPlotDto = network.harvestCrop(token, plot.id)

                refreshProfileFromApi()

                val updatedPlot = harvestedPlotDto.toPlot()
                emit(AsyncResult.Success(updatedPlot))
            } catch (e: Exception) {
                emit(AsyncResult.Error(e.message ?: "Unknown Error"))
            }
        }.flowOn(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun sellProducts(productName: String, amount: Int): Flow<AsyncResult<Receipt>> {
        return flow<AsyncResult<Receipt>> {
            emit(AsyncResult.Loading())
            try {
                val token = getUserToken()
                
                // Call API to sell products
                val sellResponse = network.sellProducts(token, productName, amount)
                refreshProfileFromApi()

                emit(AsyncResult.Success(sellResponse.toReceipt()))
            } catch (e: Exception) {
                emit(AsyncResult.Error(e.message ?: "Unknown Error"))
            }
        }.flowOn(context)
    }

    private suspend fun getUserToken(): String {
        val token = userAuthenticator.authToken.first()
        return token ?: throw IllegalStateException("User not authenticated")
    }
}