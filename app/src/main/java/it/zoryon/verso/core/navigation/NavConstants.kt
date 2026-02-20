package it.zoryon.verso.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object NavConstants {
    const val HOME_ROUTE = "home"
    const val SETTINGS_ROUTE = "settings"
}

sealed class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : NavItem(
        route = NavConstants.HOME_ROUTE,
        label = "Home",
        icon = Icons.Default.Home
    )

    object Settings : NavItem(
        route = NavConstants.SETTINGS_ROUTE,
        label = "Settings",
        icon = Icons.Default.Settings
    )

    companion object {
        // Iterable list
        val list = listOf(Home, Settings)
    }
}