package it.zoryon.verso.features.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.zoryon.verso.core.ui.components.VideoResultRow
import it.zoryon.verso.features.player.GlobalPlayerViewModel

@Composable
fun LibraryScreen(
    navController: NavController,
    globalPlayerViewModel: GlobalPlayerViewModel,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val songs by viewModel.songs.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(songs) { song ->
            VideoResultRow(
                video = song,
                onPlay = { globalPlayerViewModel.playVideo(video = song, contextPlaylist = songs) },
                onDownload = null
            )
        }
    }
}