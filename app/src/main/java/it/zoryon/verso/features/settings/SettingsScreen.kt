package it.zoryon.verso.features.settings

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentScreenState by viewModel.currentScreenState.collectAsState()
    val downloadPath by viewModel.downloadPath.collectAsState()

    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            // Makes write permission constant
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            // Update folder path
            viewModel.updateDownloadPath(it.toString())
        }
    }

    BackHandler(enabled = currentScreenState != SettingsState.MAIN_MENU) {
        viewModel.navigateBack()
    }

    AnimatedContent(
        targetState = currentScreenState,
        transitionSpec = {
            if (targetState == SettingsState.DOWNLOAD_FOLDER) {
                slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { fullWidth -> -fullWidth }
                )
            } else {
                slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { fullWidth -> -fullWidth }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { fullWidth -> fullWidth }
                )
            }
        },
        label = "Settings Navigation"
    ) {state ->
        when (state) {
            SettingsState.MAIN_MENU -> {
                MainMenuSettings(onNavigateToDownload = { viewModel.navigateToDownload() })
            }
            SettingsState.DOWNLOAD_FOLDER -> {
                DownloadFolderSettings(
                    currentPath = downloadPath,
                    onBack = { viewModel.navigateBack() },
                    onPathClick = {
                        directoryPickerLauncher.launch(null)
                    }
                )
            }
        }
    }
}