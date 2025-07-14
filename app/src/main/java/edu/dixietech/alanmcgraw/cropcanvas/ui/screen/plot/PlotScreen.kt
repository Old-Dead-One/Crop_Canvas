package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ErrorScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.LoadingScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components.PlotListScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components.PlotSelectionList

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotScreen(
    modifier: Modifier = Modifier,
    plotVm: PlotVm = hiltViewModel()
) {
    val collectedState by plotVm.uiState.collectAsStateWithLifecycle()
    val plotShopStateValue = plotVm.plotShopUiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        plotVm.forceRefreshProfile()
    }

    when (val currentState = collectedState) {
        is PlotUiState.Loading -> LoadingScreen(modifier)
        is PlotUiState.Success -> {
            PlotListScreen(
                state = currentState,
                onOpenDetail = plotVm::onOpenDetail,
                onCloseDetail = plotVm::onCloseDetail,
                onPlantSeeds = { plot, seedName, seedAmount ->
                    plotVm.onPlantSeeds(plot, seedName, seedAmount)
                },
                onHarvestCrop = plotVm::onHarvestCrop,
                onPurchasePlots = { plotVm.loadPlotShop() }
            )

            when (plotShopStateValue) {
                is PlotShopUiState.Success -> {
                    val purchaseState = plotShopStateValue.purchaseState
                    
                    when (purchaseState) {
                        is PlotPurchaseState.Processing -> {
                            AlertDialog(
                                onDismissRequest = { },
                                title = { Text("Purchasing Plot...") },
                                text = { Text("Please wait while we process your purchase.") },
                                confirmButton = { },
                                dismissButton = { }
                            )
                        }
                        is PlotPurchaseState.Purchased -> {
                            AlertDialog(
                                onDismissRequest = { plotVm.resetPlotShop() },
                                title = { Text("Purchase Successful!") },
                                text = { Text("You have successfully purchased ${purchaseState.plot.name}!") },
                                confirmButton = {
                                    Button(onClick = { plotVm.resetPlotShop() }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = { }
                            )
                        }
                        is PlotPurchaseState.Failed -> {
                            AlertDialog(
                                onDismissRequest = { plotVm.resetPlotShop() },
                                title = { Text("Purchase Failed") },
                                text = { Text(purchaseState.message) },
                                confirmButton = {
                                    Button(onClick = { plotVm.resetPlotShop() }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = { }
                            )
                        }
                        else -> {
                            AlertDialog(
                                onDismissRequest = { plotVm.resetPlotShop() },
                                title = { 
                                    Column {
                                        Text("Select a Plot to Purchase")
                                        Text(
                                            text = "Balance: $${plotShopStateValue.balance}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                text = {
                                    PlotSelectionList(
                                        plots = plotShopStateValue.availablePlots,
                                        onPlotSelected = { plot ->
                                            plotVm.selectPlot(plot)
                                        },
                                        selectedPlot = plotShopStateValue.selectedPlot
                                    )
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            plotShopStateValue.selectedPlot?.let { plot ->
                                                plotVm.purchasePlot(plot)
                                            }
                                        },
                                        enabled = plotShopStateValue.selectedPlot != null && 
                                                 (plotShopStateValue.selectedPlot?.price ?: 0) <= plotShopStateValue.balance
                                    ) {
                                        Text("Purchase")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { plotVm.resetPlotShop() }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }
                    }
                }
                is PlotShopUiState.Loading -> { }
                is PlotShopUiState.Error -> { }
            }
        }
        is PlotUiState.Error -> ErrorScreen(
            message = currentState.message,
            onTryAgain = plotVm::forceRefreshProfile,
            modifier
        )
    }
}