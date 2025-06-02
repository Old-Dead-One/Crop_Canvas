package edu.dixietech.alanmcgraw.cropcanvas.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.profile.ProfileScreen
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.shop.ShopScreen
import edu.dixietech.alanmcgraw.cropcanvas.R
import edu.dixietech.alanmcgraw.cropcanvas.ui.screen.plot.PlotScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    val topLevelNavigationItems = listOf(
        TopLevelNavigationItem(
            title = R.string.profile,
            icon = Icons.Default.Person,
            route = Route.Profile
        ),

        TopLevelNavigationItem(
            title = R.string.plots,
            icon = Icons.Default.AllInbox,
            route = Route.Plots
        ),
        TopLevelNavigationItem(
            title = R.string.shop,
            icon = Icons.Default.ShoppingBag,
            route = Route.Shop
    ),

    )

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        modifier = modifier,
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Route.Profile,
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                composable<Route.Profile> {
                    ProfileScreen()
                }

                composable<Route.Shop> {
                    ShopScreen()
                }

                composable<Route.Plots> {
                    PlotScreen()
                }
            }
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavigationBar {
                for (item in topLevelNavigationItems) {
                    NavigationBarItem(
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.hasRoute(item.route::class) } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination
                                popUpTo(navController.graph.findStartDestination().id) {
                                    // Saves state so we can return back to the same state later
                                    saveState = true
                                }

                                // Avoid multiple copies of the same destination when switching
                                launchSingleTop = true

                                // Restore the state of the tab when re-selecting it
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(stringResource(item.title))
                        }
                    )
                }
            }
        }
    )
}