package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Shop
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepository
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ShopUiState {
    object Loading: ShopUiState()
    data class Success(
        val shop: Shop,
        val purchaseState: PurchaseState? = null
        ): ShopUiState()
    data class Error(val message: String): ShopUiState()
}

sealed class PurchaseState(val seed: Seed) {
    class Detail(seed: Seed): PurchaseState(seed)
    class Processing(seed: Seed): PurchaseState(seed)
    class Failed(seed: Seed, val message: String): PurchaseState(seed)
    class Purchased(seed: Seed): PurchaseState(seed)
}

@HiltViewModel
class ShopVm @Inject constructor(
    private val repository: CropCanvasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShopUiState>(ShopUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadShop()
    }

    fun loadShop() {
        viewModelScope.launch {
            repository.getShop().collect { result ->
                _uiState.update { currentState ->
                    when(result) {
                        is AsyncResult.Loading -> currentState
                        is AsyncResult.Success -> {
                            val sortedSeeds = result.result.items.sortedBy { it.price }
                            ShopUiState.Success(result.result.copy(items = sortedSeeds))
                        }

                        is AsyncResult.Error -> ShopUiState.Error(result.message)
                    }
                }
            }
        }
    }

    fun openDetail(seed: Seed) {
        val state = _uiState.value
        if (state !is ShopUiState.Success) return

        _uiState.value = state.copy(
            purchaseState = PurchaseState.Detail(seed)
        )
    }

    fun closeDetail() {
        val state = _uiState.value
        if (state !is ShopUiState.Success) return

        _uiState.value = state.copy(
            purchaseState = null
        )
    }

    fun purchase(quantity: Int) {
        val state = _uiState.value
        if (state !is ShopUiState.Success) return

        val purchaseState = state.purchaseState
        if (purchaseState !is PurchaseState.Detail) return

        viewModelScope.launch {
            repository.purchaseSeed(purchaseState.seed, quantity).collect { result ->
                _uiState.update { currentState ->
                    if (currentState !is ShopUiState.Success) return@update currentState
                    
                    when (result) {
                        is AsyncResult.Loading -> {
                            currentState.copy(
                                purchaseState = PurchaseState.Processing(purchaseState.seed)
                            )
                        }

                        is AsyncResult.Success -> {
                            // Force refresh shop data after purchase
                            forceRefreshShop()
                            
                            currentState.copy(
                                shop = currentState.shop.copy(
                                    balance = result.result.newBalance
                                ),
                                purchaseState = PurchaseState.Purchased(purchaseState.seed)
                            )
                        }

                        is AsyncResult.Error -> {
                            currentState.copy(
                                purchaseState = PurchaseState.Failed(
                                    seed = purchaseState.seed,
                                    message = result.message
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun forceRefreshShop() {
        viewModelScope.launch {
            loadShop()
        }
    }
}