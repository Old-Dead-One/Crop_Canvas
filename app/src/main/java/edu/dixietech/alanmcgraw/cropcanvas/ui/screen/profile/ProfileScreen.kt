package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.ErrorScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.LoadingScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components.ProfileDetailScreen

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profileVm: ProfileVm = hiltViewModel()
) {
    val state by profileVm.uiState.collectAsStateWithLifecycle()

    when (val state = state) {
        is ProfileUiState.Loading -> LoadingScreen(modifier)

        is ProfileUiState.Success -> ProfileDetailScreen(
            profile = state.profile,
            onSignOut = profileVm::signOut,
            modifier = modifier
        )

        is ProfileUiState.Error -> ErrorScreen(
            message = state.message,
            modifier = modifier
        )
    }
}