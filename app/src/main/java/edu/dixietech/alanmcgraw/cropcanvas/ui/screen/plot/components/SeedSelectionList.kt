package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed

@Composable
fun SeedSelectionList(
    seeds: List<Seed>,
    onSeedSelected: (Seed) -> Unit,
    selectedSeed: Seed?,
    modifier: Modifier = Modifier
) {
    if (seeds.isNotEmpty()) {
        LazyColumn(modifier) {
            items(seeds.filter { (it.amount ?: 0) > 0 }) { seed ->
                val isSelected = seed.name == selectedSeed?.name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeedSelected(seed) }
                        .background(if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.3f)
                        else Color.Transparent)
                ) {
                    Text(
                        text = (seed.name + " (Qty: ${seed.amount ?: 0})"),
                        style = MaterialTheme.typography.bodyLarge)
                }
                HorizontalDivider()
            }
        }
    } else {
        Text("You have no seeds to plant.")
    }
}