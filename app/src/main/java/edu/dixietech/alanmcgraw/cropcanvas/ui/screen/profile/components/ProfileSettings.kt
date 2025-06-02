package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import edu.dixietech.alanmcgraw.cropcanvas.R

@Composable
fun ProfileSettings(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.medium_spacing))
    ) {
        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(R.string.sign_out))
        }
    }
}