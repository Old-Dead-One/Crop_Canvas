package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ErrorScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.LoadingScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components.PlotActionButton
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components.PlotListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlotScreen(
    modifier: Modifier = Modifier,
    plotVm: PlotVm = hiltViewModel()
) {
    val state by plotVm.uiState.collectAsStateWithLifecycle()

    when (val state = state) {
        is PlotUiState.Loading -> LoadingScreen(modifier)
        is PlotUiState.Success -> PlotListScreen(
            state = state,
            onOpenDetail = plotVm::onOpenDetail,
            onCloseDetail = plotVm::onCloseDetail,
            onPlantSeeds = plotVm::onPlantSeeds,
            onHarvestCrop = plotVm::onHarvestCrop
        )
        is PlotUiState.Error -> ErrorScreen(state.message, modifier)
    }
}
