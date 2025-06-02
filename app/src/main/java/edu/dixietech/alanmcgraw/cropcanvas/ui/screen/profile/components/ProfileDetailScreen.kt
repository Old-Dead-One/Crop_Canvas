package edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Profile
import edu.dixietech.alanmcgraw.cropcanvas.data.domain.Seed
import edu.dixietech.alanmcgraw.cropcanvas.ui.components.CustomTopAppBar
import edu.dixietech.alanmcgraw.cropcanvas.ui.theme.CropCanvasTheme

@Composable
fun ProfileDetailScreen(
    profile: Profile,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {

    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.seeds),
        stringResource(R.string.products),
        stringResource(R.string.settings)
    )

    val mediumSpacing = dimensionResource(R.dimen.medium_spacing)

    Scaffold(
        topBar = {
            CustomTopAppBar {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .padding(top = mediumSpacing)
                        .padding(horizontal = mediumSpacing)
                )

                Text(
                    text = stringResource(R.string.balance, profile.balance),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = mediumSpacing)
                )

                TabRow(tabIndex) {
                    tabs.forEachIndexed { index, name ->
                        Tab(
                            selected = index == tabIndex,
                            onClick = { tabIndex = index },
                            text = { Text(name) }
                        )
                    }
                }
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                when (tabIndex) {
                    0 -> ProfileSeedList(profile.seeds)
                    1 -> ProfileProductList(profile.products)
                    2 -> ProfileSettings(onSignOut)
                }
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun ProfileDetailScreenPreview() {
    CropCanvasTheme {
        ProfileDetailScreen(
            profile = Profile(
                name = "OldDeadOne",
                balance = 1000,
                seeds = Seed.examples,
                products = emptyList()
            ),
            onSignOut = { }
        )
    }
}