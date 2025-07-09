package edu.dixietech.alanmcgraw.cropcanvas.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserAuthenticator
import edu.dixietech.alanmcgraw.cropcanvas.data.database.CropCanvasDatabase
import edu.dixietech.alanmcgraw.cropcanvas.data.database.entities.toSeedEntity
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
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
            Log.e(LOG_TAG, "getUserProfile: API call #$apiCallCount - Starting flow")
            emit(AsyncResult.Loading())

            try {
                val token = getUserToken()
                Log.d(LOG_TAG, "getUserProfile: Calling API")
                val profileDto = network.getProfile(token)
                Log.d(LOG_TAG, "getUserProfile: API returned profile with ${profileDto.inventory.seeds.size} seeds")

                Log.d(LOG_TAG, "getUserProfile: Saving to database")
                database.saveProfile(profileDto.toProfileEntity(token))
                Log.d(LOG_TAG, "getUserProfile: Database save completed")

                // Get the saved profile from database
                val savedProfile = database.getProfile(token)
                Log.d(LOG_TAG, "getUserProfile: Retrieved from DB - ${savedProfile.profile.name}, seeds: ${savedProfile.seeds.size}")
                
                emit(AsyncResult.Success(savedProfile.toProfile()))
            } catch (e: Exception) {
                Log.e(LOG_TAG, "getUserProfile: Error", e)
                emit(AsyncResult.Error(e.message ?: "Unknown Error"))
            }
        }.flowOn(context)
    }

    // Nuclear option: Always get fresh data from API
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun refreshProfileFromApi(): Profile {
        val token = getUserToken()
        Log.d(LOG_TAG, "refreshProfileFromApi: Fetching fresh data from API")
        
        val freshProfileDto = network.getProfile(token)
        Log.d(LOG_TAG, "refreshProfileFromApi: API returned profile with ${freshProfileDto.inventory.products.size} products")
        Log.d(LOG_TAG, "refreshProfileFromApi: API returned profile with ${freshProfileDto.plots.size} plots")
        freshProfileDto.inventory.products.forEachIndexed { index, product ->
            Log.d(LOG_TAG, "refreshProfileFromApi: API product $index: ${product.name}, amount: ${product.amount}")
        }
        freshProfileDto.plots.forEachIndexed { index, plot ->
            Log.d(LOG_TAG, "refreshProfileFromApi: API plot $index: ${plot.name}, plant: ${plot.plant?.name}, ready: ${plot.plant?.maturationDate}")
        }
        
        // Save fresh data to database
        Log.d(LOG_TAG, "refreshProfileFromApi: Saving fresh data to database")
        database.saveProfile(freshProfileDto.toProfileEntity(token))
        Log.d(LOG_TAG, "refreshProfileFromApi: Fresh data saved to database")
        
        val freshProfile = freshProfileDto.toProfile()
        Log.d(LOG_TAG, "refreshProfileFromApi: Returning fresh profile: ${freshProfile.name}, products: ${freshProfile.products.size}, plots: ${freshProfile.plots.size}")
        freshProfile.plots.forEachIndexed { index, plot ->
            Log.d(LOG_TAG, "refreshProfileFromApi: Profile plot $index: ${plot.name}, plant: ${plot.plant?.name}, ready: ${plot.plant?.isReadyToHarvest()}")
        }
        return freshProfile
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun observeUserProfile(): Flow<Profile> {
        return flow {
            try {
                Log.d(LOG_TAG, "observeUserProfile: Starting to observe profile")
                
                // Try to get fresh data from API
                try {
                    val freshProfile = refreshProfileFromApi()
                    Log.d(LOG_TAG, "observeUserProfile: Emitting fresh data: ${freshProfile.name}, products: ${freshProfile.products.size}, plots: ${freshProfile.plots.size}")
                    emit(freshProfile)
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "observeUserProfile: API failed, trying database", e)
                    
                    // Fallback to database if API fails
                    try {
                        val token = getUserToken()
                        val profileEntity = database.getProfile(token)
                        val profile = profileEntity.toProfile()
                        Log.d(LOG_TAG, "observeUserProfile: Emitting database data: ${profile.name}, products: ${profile.products.size}, plots: ${profile.plots.size}")
                        emit(profile)
                    } catch (dbException: Exception) {
                        Log.e(LOG_TAG, "observeUserProfile: Database also failed, creating mock profile", dbException)
                        
//                        // Create a mock profile for offline testing
//                        val mockProfile = Profile(
//                            name = "Offline User",
//                            balance = 1000.0,
//                            products = listOf(
//                                Product("Carrots", 5),
//                                Product("Wheat", 10)
//                            ),
//                            plots = listOf(
//                                Plot("Plot 1", null),
//                                Plot("Plot 2", null),
//                                Plot("Plot 3", null)
//                            ),
//                            seeds = listOf(
//                                Seed("Carrot Seeds", 3, 2, 10),
//                                Seed("Wheat Seeds", 2, 1, 5)
//                            )
//                        )
//                        Log.d(LOG_TAG, "observeUserProfile: Emitting mock data for offline testing")
//                        emit(mockProfile)
                    }
                }
                
            } catch (e: Exception) {
                Log.e(LOG_TAG, "observeUserProfile: Unexpected error", e)
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
            Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun purchaseSeed(seed: Seed, amount: Int) = flow<AsyncResult<Receipt>> {
        emit(AsyncResult.Loading())
        try {
            val token = getUserToken()
            Log.d(LOG_TAG, "purchaseSeed: Purchasing $amount ${seed.name}")
            
            val receiptDto = network.purchaseSeeds(token, name = seed.name, amount = amount)
            Log.d(LOG_TAG, "purchaseSeed: API call successful")

            // Nuclear option: Always refresh profile from API
            Log.d(LOG_TAG, "purchaseSeed: Refreshing profile from API")
            refreshProfileFromApi()
            Log.d(LOG_TAG, "purchaseSeed: Profile refreshed from API")

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
            val profileEntity = database.getProfile(token)
            
            // Get plots from local database
            val plots = profileEntity.plots.map { it.toPlot() }
            emit(AsyncResult.Success(plots))
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
            emit(AsyncResult.Error(e.message ?: "Unknown Error"))
        }
    }.flowOn(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun plantSeeds(plotId: String, seedName: String, seedAmount: Int): Flow<AsyncResult<Plot>> {
        return flow<AsyncResult<Plot>> {
            emit(AsyncResult.Loading())
            try {
                val token = getUserToken()
                Log.d(LOG_TAG, "plantSeeds: Planting $seedAmount $seedName in plot $plotId")
                
                // Call API to plant seeds
                val plantSeedsDto = network.plantSeeds(
                    token = token,
                    plotId = plotId,
                    seedName = seedName,
                    seedAmount = seedAmount
                )
                Log.d(LOG_TAG, "plantSeeds: API call successful")
                
                // Nuclear option: Always refresh profile from API
                Log.d(LOG_TAG, "plantSeeds: Refreshing profile from API")
                refreshProfileFromApi()
                Log.d(LOG_TAG, "plantSeeds: Profile refreshed from API")
                
                // Find the updated plot from the fresh profile
                val updatedPlot = plantSeedsDto.toPlot()
                
                Log.d(LOG_TAG, "Planting successful: $updatedPlot")
                emit(AsyncResult.Success(updatedPlot))
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
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
                Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
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
                Log.d(LOG_TAG, "harvestCrop: Harvesting plot ${plot.id}")
                
                // Call API to harvest crop
                val harvestedPlotDto = network.harvestCrop(token, plot.id)
                Log.d(LOG_TAG, "harvestCrop: API call successful")
                
                // Nuclear option: Always refresh profile from API
                Log.d(LOG_TAG, "harvestCrop: Refreshing profile from API")
                refreshProfileFromApi()
                Log.d(LOG_TAG, "harvestCrop: Profile refreshed from API")
                
                // Return the harvested plot
                val updatedPlot = harvestedPlotDto.toPlot()
                Log.d(LOG_TAG, "Harvesting successful: $updatedPlot")
                emit(AsyncResult.Success(updatedPlot))
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
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
                Log.d(LOG_TAG, "sellProducts: Selling $amount $productName")
                
                // Call API to sell products
                val sellResponse = network.sellProducts(token, productName, amount)
                Log.d(LOG_TAG, "sellProducts: API call successful")
                
                // Nuclear option: Always refresh profile from API
                Log.d(LOG_TAG, "sellProducts: Refreshing profile from API")
                refreshProfileFromApi()
                Log.d(LOG_TAG, "sellProducts: Profile refreshed from API")
                
                Log.d(LOG_TAG, "Selling successful: $sellResponse")
                emit(AsyncResult.Success(sellResponse.toReceipt()))
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message ?: "Unknown Exception", e)
                emit(AsyncResult.Error(e.message ?: "Unknown Error"))
            }
        }.flowOn(context)
    }

    private suspend fun getUserToken(): String {
        val token = userAuthenticator.authToken.first()
        return token ?: throw IllegalStateException("User not authenticated")
    }

    companion object {
        const val LOG_TAG = "CropCanvasRepository"
    }
}