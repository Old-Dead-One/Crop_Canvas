package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRow
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowCostLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ListRowTimeLabel
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.QuantitySelector
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop.PurchaseState
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme
import edu.dixietech.alanmcgraw.cropcanvas.utils.drawableResource

@Composable
fun PurchaseSeedPrompt(
    state: PurchaseState,
    onPurchase: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableIntStateOf(1) }

    val mediumPadding = dimensionResource(R.dimen.medium_spacing)

    Column(modifier) {
        ListRow(
            image = state.seed.drawableResource(),
            title = state.seed.name,
            labelOne = { ListRowCostLabel(state.seed.price ?: 0) },
            labelTwo = { ListRowTimeLabel(state.seed.growthDuration) }
        )

        if (state is PurchaseState.Failed) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_spacing)),
                modifier = Modifier
                    .padding(horizontal = mediumPadding)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        QuantitySelector(
            quantity = quantity,
            onQuantityChange = { quantity = it },
            modifier = Modifier
                .padding(horizontal = mediumPadding)
        )

        Button(
            onClick = { onPurchase(quantity) },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .padding(horizontal = mediumPadding)
        ) {
            Text(
                text = stringResource(R.string.purchase_seeds),
                modifier = Modifier
                    .weight(1f)
            )

            Text("$${quantity * (state.seed.price ?: 1)}")
        }
    }

    if (state is PurchaseState.Processing) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = mediumPadding)
        )
    }
}

@Preview
@Composable
private fun PurchaseSeedPromptPrev() {
    CropCanvasTheme {
        Surface {
            PurchaseSeedPrompt(
                state = PurchaseState.Detail(
                    seed = Seed.examples.first()
                ),
                onPurchase = {}
            )
        }
    }
}