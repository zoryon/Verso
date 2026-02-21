package it.zoryon.verso.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

object NavConstants {
    const val HOME_ROUTE = "home"
    const val LIBRARY_ROUTE = "library"
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

    object Library : NavItem(
        route = NavConstants.LIBRARY_ROUTE,
        label = "Library",
        icon = Icons.Default.Star
    )

    object Settings : NavItem(
        route = NavConstants.SETTINGS_ROUTE,
        label = "Settings",
        icon = Icons.Default.Settings
    )

    companion object {
        // Iterable list
        val list = listOf(Home, Library, Settings)
    }
}