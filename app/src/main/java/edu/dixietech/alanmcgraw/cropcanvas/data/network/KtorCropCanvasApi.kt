package edu.dixietech.alanmcgraw.cropcanvas.data.network

import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.AuthResponse
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.CropCanvasError
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlotDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlotResponseDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProductDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProfileDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PurchaseRequest
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ReceiptDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ShopDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.UnexpectedResponseException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun createAccount(username: String): AuthResponse {
        return withContext(context) {
            val response = client.post("https://cropcanvas.dev/profile") {
                header("Content-Type", "application/json")
                setBody(mapOf("name" to username))
            }

            when (response.status.value) {
                201 -> return@withContext response.body()
                401 -> throw response.body<CropCanvasError>()
                else -> throw UnexpectedResponseException()
            }
        }
    }

    override suspend fun getProfile(token: String): ProfileDto {
        return withContext(context) {
            val response = client.get("https://cropcanvas.dev/profile") {
                header("Authorization", "Bearer $token")
                parameter("include_inventory", "true")
            }

            when (response.status.value) {
                200 -> return@withContext response.body()
                401 -> throw response.body<CropCanvasError>()
                else -> throw UnexpectedResponseException()
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

    override suspend fun purchaseSeeds(
        token: String,
        name: String,
        amount: Int
    ): ReceiptDto = withContext(context) {
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

    override suspend fun plantSeeds(token: String, plotId: Int): PlotDto {
        TODO("Not yet implemented")
    }

    override suspend fun harvestCrop(token: String, plotId: Int): PlotDto {
        TODO("Not yet implemented")
    }


    override suspend fun getProducts(token: String): List<ProductDto> {
        TODO("Not yet implemented")
    }
}