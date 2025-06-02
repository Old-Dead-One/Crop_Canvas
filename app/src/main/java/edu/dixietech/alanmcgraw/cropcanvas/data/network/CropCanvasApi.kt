package edu.dixietech.alanmcgraw.cropcanvas.data.network

import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.AuthResponse
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProductDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProfileDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ReceiptDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ShopDto

interface CropCanvasApi {
    suspend fun createAccount(username: String): AuthResponse
    suspend fun getProfile(token: String): ProfileDto

    suspend fun getSeeds(token: String): ShopDto
    suspend fun purchaseSeeds(token: String, name: String, amount: Int): ReceiptDto

    suspend fun getProducts(token: String): List<ProductDto>
}