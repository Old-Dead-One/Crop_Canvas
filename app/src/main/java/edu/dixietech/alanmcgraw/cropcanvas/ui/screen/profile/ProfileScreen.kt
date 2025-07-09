package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ErrorScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.LoadingScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components.ProfileDetailScreen

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileVm: ProfileVm = hiltViewModel()
) {
    val state by profileVm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        profileVm.forceRefreshProfile()
    }

    when (val currentState = state) {
        is ProfileUiState.Loading -> LoadingScreen(modifier)

        is ProfileUiState.Success ->
            ProfileDetailScreen(
                profile = currentState.profile,
                onSignOut = profileVm::signOut,
                onSellProduct = profileVm::sellProducts,
                modifier = modifier
            )

        is ProfileUiState.Error -> ErrorScreen(
            message = currentState.message,
            onTryAgain = { profileVm.forceRefreshProfile() },
            modifier = modifier,
            extraContent = {
                Button(
                    onClick = profileVm::signOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
                { Text(stringResource(R.string.sign_out)) }
            }
        )
    }
}