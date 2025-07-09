package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.CustomTopAppBar
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.PlotUiState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotListScreen(
    state: PlotUiState.Success,
    onOpenDetail: (Plot) -> Unit,
    onCloseDetail: () -> Unit,
    onPlantSeeds: (Plot, String, Int) -> Unit,
    onHarvestCrop: (Plot) -> Unit,
    modifier: Modifier = Modifier
) {
    val mediumSpacing = dimensionResource(R.dimen.medium_spacing)

    val plots = state.plots



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
                        style = MaterialTheme.typography.bodyLarge
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
                    val currentPlotAction = determinePlotAction(plot)

                    val backgroundColor = when (currentPlotAction) {
                        is PlotAction.Plantable -> Color.Cyan
                        is PlotAction.Harvestable -> Color.Green
                        is PlotAction.PlantedNotReady -> Color.Magenta
                    }


                    val statusText: String
                    val statusColor: Color

                    when (currentPlotAction) {
                        is PlotAction.Plantable -> {
                            statusText = "Ready to Plant"
                            statusColor = Color.Blue
                        }
                        is PlotAction.PlantedNotReady -> {
                            statusText = "Growing"
                            statusColor = Color.Yellow
                        }
                        is PlotAction.Harvestable -> {
                            statusText = "Ready to Harvest"
                            statusColor = Color.Green
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .clickable { onOpenDetail(plot) }
                            .padding(horizontal = mediumSpacing)
                    ) {
                        ListRow(
                            image = plot.drawableResource ?: R.drawable.crop_plot_empty,
                            title = plot.name,
                            labelOne = { Text("Size: ${plot.size}") },
                            labelTwo = {
                                Text(
                                    text = plot.plant?.name ?: "Not planted",
                                    color = statusColor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                        )
                    }
                    HorizontalDivider()
                }
            }

            if (state.activePlotState?.plot != null) {
                ModalBottomSheet(
                    onDismissRequest = onCloseDetail,
                    content = {
                        SelectedPlotDetail(
                            state = state,
                            onPlantSeeds = onPlantSeeds,
                            onHarvestCrop = onHarvestCrop
                        )
                    }
                )
            }
        }
    )
}
