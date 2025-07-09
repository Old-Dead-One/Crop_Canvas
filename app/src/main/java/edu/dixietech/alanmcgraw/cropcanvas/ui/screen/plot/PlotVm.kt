package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plant
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepository
import edu.dixietech.alanmcgraw.cropcanvas.utils.AsyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PlotUiState {
    object Loading: PlotUiState()
    data class Success(
        val plots: List<Plot>,
        val availableSeeds: List<Seed> = emptyList(),
        val activePlotState: PlotState? = null

    ): PlotUiState()
    data class Error(val message: String): PlotUiState()
}

sealed class PlotState(open val plot: Plot?) {
    data class Detail(override val plot: Plot): PlotState(plot)
    data class UnPlanted(override val plot: Plot?): PlotState(plot)
    data class Planted(override val plot: Plot): PlotState(plot)
    data class Failed(override val plot: Plot, val message: String): PlotState(plot)
}

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PlotVm @Inject constructor(
    private val repository: CropCanvasRepository,
): ViewModel() {
    
    private val _uiState = MutableStateFlow<PlotUiState>(PlotUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeProfileData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onOpenDetail(plotToShow: Plot) {
        _uiState.update { currentState ->
            if (currentState !is PlotUiState.Success) return@update currentState
            val plotFromState = currentState.plots.find { it.id == plotToShow.id } ?: plotToShow
            val newActivePlotState: PlotState = when {
                plotFromState.plant == null -> PlotState.UnPlanted(plotFromState)
                plotFromState.plant.isReadyToHarvest() -> PlotState.Detail(plotFromState)
                else -> PlotState.Planted(plotFromState)
            }
            currentState.copy(activePlotState = newActivePlotState)
        }
    }

    fun onCloseDetail() {
        _uiState.update { currentState ->
            if (currentState !is PlotUiState.Success) return@update currentState
            currentState.copy(activePlotState = PlotState.UnPlanted(null))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPlantSeeds(plotToPlant: Plot, seedName: String, seedAmount: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (currentState is PlotUiState.Success) {
                    val updatedPlots = currentState.plots.map { plot ->
                        if (plot.id == plotToPlant.id) {
                            val newPlant = createTemporaryPlantedState(seedName, seedAmount)
                            plot.copy(plant = newPlant)
                        } else {
                            plot
                        }
                    }

                    val newActivePlotState = if (currentState.activePlotState?.plot?.id == plotToPlant.id) {
                        val newPlant = createTemporaryPlantedState(seedName, seedAmount)
                        PlotState.Planted(plotToPlant.copy(plant = newPlant))
                    } else {
                        currentState.activePlotState
                    }
                    currentState.copy(plots = updatedPlots, activePlotState = newActivePlotState)
                } else {
                    currentState
                }
            }

            repository.plantSeeds(plotToPlant.id, seedName, seedAmount).collect { result ->
                when (result) {
                    is AsyncResult.Loading -> { }
                    is AsyncResult.Success -> { forceRefreshProfile() }
                    is AsyncResult.Error -> {
                        _uiState.update { currentState ->
                            if (currentState is PlotUiState.Success) {
                                currentState.copy(
                                    activePlotState = PlotState.Failed(
                                        plotToPlant,
                                        result.message
                                    )
                                )
                            } else {
                                PlotUiState.Error(result.message)
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTemporaryPlantedState(seedName: String? = null, seedAmount: Int? = null): Plant {
        val plantedDate = java.time.LocalDate.now().toString()
        val maturationDate = java.time.LocalDate.now().plusDays(1).toString()
        return Plant(
            name = seedName ?: "Planting...",
            amount = seedAmount ?: 1,
            plantedDate = plantedDate,
            maturationDate = maturationDate
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onHarvestCrop(plotToHarvest: Plot) {
        val currentSuccessState = _uiState.value
        if (currentSuccessState !is PlotUiState.Success) return

        val plotFromList = currentSuccessState.plots.find { it.id == plotToHarvest.id }

        if (plotFromList?.plant == null || !plotFromList.plant.isReadyToHarvest()) {
            _uiState.update { currentState ->
                if (currentState is PlotUiState.Success) {
                    currentState.copy(
                        activePlotState = PlotState.Failed(
                            plotToHarvest,
                            "Plot is not ready to harvest."
                        )
                    )
                } else currentState
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { state ->
                if (state is PlotUiState.Success) {
                    state.copy(activePlotState = PlotState.Detail(plotToHarvest))
                } else state
            }
            repository.harvestCrop(plotToHarvest).collect { result ->
                when (result) {
                    is AsyncResult.Loading -> PlotUiState.Loading
                    is AsyncResult.Success -> { forceRefreshProfile() }
                    is AsyncResult.Error -> {
                        _uiState.update { currentState ->
                            if (currentState is PlotUiState.Success) {
                                currentState.copy(
                                    activePlotState = PlotState.Failed(
                                        plotToHarvest,
                                        result.message
                                    )
                                )
                            } else {
                                PlotUiState.Error(result.message)
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeProfileData() {
        viewModelScope.launch {
            repository.observeUserProfile().collect { profile ->
                val previousActivePlot = (_uiState.value as? PlotUiState.Success)?.activePlotState?.plot
                val sortedPlots = profile.plots.sortedBy { it.name }
                val seeds = profile.seeds
                val restoredActivePlot = previousActivePlot?.let { oldPlot ->
                    sortedPlots.find { it.id == oldPlot.id }
                }
                val newActivePlotState = restoredActivePlot?.let {
                    if (it.plant == null) {
                        PlotState.UnPlanted(it)
                    } else {
                        if (it.plant.isReadyToHarvest()) {
                            PlotState.Detail(it)
                        } else {
                            PlotState.Planted(it)
                        }
                    }
                } ?: PlotState.UnPlanted(null)

                val newUiState = PlotUiState.Success(
                    plots = sortedPlots,
                    availableSeeds = seeds,
                    activePlotState = newActivePlotState
                )
                _uiState.update { newUiState }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun forceRefreshProfile() {
        viewModelScope.launch {
            observeProfileData()
        }
    }
}