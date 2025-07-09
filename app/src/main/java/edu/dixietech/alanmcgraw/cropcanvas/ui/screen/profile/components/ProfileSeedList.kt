package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowAmountLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowTimeLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.NoContent
import edu.dixietech.alanmcgraw.cropcanvas.utils.drawableResource

@Composable
fun ProfileSeedList(
    seeds: List<Seed>,
    modifier: Modifier = Modifier
) {

    if (seeds.isNotEmpty()) {
        LazyColumn(modifier) {
            items(seeds, key = { seed -> seed.name }) { seed ->
                ListRow(
                    image = seed.drawableResource(),
                    title = seed.name,
                    labelOne = { ListRowAmountLabel(amount = seed.amount ?: 0) },
                    labelTwo = { ListRowTimeLabel(seconds = seed.growthDuration) }
                )
                HorizontalDivider()
            }
        }
    } else {
        NoContent(
            title = stringResource(R.string.no_seeds),
            icon = Icons.Default.Grain,
            modifier = modifier
        )
    }
}