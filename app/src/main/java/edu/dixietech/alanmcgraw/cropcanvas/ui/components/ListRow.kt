package edu.dixietech.alanmcgraw.cropcanvas.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components.ListRowLabel
import kotlin.time.Duration.Companion.seconds

@Composable
fun ListRow(
    @DrawableRes image: Int,
    title: String,
    modifier: Modifier = Modifier,
    labelOne: @Composable () -> Unit = {},
    labelTwo: @Composable () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Image(
            bitmap = ImageBitmap.imageResource(image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None,
            modifier = Modifier
                .size(100.dp)
                .padding(dimensionResource(R.dimen.medium_spacing))
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            labelOne()
            labelTwo()
        }
    }
}

@Composable
fun ListRowAmountLabel(
    amount: Int,
    modifier: Modifier = Modifier
) {
    ListRowLabel(
        icon = Icons.Outlined.ShoppingBasket,
        value = amount.toString(),
        description = stringResource(R.string.amount_available),
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun ListRowTimeLabel(
    seconds: Int,
    modifier: Modifier = Modifier
) {
    ListRowLabel(
        icon = Icons.Outlined.Timer,
        value = seconds.seconds.toString(),
        description = stringResource(R.string.duration),
        modifier = modifier
    )
}

@Composable
fun ListRowCostLabel(
    cost: Int,
    modifier: Modifier = Modifier
) {
    ListRowLabel(
        icon = Icons.Outlined.MonetizationOn,
        value = cost.toString(),
        description = stringResource(R.string.cost),
        modifier = modifier
    )
}

@Composable
fun ListRowDurationLabel(
    duration: Int,
    modifier: Modifier = Modifier
) {
    ListRowLabel(
        icon = Icons.Outlined.Timer,
        value = duration.seconds.toString(),
        description = stringResource(R.string.duration),
        modifier = modifier
    )
}