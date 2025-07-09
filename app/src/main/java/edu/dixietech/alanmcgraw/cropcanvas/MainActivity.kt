package edu.dixietech.alanmcgraw.cropcanvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import edu.dixietech.alanmcgraw.cropcanvas.ui.navigation.AppNavigation
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.auth.AuthScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.auth.AuthUiState
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.auth.AuthVm
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CropCanvasTheme {
                RootScreen()

            }
        }
    }
}

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    authVm: AuthVm = hiltViewModel()
) {
    val state by authVm.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (state is AuthUiState.Authenticated) {
            AppNavigation()
        } else {
            AuthScreen(
                state = state,
                onSignIn = authVm::signIn,
                onSignUp = authVm::signUp,
            )
        }
    }
}