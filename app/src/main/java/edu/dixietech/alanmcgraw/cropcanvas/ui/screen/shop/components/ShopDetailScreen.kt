package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Shop
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.CustomTopAppBar
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowCostLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowTimeLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop.PurchaseState
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop.ShopUiState
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme
import edu.dixietech.alanmcgraw.cropcanvas.utils.drawableResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDetailScreen(
    state: ShopUiState.Success,
    onOpenDetail: (Seed) -> Unit,
    onClosedDetail: () -> Unit,
    onPurchase: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mediumSpacing = dimensionResource(R.dimen.medium_spacing)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.purchaseState) {
        if (state.purchaseState is PurchaseState.Purchased) {
            snackbarHostState.showSnackbar(
                message = context.getString(
                    R.string.successfully_purchased,
                    state.purchaseState.seed.name
                ),
                withDismissAction = true
            )
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(Modifier.padding(mediumSpacing)) {
                Text(
                    text = stringResource(R.string.welcome_to_the_shop),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = stringResource(R.string.balance, state.shop.balance),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding
            ) {
                items(state.shop.items) { item ->
                    ListRow(
                        image = item.drawableResource(),
                        title = item.name,
                        labelOne = { ListRowCostLabel(item.price ?: 0) },
                        labelTwo = { ListRowTimeLabel(item.growthDuration) },
                        modifier = Modifier
                            .clickable { onOpenDetail(item) }
                    )
                }
            }

            if (state.purchaseState != null) {
                ModalBottomSheet(
                    onDismissRequest = onClosedDetail,
                    content = {
                        PurchaseSeedPrompt(
                            state = state.purchaseState,
                            numberOwned = state.shop.ownedSeeds.firstOrNull
                            { it.name == state.purchaseState.seed.name } ?.amount ?: 0,
                            balance = state.shop.balance,
                            onPurchase = onPurchase,
                            modifier = Modifier
                                .padding(bottom = mediumSpacing),
                        )
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun ShopScreenDetailPrev() {
    CropCanvasTheme {
        ShopDetailScreen(
            state = ShopUiState.Success(
                shop = Shop(
                    balance = 1000,
                    items = Seed.examples,
                    products = emptyList(),
                    ownedSeeds = emptyList()
                )
            ),
            onPurchase = { },
            onOpenDetail = { },
            onClosedDetail = { }
        )
    }
}