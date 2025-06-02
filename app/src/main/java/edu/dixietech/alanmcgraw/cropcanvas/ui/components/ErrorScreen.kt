package edu.dixietech.alanmcgraw.cropcanvas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme

@Composable
fun ErrorScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(dimensionResource(R.dimen.large_spacing))
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_spacing)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val iconMinSize = dimensionResource(R.dimen.placeholder_icon_min_size)

            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .sizeIn(minWidth = iconMinSize, minHeight = iconMinSize)
            )

            Text(
                text = stringResource(R.string.error),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = message,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun ErrorScreenPreview() {
    CropCanvasTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ErrorScreen("An Error Occurred")
        }
    }
}