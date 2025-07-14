package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Plot

@Composable
fun PlotSelectionList(
    plots: List<Plot>,
    onPlotSelected: (Plot) -> Unit,
    selectedPlot: Plot?,
    modifier: Modifier = Modifier
) {
    if (plots.isNotEmpty()) {
        LazyColumn(modifier) {
            items(plots) { plot ->
                val isSelected = plot.id == selectedPlot?.id
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlotSelected(plot) }
                        .background(if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.3f)
                        else Color.Transparent)
                        .padding(16.dp)
                ) {
                    Text(
                        text = plot.name,
                        modifier = Modifier.weight(1f)
                    )
                    plot.price?.let { price ->
                        Text(
                            text = "$${price}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    } else {
        Text("No plots available.")
    }
}