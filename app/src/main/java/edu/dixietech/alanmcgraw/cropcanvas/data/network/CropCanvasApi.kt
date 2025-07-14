package edu.dixietech.alanmcgraw.cropcanvas.data.network

import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.AuthResponse
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.AvailablePlotsResponseDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlantSeedsDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.PlotDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProductDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ProfileDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ReceiptDto
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.SellResponse
import edu.dixietech.alanmcgraw.cropcanvas.data.network.model.ShopDto

interface CropCanvasApi {
    suspend fun createAccount(username: String): AuthResponse
    suspend fun getProfile(token: String): ProfileDto

    suspend fun getSeeds(token: String): ShopDto
    suspend fun purchaseSeeds(token: String, name: String, amount: Int): ReceiptDto
    suspend fun getAvailablePlots(token: String): AvailablePlotsResponseDto
    suspend fun purchasePlots(token: String, plotId: Int): ReceiptDto

    suspend fun getPlots(token: String): List<PlotDto>
    suspend fun plantSeeds(token: String, plotId: String, seedName: String, seedAmount: Int): PlantSeedsDto
    suspend fun harvestCrop(token: String, plotId: String): PlotDto

    suspend fun getProducts(token: String): List<ProductDto>
    suspend fun sellProducts(token: String, name: String, amount: Int): SellResponse
}