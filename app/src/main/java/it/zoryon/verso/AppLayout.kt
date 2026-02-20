package it.zoryon.verso

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.zoryon.verso.core.navigation.NavConstants
import it.zoryon.verso.core.navigation.NavItem
import it.zoryon.verso.core.ui.components.FloatingBottomBar
import it.zoryon.verso.features.home.HomeScreen
import it.zoryon.verso.features.settings.SettingsScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import it.zoryon.verso.core.navigation.BottomBarViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState

@Composable
fun AppLayout(nav: BottomBarViewModel = viewModel()) {
    val navController = rememberNavController()
    val selectedIndex by nav.selectedIndex.collectAsState()

    Scaffold(
        bottomBar = {
            FloatingBottomBar(
                items = NavItem.list,
                selectedItem = selectedIndex,
                onItemSelected = { index ->
                    val route = NavItem.list[index].route
                    nav.selectTab(index)
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavConstants.HOME_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavConstants.HOME_ROUTE) { HomeScreen(navController) }
            composable(NavConstants.SETTINGS_ROUTE) { SettingsScreen(navController) }
        }
    }
}