package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ErrorScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.LoadingScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop.components.ShopDetailScreen

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    shopVm: ShopVm = hiltViewModel()
) {
    val state by shopVm.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        shopVm.forceRefreshShop()
    }

    when (val state = state) {
        is ShopUiState.Loading -> LoadingScreen(modifier)
        is ShopUiState.Success -> ShopDetailScreen(
            state = state,
            onOpenDetail = shopVm::openDetail,
            onClosedDetail = shopVm::closeDetail,
            onPurchase = shopVm::purchase
        )
        is ShopUiState.Error -> ErrorScreen(
            message = state.message,
            onTryAgain = shopVm::forceRefreshShop,
            modifier = modifier
        )
    }
}