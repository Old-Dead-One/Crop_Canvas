package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.CustomTopAppBar
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.PlotUiState
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotListScreen(
    state: PlotUiState.Success,
    onOpenDetail: (Plot) -> Unit,
    onCloseDetail: () -> Unit,
    onPlantSeeds: (Plot) -> Unit,
    onHarvestCrop: (Plot) -> Unit,
    modifier: Modifier = Modifier
) {
    val mediumSpacing = dimensionResource(R.dimen.medium_spacing)

    val plots = state.plot
    val selectedPlot = state.selectedPlot

    Scaffold(
        topBar = {
            CustomTopAppBar(Modifier.padding(mediumSpacing)) {
                Text(
                    text = "Welcome to plots!",
                    style = MaterialTheme.typography.displaySmall
                )

                if (plots.isEmpty()) {
                    Text(
                        text = "You have no plots",
                        style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    Text(
                        text = "You have ${plots.size} plots",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding
            ) {
                items(plots, key = { plot -> plot.id }) { plot ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDetail(plot) }
                            .padding(horizontal = mediumSpacing)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ListRow(
                                image = plot.drawableResource ?: R.drawable.crop_plot_empty,
                                title = plot.name,
                                labelOne = { Text("Size: ${plot.size}") },
                                labelTwo = { Text(plot.plant?.name ?: "Not Planted") }
                            )
                        }

                        PlotActionButton(
                            plot = plot,
                            onPlantSeeds = onPlantSeeds,
                            onHarvest = onHarvestCrop,
                            modifier = Modifier
                                .padding(mediumSpacing)
                        )
                    }
                    HorizontalDivider()
                }
            }

            if (selectedPlot != null) {
                ModalBottomSheet(
                    onDismissRequest = onCloseDetail,
                    content = {
                        SelectedPlotDetail(
                            state = state,
                            onPlantSeeds = { },
                            onHarvestCrop = { },
                            modifier = Modifier
                        )
                    }
                )
            }
        }
    )
}
