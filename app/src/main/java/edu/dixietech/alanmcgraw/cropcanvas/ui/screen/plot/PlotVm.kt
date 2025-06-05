package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepository
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PlotUiState {
    object Loading: PlotUiState()
    data class Success(
        val plot: List<Plot>,
        val selectedPlot: Plot? = null,
    ): PlotUiState()
    data class Error(val message: String): PlotUiState()
}

@HiltViewModel
class PlotVm @Inject constructor(
    val repository: CropCanvasRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow<PlotUiState>(PlotUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadPlot()
    }

    fun loadPlot() {
        viewModelScope.launch {
            repository.getPlots().collect { result ->
                _uiState.value = when (result) {
                    is AsyncResult.Loading -> PlotUiState.Loading
                    is AsyncResult.Success -> {
                        val sortedPlots = result.result.sortedBy { it.name }
                        val plot: Plot? = null
                        PlotUiState.Success(sortedPlots, plot)
                    }

                    is AsyncResult.Error -> PlotUiState.Error(result.message)
                }
            }
        }
    }

    fun onOpenDetail(plot: Plot) {
        val state = _uiState.value
        if (state !is PlotUiState.Success) return

        _uiState.value = state.copy(selectedPlot = plot)
    }

    fun onCloseDetail() {
        val state = _uiState.value
        if (state !is PlotUiState.Success) return

        _uiState.value = state.copy(selectedPlot = null)
    }

    fun onPlantSeeds(plot: Plot) {
        val state = _uiState.value
        if (state !is PlotUiState.Success) return

        _uiState.value = state.copy(selectedPlot = plot)
        }

    fun onHarvestCrop(plot: Plot) {
        val state = _uiState.value
        if (state !is PlotUiState.Success) return

        _uiState.value = state.copy(selectedPlot = plot)
    }
}



