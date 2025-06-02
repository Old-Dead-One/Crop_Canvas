package edu.dixietech.alanmcgraw.cropcanvas.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable object Auth : Route()
    @Serializable object Profile : Route()
    @Serializable object Shop : Route()
    @Serializable object Plots: Route()
}
data class TopLevelNavigationItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: Route
)
