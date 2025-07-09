package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ErrorScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.LoadingScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components.PlotListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotScreen(
    modifier: Modifier = Modifier,
    plotVm: PlotVm = hiltViewModel()
) {
    val collectedState by plotVm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        plotVm.forceRefreshProfile()
    }

    when (val currentState = collectedState) {
        is PlotUiState.Loading -> LoadingScreen(modifier)
        is PlotUiState.Success -> PlotListScreen(
            state = currentState,
            onOpenDetail = plotVm::onOpenDetail,
            onCloseDetail = plotVm::onCloseDetail,
            onPlantSeeds = { plot, seedName, seedAmount ->
                plotVm.onPlantSeeds(plot, seedName, seedAmount)
            },
            onHarvestCrop = plotVm::onHarvestCrop
        )
        is PlotUiState.Error -> ErrorScreen(
            message = currentState.message,
            onTryAgain = plotVm::forceRefreshProfile,
            modifier)
    }
}