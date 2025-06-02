package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme
import edu.dixietech.alanmcgraw.cropcanvas.R

@Composable
fun AuthScreen(
    state: AuthUiState,
    onSignIn: (String) -> Unit,
    onSignUp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var fieldValue by rememberSaveable { mutableStateOf("") }
    var isCreating by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(R.dimen.medium_spacing))
                .fillMaxSize()
        ) {
            Spacer(Modifier)

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge
            )

            when (state) {
                is AuthUiState.Loading -> CircularProgressIndicator()
                is AuthUiState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )

                else -> Spacer(Modifier)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.medium_spacing))
            ) {
                TextField(
                    value = fieldValue,
                    onValueChange = { fieldValue = it },
                    label = {
                        Text(
                            text = if (isCreating) stringResource(R.string.username)
                            else stringResource(R.string.token)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (isCreating) onSignUp(fieldValue)
                        else onSignIn(fieldValue)
                    },
                    enabled = fieldValue.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (isCreating) stringResource(R.string.sign_up)
                        else stringResource(R.string.sign_in)
                    )
                }
            }

            Spacer(Modifier)

            Button(
                onClick = { isCreating = !isCreating }
            ) {
                Text(
                    text = if (isCreating) stringResource(R.string.already_have_an_account)
                    else stringResource(R.string.create_an_account)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AuthScreenPreview() {
    CropCanvasTheme {
        Surface {
            AuthScreen(
                state = AuthUiState.Unauthenticated,
                onSignIn = { },
                onSignUp = { }
            )
        }
    }
}