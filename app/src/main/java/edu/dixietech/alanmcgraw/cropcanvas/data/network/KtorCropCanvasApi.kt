package edu.dixietech.alanmcgraw.cropcanvas.data.network

import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.AuthResponse
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.AvailablePlotsResponseDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.CropCanvasError
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlantSeedRequest
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlantSeedsDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlotDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProductDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProfileDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PurchaseRequest
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ReceiptDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.SellRequest
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.SellResponse
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ShopDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.UnexpectedResponseException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class KtorCropCanvasApi(
    private val context: CoroutineContext
) : CropCanvasApi {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000  // 5 seconds - reduced for faster failure
            connectTimeoutMillis = 3000  // 3 seconds
            socketTimeoutMillis = 5000   // 5 seconds
        }
    }

    override suspend fun createAccount(username: String): AuthResponse {
        return withContext(context) {
            try {
                val response = client.post("https://cropcanvas.dev/profile") {
                    header("Content-Type", "application/json")
                    setBody(mapOf("name" to username))
                }

                when (response.status.value) {
                    201 -> return@withContext response.body()
                    401 -> throw response.body<CropCanvasError>()
                    else -> throw UnexpectedResponseException()
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun getProfile(token: String): ProfileDto {
        return withContext(context) {
            try {
                val response = client.get("https://cropcanvas.dev/profile") {
                    header("Authorization", "Bearer $token")
                    parameter("include_inventory", "true")
                    parameter("include_plots", "true")
                }

                when (response.status.value) {
                    200 -> return@withContext response.body()
                    401 -> throw response.body<CropCanvasError>()
                    else -> throw UnexpectedResponseException()
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override suspend fun getSeeds(token: String): ShopDto {
        return withContext(context) {
            val response = client.get("https://cropcanvas.dev/shop/seeds") {
                header("Authorization", "Bearer $token")
            }

            when (response.status.value) {
                200 -> return@withContext response.body()
                401 -> throw response.body<CropCanvasError>()
                else -> throw UnexpectedResponseException()
            }
        }
    }

    override suspend fun purchaseSeeds(token: String, name: String, amount: Int): ReceiptDto = withContext(context) {
        val response = client.put("https://cropcanvas.dev/shop/seeds") {
            header("Authorization", "Bearer $token")
            header("Content-Type", "application/json")
            setBody(PurchaseRequest(name, amount))
        }

        when (response.status.value) {
            200 -> return@withContext response.body()
            400, 401, 500 -> throw response.body<CropCanvasError>()
            else -> throw UnexpectedResponseException()
        }
    }

    override suspend fun getAvailablePlots(token: String): AvailablePlotsResponseDto {
        return withContext(context) {
            val response = client.get("https://cropcanvas.dev/shop/plots") {
                header("Authorization", "Bearer $token")
            }

            when (response.status.value) {
                200 -> return@withContext response.body()
                400, 401, 500 -> throw response.body<CropCanvasError>()
                else -> throw UnexpectedResponseException()
            }
        }
    }

    override suspend fun purchasePlots(token: String, plotId: Int): ReceiptDto = withContext(context) {
        val requestBody = mapOf("amount" to 1)
        
        val response = client.put("https://cropcanvas.dev/shop/plots/$plotId") {
            header("Authorization", "Bearer $token")
            header("Content-Type", "application/json")
            setBody(requestBody)
        }
        
        when (response.status.value) {
            200 -> {
                try {
                    val body = response.body<ReceiptDto>()
                    return@withContext body
                } catch (e: Exception) {
                    throw e
                }
            }
            400, 401, 500 -> {
                val error = response.body<CropCanvasError>()
                throw error
            }
            else -> {
                try {
                    val errorBody = response.body<String>()
                } catch (e: Exception) {
                }
                throw UnexpectedResponseException()
            }
        }
    }

    override suspend fun getPlots(token: String): List<PlotDto> = withContext(context) {
        val response = client.get("https://cropcanvas.dev/plots") {
            header("Authorization", "Bearer $token")
            header("Content-Type", "application/json")
        }

        when (response.status.value) {
            200 -> return@withContext response.body()
            400, 401, 500 -> throw response.body<CropCanvasError>()
            else -> throw UnexpectedResponseException()
        }
    }

    override suspend fun plantSeeds(token: String, plotId: String, name: String, amount: Int): PlantSeedsDto = withContext(context) {
        val response = client.post("https://cropcanvas.dev/plots/plant/$plotId") {
            header("Authorization", "Bearer $token")
            header("Content-Type", "application/json")
            setBody(PlantSeedRequest(name, amount))
        }

        when (response.status.value) {
            200 -> return@withContext response.body<PlantSeedsDto>()
            400, 401, 500 -> throw response.body<CropCanvasError>()
            else -> throw UnexpectedResponseException()
    }
}

    override suspend fun harvestCrop(token: String, plotId: String): PlotDto = withContext(context) {
        val response = client.post("https://cropcanvas.dev/plots/harvest/$plotId") {
            header("Authorization", "Bearer $token")
            header("Content-Type", "application/json")
        }

        when (response.status.value) {
            200 -> return@withContext response.body<PlotDto>()
            400, 401, 500 -> throw response.body<CropCanvasError>()
            else -> throw UnexpectedResponseException()
        }
    }

    override suspend fun getProducts(token: String): List<ProductDto> = withContext(context) {
        val response = client.get("https://cropcanvas.dev/products") {
            header("Authorization", "Bearer $token")
            header("Content-Type", "application/json")
        }

        when (response.status.value) {
            200 -> return@withContext response.body()
            400, 401, 500 -> throw response.body<CropCanvasError>()
            else -> throw UnexpectedResponseException()
        }
    }

    override suspend fun sellProducts(token: String, name: String, amount: Int): SellResponse = withContext(context) {
        val response = client.put("https://cropcanvas.dev/market") {
            header("Authorization", "Bearer $token")
            header("Content-Type", "application/json")
            setBody(SellRequest(name, amount))
        }

        when (response.status.value) {
            200 -> {
                val sellResponse = response.body<SellResponse>()
                return@withContext sellResponse
            }
            400, 401, 500 -> {
                val error = response.body<CropCanvasError>()
                throw error
            }
            else -> {
                throw UnexpectedResponseException()
            }
        }
    }
}