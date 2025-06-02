package edu.dixietech.alanmcgraw.cropcanvas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import edu.dixietech.alanmcgraw.cropcanvas.R

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(dimensionResource(R.dimen.large_spacing))
            .fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}