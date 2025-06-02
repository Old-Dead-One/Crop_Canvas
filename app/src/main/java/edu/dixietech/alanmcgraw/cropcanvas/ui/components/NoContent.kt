package edu.dixietech.alanmcgraw.cropcanvas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme

@Composable
fun NoContent(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.NotInterested,
    title: String = stringResource(R.string.no_content)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy (dimensionResource(R.dimen.small_spacing)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val iconSize = dimensionResource(R.dimen.placeholder_icon_min_size)

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .sizeIn(minWidth = iconSize, minHeight = iconSize)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
private fun NoContentPrev() {
    CropCanvasTheme {
        Surface(Modifier.fillMaxSize()) {
            NoContent()
        }
    }
}