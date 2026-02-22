package it.zoryon.verso

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.zoryon.verso.core.navigation.NavConstants
import it.zoryon.verso.core.navigation.NavItem
import it.zoryon.verso.core.ui.components.FloatingBottomBar
import it.zoryon.verso.features.home.HomeScreen
import it.zoryon.verso.features.settings.SettingsScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import it.zoryon.verso.core.navigation.BottomBarViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import it.zoryon.verso.core.ui.components.FullPlayer
import it.zoryon.verso.core.ui.components.MiniPlayer
import it.zoryon.verso.core.utils.formatTime
import it.zoryon.verso.features.library.LibraryScreen
import it.zoryon.verso.features.player.GlobalPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayout(
    nav: BottomBarViewModel = viewModel(),
    globalPlayerViewModel: GlobalPlayerViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val selectedIndex by nav.selectedIndex.collectAsState()

    // Observe global player state
    val playerState by globalPlayerViewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            // Screens
            NavHost(
                navController = navController,
                startDestination = NavConstants.HOME_ROUTE,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(NavConstants.HOME_ROUTE) { HomeScreen(navController, globalPlayerViewModel) }
                composable(NavConstants.LIBRARY_ROUTE) { LibraryScreen(navController, globalPlayerViewModel) }
                composable(NavConstants.SETTINGS_ROUTE) { SettingsScreen(navController) }
            }

            // Global Mini Player aligned to the bottom of the screen (above the FloatingBottomBar)
            AnimatedVisibility(
                visible = playerState.currentVideo != null,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                playerState.currentVideo?.let { video ->
                    MiniPlayer(
                        video = video,
                        isPlaying = playerState.isPlaying,
                        onPlayPauseClick = { globalPlayerViewModel.togglePlayPause() },
                        onExpand = { globalPlayerViewModel.setPlayerExpanded(true) }
                    )
                }
            }
        }

        // Full Player Modal (Covers the whole screen including BottomBar when expanded)
        if (playerState.isPlayerExpanded) {
            ModalBottomSheet(
                onDismissRequest = { globalPlayerViewModel.setPlayerExpanded(false) },
                sheetState = sheetState,
                dragHandle = null,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                playerState.currentVideo?.let { video ->
                    FullPlayer(
                        video = video,
                        isPlaying = playerState.isPlaying,
                        position = playerState.playbackPosition,
                        duration = playerState.duration,
                        onSeek = { globalPlayerViewModel.seekTo(it) },
                        onNext = { globalPlayerViewModel.playNext() },
                        onPrevious = { globalPlayerViewModel.playPrevious() },
                        onPlayPauseClick = { globalPlayerViewModel.togglePlayPause() },
                        onCollapse = { globalPlayerViewModel.setPlayerExpanded(false) },
                        formatTime = { formatTime(it) }
                    )
                }
            }
        }
    }
}